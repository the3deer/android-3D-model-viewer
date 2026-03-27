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
import org.the3deer.android.engine.renderer.GLRenderer
import org.the3deer.android.engine.renderer.GLSurfaceView
import org.the3deer.android.viewer.SharedViewModel
import org.the3deer.android.viewer.databinding.FragmentHomeBinding
import org.the3deer.android.viewer.ui.settings.SettingsOptions
import org.the3deer.android.viewer.ui.settings.SettingsFragment

open class HomeFragment : Fragment() {

    val TAG: String = HomeFragment::class.java.getSimpleName()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    protected val handler = Handler(Looper.getMainLooper())

    private var surface: GLSurfaceView? = null
    private val renderer = GLRenderer()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // debug
        Log.i(TAG, "Initializing ModelFragment... " + System.identityHashCode(this))

        // Get UI binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Get GL Surface (from Layout)
        surface = binding.glSurfaceView as GLSurfaceView?
        val glSurfaceView = binding.glSurfaceView as GLSurfaceView

        // debug
        Log.i(TAG, "Initializing GLSurfaceView... " + System.identityHashCode(surface))

        // configure GL Surface
        try {

            // Create an OpenGL ES context.
            glSurfaceView.setEGLContextClientVersion(3)
        } catch (e: Exception) {
            Log.w(
                TAG,
                "GL ES version 3 not supported, falling back to 2.0. " + e.message
            )
            try {
                glSurfaceView.setEGLContextClientVersion(2)
            } catch (e2: Exception) {
                Log.e(
                    TAG,
                    "Failed to set GL ES version 2.0",
                    e2
                )
            }
        }

        // Set up OpenGL Surface View using the engine's GLRenderer
        glSurfaceView.setRenderer(renderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        
        // Ensure buttons stay on top of the GL surface
        //glSurfaceView.setZOrderMediaOverlay(true)

        // debug
        Log.i(TAG, "ModelFragment initialized: " + System.identityHashCode(this))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the active model to trigger engine initialization
        sharedViewModel.activeFragment.observe(viewLifecycleOwner) { uriString ->

            // Try to get the model from the shared view model
            var model = sharedViewModel.getModel(uriString)
            if (model == null) {
                model = sharedViewModel.createModel(uriString);
            }

            // Try to get existing engine or load a new one
            var engine = sharedViewModel.getEngine(uriString)
            if (engine == null) {
                engine = sharedViewModel.loadEngine(uriString, model, requireActivity())
            }

            // setup engine
            handler.post {
                setupEngine(engine)
            }
        }

        // setup engine
        loadEngine(savedInstanceState)
    }

    private fun loadEngine(savedInstanceState: Bundle?) {

        // Get model URI from arguments
        val uriString = arguments?.getString("uri") ?: throw Exception("No model URI provided")
        Log.v(TAG, "Loading engine for fragment: $uriString")

        // Setup engine asynchronously
        handler.post {
            try {

                // debug
                Log.i(TAG, "Loading Engine for ModelFragment... $uriString")

                // Try to get the model from the shared view model
                var model = sharedViewModel.getModel(uriString)
                if (model == null) {
                    model = sharedViewModel.createModel(uriString);
                }

                // Get engine
                var engine = sharedViewModel.getEngine(uriString)

                // Initialize engine
                if (engine == null) {
                    engine = sharedViewModel.loadEngine(uriString, model, requireActivity())
                }

                // Setup engine
                setupEngine(engine)

                // set active engine
                sharedViewModel.setActiveFragment(uriString)

                Log.i(TAG, "Engine connected to GLSurfaceView successfully")
            } catch (ex: Exception) {
                Log.e(TAG, "Error connecting engine", ex)
            }
        }
    }

    private fun setupEngine(engine: ModelEngine) {
        try {

            // Register the GL components in our Engine
            engine.beanFactory.addOrReplace("gl.surfaceView", surface)
            engine.beanFactory.addOrReplace("gl.renderer", renderer)

            // Register UI components
            engine.beanFactory.addOrReplace("ui.settings", SettingsOptions())

            // debug
            Log.i(TAG, "Engine setup finished");

            // boot engine
            engine.isInitialized.let {
                if (!it) {

                    // debug
                    Log.i(TAG, "Initializing Engine... $engine")

                    // configure engine
                    engine.init()

                    // boot engine
                    engine.start()
                }
            }

            // debug
            Log.d(TAG, "Restoring preferences...")

            // apply saved preferences
            SettingsFragment.applySavedPreferences(engine, requireContext())

            // debug
            Log.d(TAG, "Engine setup finished")

        } catch (ex: Exception) {
            Log.e(TAG, "Error setting up engine", ex)
        }
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
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}