package org.the3deer.android.viewer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import androidx.core.content.edit


class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(application)

    /**
     * Navigation / UI State
     */
    private val _activeFragment = MutableLiveData<String>()
    var activeFragment: LiveData<String> = _activeFragment

    private val _modelColor = MutableLiveData<FloatArray>()
    val modelColor: LiveData<FloatArray> = _modelColor

    private val _history = MutableLiveData<List<String>>()
    val history: LiveData<List<String>> = _history

    init {
        // Load history
        val savedHistory = prefs.getString(SharedViewModel::class.java.name+".history", "") ?: ""
        _history.value = if (savedHistory.isEmpty()) emptyList() else savedHistory.split(",")
	
        /*// Set initial state
        val savedModel = prefs.getString(SharedViewModel::class.java.name+".active_uri", "triangle") ?: "triangle"
        _activeFragment.value = savedModel*/
    }

    fun setActiveFragment(uri: String) {

        this._activeFragment.value = uri;
        
        // Save the last active URI to preferences
        prefs.edit { putString(SharedViewModel::class.java.name+".active_uri", uri) }

        updateHistory(uri)
    }


    private fun updateHistory(item: String) {
        val currentHistory = _history.value?.toMutableList() ?: mutableListOf()
        currentHistory.remove(item)
        currentHistory.add(0, item)
        val newHistory = currentHistory.take(10)
        _history.value = newHistory
        
        prefs.edit { putString(SharedViewModel::class.java.name+".history", newHistory.joinToString(",")) }
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
