package org.the3deer.android.viewer

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.the3deer.android.engine.Model
import org.the3deer.android.viewer.api.AppAPI
import org.the3deer.android.viewer.api.AppAPIImpl
import org.the3deer.android.viewer.providers.ModelMetadataCache
import org.the3deer.android.viewer.providers.ProviderManager
import org.the3deer.android.viewer.settings.AppSettings
import org.the3deer.android.viewer.settings.SettingsManager
import java.net.URI

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(application)
    val appSettings = AppSettings()
    val settings = SettingsManager(this, appSettings)
    val providerManager = ProviderManager(application)
    val metadataCache = ModelMetadataCache(application.cacheDir)

    val api: AppAPI = AppAPIImpl(this)

    /**
     * Navigation / UI State
     */
    private val _navigationRequest = MutableLiveData<String>()
    val navigationRequest: LiveData<String> = _navigationRequest

    private val _loadRequest = MutableLiveData<Model>()
    val loadRequest: LiveData<Model> = _loadRequest

    private val _exitRequest = MutableLiveData<Unit>()
    val exitRequest: LiveData<Unit> = _exitRequest

    private val _history = MutableLiveData<List<Model>>()
    val history: LiveData<List<Model>> = _history

    private val _restartRequest = MutableLiveData<Long?>()
    val restartRequest: LiveData<Long?> = _restartRequest

    init {
        // Load history from AppSettings
        val savedHistory = prefs.getString(appSettings.javaClass.name + ".history", "") ?: ""
        val historyLines = if (savedHistory.isEmpty()) {
            emptyList()
        } else if (savedHistory.contains("\n")) {
            savedHistory.split("\n")
        } else {
            savedHistory.split(",")
        }

        for (item in historyLines) {
            val parts = item.split("|")
            val uriStr = parts[0].trim()
            if (uriStr.isNotBlank()) {
                if (parts.size > 1) {
                    val name = parts.getOrNull(1)?.ifBlank { null }
                    val type = parts.getOrNull(2)?.ifBlank { null }
                    val provider = parts.getOrNull(3)?.ifBlank { null }
                    val location = parts.getOrNull(4)?.ifBlank { null }

                    try {
                        val model = Model(URI.create(uriStr))
                        if (name != null) model.name = name
                        if (type != null) model.type = type
                        if (provider != null) model.provider = provider
                        if (location != null) model.uriModel = URI.create(location)
                        metadataCache.put(model)
                    } catch (e: Exception) {
                        // ignore malformed legacy URI
                    }
                }
            }
        }
        
        refreshHistoryLiveData()
    }

    private fun refreshHistoryLiveData() {
        _history.value = metadataCache.getRecentModels(10)
    }

    fun getActiveUri(): String? {
        return prefs.getString(appSettings.javaClass.name + ".activeUri", null)
    }

    /**
     * Update the last active URI and the history.
     */
    fun onModelOpened(model: Model) {
        val uri = model.uri.toString()

        // Save metadata to persistent .cache_model.json
        metadataCache.put(model)

        // Save the last active URI to AppSettings (and persist)
        prefs.edit { putString(appSettings.javaClass.name + ".activeUri", uri) }

        updateHistory()
    }

    private fun updateHistory() {
        refreshHistoryLiveData()

        // Save history to AppSettings (and persist)
        val uriList = (_history.value ?: emptyList()).map { it.uri.toString() }
        prefs.edit { putString(appSettings.javaClass.name + ".history", uriList.joinToString("\n")) }
    }

    fun removeFromHistory(uri: String) {
        val pureUri = uri.substringBefore("|")
        try {
            metadataCache.remove(URI.create(pureUri))
        } catch (e: Exception) {
            // ignore
        }
        refreshHistoryLiveData()
        val uriList = (_history.value ?: emptyList()).map { it.uri.toString() }
        prefs.edit { putString(appSettings.javaClass.name + ".history", uriList.joinToString("\n")) }
    }

    fun requestRestart() {
        _restartRequest.postValue(System.currentTimeMillis())
    }

    fun clearRestartRequest() {
        _restartRequest.postValue(null)
    }

    fun navigate(screenId: String) {
        _navigationRequest.postValue(screenId)
    }

    fun loadModelByUri(uriString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = URI.create(uriString)
                val model = Model(uri)
                metadataCache.hydrate(model)

                if (model.includes.isEmpty() && !model.provider.isNullOrEmpty()) {
                    val resolved = providerManager.resolveModel(model.provider!!, uri)
                    metadataCache.hydrate(resolved)
                    metadataCache.put(resolved)
                    _loadRequest.postValue(resolved)
                } else {
                    metadataCache.put(model)
                    _loadRequest.postValue(model)
                }
            } catch (e: Exception) {
                try {
                    val model = Model(URI.create(uriString))
                    _loadRequest.postValue(model)
                } catch (e2: Exception) {
                    // ignore
                }
            }
        }
    }

    fun loadModel(model: Model) {
        viewModelScope.launch(Dispatchers.IO) {
            metadataCache.put(model)

            if (model.includes.isEmpty() && !model.provider.isNullOrEmpty()) {
                val hydrated = providerManager.resolveModel(model.provider!!, model.uri)
                metadataCache.hydrate(hydrated)
                metadataCache.put(hydrated)
                _loadRequest.postValue(hydrated)
                return@launch
            }
            _loadRequest.postValue(model)
        }
    }

    fun exitApp() {
        _exitRequest.postValue(Unit)
    }

}
