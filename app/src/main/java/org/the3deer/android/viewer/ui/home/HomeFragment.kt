package org.the3deer.android.viewer.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import org.the3deer.android.engine.ModelEngine
import org.the3deer.android.engine.ModelEngineViewModel
import org.the3deer.android.engine.renderer.GLRenderer
import org.the3deer.android.engine.renderer.GLSurfaceView
import org.the3deer.android.engine.shader.ShaderManager
import org.the3deer.android.viewer.SharedViewModel
import org.the3deer.android.viewer.databinding.FragmentHomeBinding
import org.the3deer.android.viewer.ui.settings.SettingsFragment
import org.the3deer.android.viewer.ui.settings.SettingsOptions
import org.the3deer.util.event.EventListener
import java.util.EventObject

open class HomeFragment : Fragment(), EventListener {

    val TAG: String = HomeFragment::class.java.simpleName

    private var uriString: String = ""
    private var modelName: String = ""
    private var modelType: String = ""
    private var _binding: FragmentHomeBinding? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val modelEngineViewModel: ModelEngineViewModel by activityViewModels()
    protected val handler = Handler(Looper.getMainLooper())


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // debug
        Log.i(TAG, "HomeFragment onCreateView... " + System.identityHashCode(this))

        // check arguments
        uriString = arguments?.getString("uri") ?: throw Exception("No Uri provided as argument")
        modelName = arguments?.getString("name") ?: uriString.split("/").last()
        modelType = arguments?.getString("type") ?: uriString.split(".").last()

        // Get UI binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Get GL Surface (from Layout)
        val glSurfaceView = _binding?.glSurfaceView

        // configure GL Surface
        try {

            // Get desired OpenGL version from preferences
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val openGLVersionKey = ShaderManager::class.java.name + ".openGLVersion"
            val openGLVersion = sharedPreferences.getString(openGLVersionKey, "3")?.toIntOrNull() ?: 3

            // debug
            Log.i(TAG, "Initializing GLSurfaceView with OpenGL ES $openGLVersion ... " + System.identityHashCode(glSurfaceView))

            // Create an OpenGL ES context.
            glSurfaceView?.setEGLContextClientVersion(openGLVersion)
        } catch (e: Exception) {
            Log.w(TAG, "GL ES version not supported, falling back to 2.0. " + e.message)
            try {
                glSurfaceView?.setEGLContextClientVersion(2)
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to set GL ES version 2.0", e2)
            }
        }

        // Set up OpenGL Surface View using the engine's GLRenderer
        glSurfaceView?.setRenderer(GLRenderer(modelEngineViewModel.glScreen, modelEngineViewModel))
        glSurfaceView?.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        Log.i(TAG, "HomeFragment onCreateView finished " + System.identityHashCode(this))
        
        return _binding?.root ?: throw Exception("Failed to inflate layout")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // debug
        Log.i(TAG, "HomeFragment onViewCreated: " + System.identityHashCode(this))

        super.onViewCreated(view, savedInstanceState)

        // Monitor active engine to refresh UI buttons
        modelEngineViewModel.activeEngine.observe(viewLifecycleOwner) { engine ->
            val surface = _binding?.glSurfaceView
            if (surface != null) {
                if (engine == null) {
                    surface.reset()
                    val parent = _binding?.glSurfaceView?.parent as? ViewGroup
                    parent?.removeView(surface)
                    _binding = null
                    Log.i(TAG, "Binding removed: " + uriString)
                } else if (engine.id != uriString){
                    surface.reset()
                    Log.i(TAG, "GL view reset called " + engine.id)

                }
            }
        }

        // Start engine setup
        setupAndStartEngine(uriString)

        Log.i(TAG, "HomeFragment onViewCreated finished " + System.identityHashCode(this))
    }

    /**
     * Load, start and activate the engine for the given URI.
     */
    private fun setupAndStartEngine(uriString : String) {
        try {

            // debug
            Log.i(TAG, "setupAndStartEngine $uriString")

            // Initialize engine view model with this model's metadata
            modelEngineViewModel.initEngine(uriString, modelName, modelType) {

                // get engine
                val engine = modelEngineViewModel.getEngine(uriString)
                if (engine == null){
                    Log.e(TAG, "Engine not initialized")
                    return@initEngine
                }

                // check
                if (_binding == null){
                    Log.e(TAG, "No binding found for id:"+uriString)
                    return@initEngine
                }

                // setup engine with UI/Context components
                engine.addOrReplace("gl.surfaceView", _binding?.glSurfaceView)
                engine.addOrReplace("gl.renderer", _binding?.glSurfaceView?.renderer)
                engine.addOrReplace("ui.settings", SettingsOptions())
                engine.addOrReplace("ui.fragment", this)

                // load engine
                modelEngineViewModel.loadEngine(uriString) {

                    // [SAFE APPLY] Apply saved preferences (Theme, Language, OpenGL settings, etc.)
                    // We use activity?.let to ensure we have a valid context and to skip if detaching
                    activity?.let { activity ->
                        try {
                            SettingsFragment.applySavedPreferences(engine, activity)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error applying saved preferences", e)
                        }
                    }

                    // boot engine
                    modelEngineViewModel.startEngine(uriString) {

                        // log success
                        Log.i(TAG, "setupAndStartEngine Activating engine...")

                        // check status
                        if (engine.status == ModelEngine.Status.OK) {

                            // activate engine if no error
                            modelEngineViewModel.setActiveEngine(uriString)

                            // update shared state (history, etc)
                            sharedViewModel.onModelOpened(uriString)

                            // log success
                            Log.i(TAG, "setupAndStartEngine Engine activated successfully")

                        } else {

                            // log error
                            Log.e(
                                TAG,
                                "setupAndStartEngine Starting engine finished with error: ${engine.message}"
                            )
                        }
                    }
                };
            };
        } catch (ex: Exception) {
            Log.e(TAG, "setupAndStartEngine finished with exception", ex)
        }
    }

    override fun onEvent(event: EventObject?): Boolean {
        // Global events like LOAD_ERROR are now handled by MainActivity via ViewModel observation
        return false
    }
    
    override fun onResume() {
        super.onResume()
        _binding?.glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        _binding?.glSurfaceView?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        
        // Reset engine to clear resources (GPU and memory)
        modelEngineViewModel.resetEngine(uriString)

        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
