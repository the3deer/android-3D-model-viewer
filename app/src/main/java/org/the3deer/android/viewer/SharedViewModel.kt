package org.the3deer.android.viewer

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import org.the3deer.android.engine.ModelEngine
import org.the3deer.android.engine.model.Model
import org.the3deer.android.engine.model.Node
import org.the3deer.android.engine.model.Object3D
import org.the3deer.android.engine.model.Scene
import java.util.LinkedHashMap
import androidx.core.net.toUri
import androidx.core.content.edit
import org.the3deer.android.engine.model.ModelEvent
import org.the3deer.util.event.EventListener
import java.util.EventObject

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(application)

    /**
     * Model
     */
    private val _models = MutableLiveData<MutableMap<String, Model>>(LinkedHashMap())
    private val _activeModel = MutableLiveData<Model>()
    val activeModel: LiveData<Model> = _activeModel

    /**
     * Engine
     */
    private val _engines = MutableLiveData<MutableMap<String, ModelEngine>>(LinkedHashMap())
    private val _activeEngine = MutableLiveData<ModelEngine>()
    var activeEngine: LiveData<ModelEngine> = _activeEngine

    /**
     * Fragment
     */
    private val _activeFragment = MutableLiveData<String>()
    var activeFragment: LiveData<String> = _activeFragment


    private val _modelColor = MutableLiveData<FloatArray>()
    val modelColor: LiveData<FloatArray> = _modelColor

    private val _history = MutableLiveData<List<String>>()
    val history: LiveData<List<String>> = _history

    /**
     * Loading state per URI. Value is the loading message or null if not loading.
     */
    private val _loadingState = MutableLiveData<Map<String, String>>(emptyMap())
    val loadingState: LiveData<Map<String, String>> = _loadingState

    // Simple shapes for testing
    private val triangle = createModelForTest("triangle", floatArrayOf(
        0.0f,  0.622008459f, 0.0f,
       -0.5f, -0.311004243f, 0.0f,
        0.5f, -0.311004243f, 0.0f
    ))

    private val cube = createModelForTest("cube", floatArrayOf(
        // Front face
        -0.3f,  0.3f,  0.3f,   -0.3f, -0.3f,  0.3f,    0.3f, -0.3f,  0.3f,
        -0.3f,  0.3f,  0.3f,    0.3f, -0.3f,  0.3f,    0.3f,  0.3f,  0.3f,
        // Back face
        -0.3f,  0.3f, -0.3f,    0.3f, -0.3f, -0.3f,   -0.3f, -0.3f, -0.3f,
        -0.3f,  0.3f, -0.3f,    0.3f,  0.3f, -0.3f,    0.3f, -0.3f, -0.3f,
        // Top face
        -0.3f,  0.3f, -0.3f,   -0.3f,  0.3f,  0.3f,    0.3f,  0.3f,  0.3f,
        -0.3f,  0.3f, -0.3f,    0.3f,  0.3f,  0.3f,    0.3f,  0.3f, -0.3f,
        // Bottom face
        -0.3f, -0.3f, -0.3f,    0.3f, -0.3f, -0.3f,    0.3f, -0.3f,  0.3f,
        -0.3f, -0.3f, -0.3f,    0.3f, -0.3f,  0.3f,   -0.3f, -0.3f,  0.3f,
        // Left face
        -0.3f,  0.3f, -0.3f,   -0.3f, -0.3f, -0.3f,   -0.3f, -0.3f,  0.3f,
        -0.3f,  0.3f, -0.3f,   -0.3f, -0.3f,  0.3f,   -0.3f,  0.3f,  0.3f,
        // Right face
         0.3f,  0.3f, -0.3f,    0.3f, -0.3f,  0.3f,    0.3f, -0.3f, -0.3f,
         0.3f,  0.3f, -0.3f,    0.3f,  0.3f,  0.3f,    0.3f, -0.3f,  0.3f
    ))

    private val square = createModelForTest("square", floatArrayOf(
        -0.5f,  0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
         0.5f, -0.5f, 0.0f,
        -0.5f,  0.5f, 0.0f,
         0.5f, -0.5f, 0.0f,
         0.5f,  0.5f, 0.0f
    ))

    init {
        // Load history
        val savedHistory = prefs.getString(SharedViewModel::class.java.name+".history", "") ?: ""
        _history.value = if (savedHistory.isEmpty()) emptyList() else savedHistory.split(",")
	
	    // Load initial color from preferences
        //val savedColor = prefs.getString(SharedViewModel::class.java.name+".color", "green") ?: "green"
        //updateModelColor(savedColor)

        // Set initial state
        val savedModel = prefs.getString(SharedViewModel::class.java.name+".active_uri", "triangle") ?: "triangle"
        _activeFragment.value = savedModel
        
        when (savedModel) {
            "cube" -> _activeModel.value = cube
            "square" -> _activeModel.value = square
            "triangle" -> _activeModel.value = triangle
            else -> {
                // For custom URIs, we don't have the model yet, MainActivity will load it
            }
        }
    }

    fun getEngine(uri: String): ModelEngine? {
        return _engines.value?.get(uri)
    }

    /**
     * Creates or retrieves a ModelEngine for the given URI.
     */
    fun loadEngine(uriString: String, model: Model, activity: Activity): ModelEngine {

        val engines = _engines.value!!

        // get engine
        var engine = engines[uriString]

        if (engine == null) {

            // Create the heavy engine instance
            engine = ModelEngine(uriString, model, activity)
            engines[uriString] = engine
            _engines.value = engines

            // Register a listener that updates the loading state
            engine.beanFactory.addOrReplace("sharedViewModelListener", object : EventListener {
                override fun onEvent(event: EventObject): Boolean {
                    if (event is ModelEvent) {
                        when (event.code) {
                            ModelEvent.Code.LOADING -> setLoading(uriString, "Loading...")
                            ModelEvent.Code.PROGRESS -> setLoading(uriString, event.data["message"] as? String?: "Loading...")
                            ModelEvent.Code.LOADED, ModelEvent.Code.LOAD_ERROR -> setLoading(uriString, null)
                            else -> {}
                        }
                    }
                    return false
                }
            })
        }

        if (_activeEngine.value == null) {
            setActiveFragment(uriString)
        }

        updateHistory(uriString)

        return engine;
    }

    private fun setLoading(uri: String, message: String?) {
        val current = _loadingState.value?.toMutableMap() ?: mutableMapOf()
        if (message == null) {
            current.remove(uri)
        } else {
            current[uri] = message
        }
        _loadingState.postValue(current)
    }

    fun getModel(uriString: String) : Model? {
        return _models.value?.get(uriString)
    }

    fun createModel(uriString: String): Model {
        return createModel(uriString, floatArrayOf(0f,0f,0f));
    }

    fun createModelForTest(uriString: String, vertices: FloatArray): Model {

        val model = createModel(uriString, vertices);

        val scene = Scene("Default_${uriString}")
        val node = Node("Root")
        node.mesh = Object3D(vertices)
        scene.rootNodes.add(node)
        model.addScene(scene)

        return model;
    }

    fun createModel(uriString: String, vertices: FloatArray): Model {
        val model = Model(uriString.toUri())

        // register model
        val models = _models.value!!
        models[uriString] = model

        return model;
    }

    fun setActiveFragment(uri: String) {
        Log.i("SharedViewModel", "Setting active engine... uri: $uri")

        _engines.value?.get(uri)?.let {
            _activeEngine.value = it
            Log.i("SharedViewModel", "Setting active engine ok")
        }
        
        this._activeFragment.value = uri;
        
        // Save the last active URI to preferences
        prefs.edit { putString(SharedViewModel::class.java.name+".active_uri", uri) }
    }

    fun loadTriangle() {
        _activeModel.value = triangle
        setActiveFragment("triangle")
        updateHistory("triangle")
    }

    fun loadCube() {
        _activeModel.value = cube
        setActiveFragment("cube")
        updateHistory("cube")
    }

    fun loadSquare() {
        _activeModel.value = square
        setActiveFragment("square")
        updateHistory("square")
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