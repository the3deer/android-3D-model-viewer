package org.the3deer.android.viewer

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.the3deer.android.engine.Model
import org.the3deer.android.viewer.api.AppAPI
import org.the3deer.android.viewer.api.AppAPIImpl
import org.the3deer.android.viewer.providers.ProviderManager
import org.the3deer.android.viewer.settings.AppSettings
import org.the3deer.android.viewer.settings.SettingsManager

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(application)
    val appSettings = AppSettings()
    val settings = SettingsManager(this, appSettings)
    val providerManager = ProviderManager(application)

    val api: AppAPI = AppAPIImpl(
        application,
        settings,
        providerManager,
        onNavigate = { _navigationRequest.postValue(it) },
        onLoadModel = { _loadRequest.postValue(it) },
        onExit = { _exitRequest.postValue(Unit) }
    )

    /**
     * Navigation / UI State
     */
    private val _navigationRequest = MutableLiveData<String>()
    val navigationRequest: LiveData<String> = _navigationRequest

    private val _loadRequest = MutableLiveData<Model>()
    val loadRequest: LiveData<Model> = _loadRequest

    private val _exitRequest = MutableLiveData<Unit>()
    val exitRequest: LiveData<Unit> = _exitRequest

    private val _history = MutableLiveData<List<String>>()
    val history: LiveData<List<String>> = _history

    private val _restartRequest = MutableLiveData<Unit>()
    val restartRequest: LiveData<Unit> = _restartRequest

    init {
        // Load history from AppSettings
        val savedHistory = prefs.getString(appSettings.javaClass.name + ".history", "") ?: ""
        _history.value = if (savedHistory.isEmpty()) {
            emptyList()
        } else if (savedHistory.contains("\n")) {
            savedHistory.split("\n")
        } else {
            savedHistory.split(",")
        }
    }

    /**
     * Update the last active URI and the history.
     */
    fun onModelOpened(model: Model, provider: String?) {
        val uri = model.uri.toString()
        val name = model.name ?: uri
        val type = model.type ?: "gltf"
        
        // Save the last active URI to AppSettings (and persist)
        prefs.edit { putString(appSettings.javaClass.name + ".activeUri", uri) }

        updateHistory(uri, name, type, provider)
    }

    private fun updateHistory(uri: String, name: String, type: String, provider: String?) {
        val currentHistory = _history.value?.toMutableList() ?: mutableListOf()
        
        // Remove existing entries for this URI (checking both old and new format)
        currentHistory.removeAll { it == uri || it.startsWith("$uri|") }
        
        // Add new entry with name, type and provider
        currentHistory.add(0, "$uri|$name|$type|${provider ?: ""}")
        
        val newHistory = currentHistory.take(10)
        _history.value = newHistory
        
        // Save history to AppSettings (and persist)
        prefs.edit { putString(appSettings.javaClass.name + ".history", newHistory.joinToString("\n")) }
    }

    fun removeFromHistory(uri: String) {
        val currentHistory = _history.value?.toMutableList() ?: mutableListOf()
        if (currentHistory.removeAll { it == uri || it.startsWith("$uri|") }) {
            _history.value = currentHistory
            prefs.edit { putString(appSettings.javaClass.name + ".history", currentHistory.joinToString("\n")) }
        }
    }

    fun requestRestart() {
        _restartRequest.postValue(Unit)
    }

}
