package org.the3deer.android.viewer.ui.load

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoadViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Select a model to load into the viewer"
    }
    val text: LiveData<String> = _text
}