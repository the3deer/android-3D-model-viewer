package org.the3deer.android.viewer

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager


class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(application)

    /**
     * Navigation / UI State
     */
    private val _modelColor = MutableLiveData<FloatArray>()
    val modelColor: LiveData<FloatArray> = _modelColor

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
    fun onModelOpened(uri: String, name: String, type: String) {
        
        // Save the last active URI to preferences
        prefs.edit { putString(SharedViewModel::class.java.name+".active_uri", uri) }

        updateHistory(uri, name, type)
    }

    private fun updateHistory(uri: String, name: String, type: String) {
        val currentHistory = _history.value?.toMutableList() ?: mutableListOf()
        
        // Remove existing entries for this URI (checking both old and new format)
        currentHistory.removeAll { it == uri || it.startsWith("$uri|") }
        
        // Add new entry with name and type
        currentHistory.add(0, "$uri|$name|$type")
        
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

    fun updateModelColor(color: String) {
        _modelColor.value = when (color) {
            "red" -> floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)
            "green" -> floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f)
            "blue" -> floatArrayOf(0.0f, 0.0f, 1.0f, 1.0f)
            else -> floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
        }
        // Save the color choice to preferences
        prefs.edit { putString(SharedViewModel::class.java.name+".color", color) }
    }

}
