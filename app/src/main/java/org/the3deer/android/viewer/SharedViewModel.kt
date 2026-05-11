package org.the3deer.android.viewer

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import org.the3deer.android.engine.Model
import org.the3deer.android.viewer.ai.AppAPI
import org.the3deer.android.viewer.providers.*
import java.net.URI

class SharedViewModel(application: Application) : AndroidViewModel(application), AppAPI {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(application)
    private val remoteConfig = FirebaseRemoteConfig.getInstance()

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

    init {
        // Load history
        val savedHistory = prefs.getString(SharedViewModel::class.java.name+".history", "") ?: ""
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
        
        // Save the last active URI to preferences
        prefs.edit { putString(SharedViewModel::class.java.name+".active_uri", uri) }

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
        
        // Use newline as separator to avoid issues with commas in URIs
        prefs.edit { putString(SharedViewModel::class.java.name+".history", newHistory.joinToString("\n")) }
    }

    fun removeFromHistory(uri: String) {
        val currentHistory = _history.value?.toMutableList() ?: mutableListOf()
        if (currentHistory.removeAll { it == uri || it.startsWith("$uri|") }) {
            _history.value = currentHistory
            prefs.edit { putString(SharedViewModel::class.java.name+".history", currentHistory.joinToString("\n")) }
        }
    }

    // --- AppAPI Implementation ---

    override fun navigate(screenId: String) {
        _navigationRequest.postValue(screenId)
    }

    override fun loadModel(model: Model) {
        if (model.uriModel == null && !model.provider.isNullOrEmpty()) {
            // Re-hydrate model to get absolute URLs and included files
            val hydrated = resolveModel(model.provider!!, model.uri)
            if (hydrated != null) {
                _loadRequest.postValue(hydrated)
                return
            }
        }
        _loadRequest.postValue(model)
    }

    override fun setAppSetting(key: String, value: Any) {
        prefs.edit {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
            }
        }
    }

    override fun exitApp() {
        _exitRequest.postValue(Unit)
    }

    override fun getProviders(): List<String> {
        return listOf("Assets", "Khronos", "Poly Haven", "the3deer")
    }

    override fun listModels(provider: String): Any {
        return getProvider(provider)?.list() ?: emptyList<URI>()
    }

    override fun resolveModel(provider: String, uri: URI): Model? {
        return getProvider(provider)?.resolve(uri)
    }

    private fun getProvider(provider: String): ModelProvider? {
        return when (provider.lowercase()) {
            "assets" -> AssetsModelProvider(getApplication())
            "khronos" -> KhronosModelProvider()
            "poly haven", "polyhaven" -> PolyHavenModelProvider()
            "the3deer" -> RepositoryModelProvider()
            else -> null
        }
    }
}
