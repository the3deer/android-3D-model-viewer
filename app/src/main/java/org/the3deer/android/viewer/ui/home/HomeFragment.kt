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
import org.the3deer.android.engine.ModelEngine
import org.the3deer.android.engine.ModelEngineViewModel
import org.the3deer.android.engine.renderer.GLRenderer
import org.the3deer.android.engine.renderer.GLSurfaceView
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
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val modelEngineViewModel: ModelEngineViewModel by activityViewModels()
    
    protected val handler = Handler(Looper.getMainLooper())

    private var surface: GLSurfaceView? = null
    private lateinit var renderer: GLRenderer

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

        // Initialize renderer with the engine view model
        renderer = GLRenderer(modelEngineViewModel)

        // Get GL Surface (from Layout)
        surface = binding.glSurfaceView
        val glSurfaceView = binding.glSurfaceView

        // debug
        Log.i(TAG, "Initializing GLSurfaceView... " + System.identityHashCode(surface))

        // configure GL Surface
        try {
            // Create an OpenGL ES context.
            glSurfaceView.setEGLContextClientVersion(3)
        } catch (e: Exception) {
            Log.w(TAG, "GL ES version 3 not supported, falling back to 2.0. " + e.message)
            try {
                glSurfaceView.setEGLContextClientVersion(2)
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to set GL ES version 2.0", e2)
            }
        }

        // Set up OpenGL Surface View using the engine's GLRenderer
        glSurfaceView.setRenderer(renderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        Log.i(TAG, "HomeFragment onCreateView finished " + System.identityHashCode(this))
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // debug
        Log.i(TAG, "HomeFragment onViewCreated: " + System.identityHashCode(this))

        super.onViewCreated(view, savedInstanceState)

        // Note: Loading state observation moved to MainActivity for better lifecycle management

        // Observe the active engine to trigger setup
        sharedViewModel.activeFragment.observe(viewLifecycleOwner) { uriString ->
            setupAndStartEngine(uriString)
        }

        handler.post { setupAndStartEngine(uriString) }

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
            modelEngineViewModel.initEngine(uriString, modelName, modelType, {

                // get engine
                val engine = modelEngineViewModel.getEngine(uriString)
                    ?: throw IllegalArgumentException("Engine not initialized")

                // setup engine with UI/Context components
                engine.add("gl.surfaceView", surface)
                engine.add("gl.renderer", renderer)
                engine.add("ui.settings", SettingsOptions())
                engine.add("ui.fragment", this)

                // load engine
                modelEngineViewModel.loadEngine(uriString, {

                    // apply saved preferences
                    SettingsFragment.applySavedPreferences(engine, requireContext())

                    // boot engine
                    modelEngineViewModel.startEngine(uriString, {

                        // check status
                        if (engine.status == ModelEngine.Status.OK) {

                            // activate engine if no error
                            modelEngineViewModel.setActiveEngine(uriString)

                            // log success
                            Log.i(TAG, "setupAndStartEngine finished successfully")

                        } else {

                            // log error
                            Log.e(TAG, "setupAndStartEngine finished with error: ${engine.message}")
                        }
                    })
                });
            });
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
        binding.glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.glSurfaceView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        
        // Reset engine to clear resources (GPU and memory)
        modelEngineViewModel.resetEngine(uriString)

        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
