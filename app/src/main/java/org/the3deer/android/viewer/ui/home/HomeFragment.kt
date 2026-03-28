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
import org.the3deer.android.viewer.SharedViewModel;
import org.the3deer.android.viewer.databinding.FragmentHomeBinding
import org.the3deer.android.viewer.ui.settings.SettingsOptions
import org.the3deer.android.viewer.ui.settings.SettingsFragment
import androidx.core.view.isVisible
import org.the3deer.android.engine.ModelEngineViewModel

open class HomeFragment : Fragment() {

    val TAG: String = HomeFragment::class.java.getSimpleName()

    private var uriString: String = ""
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
        Log.i(TAG, "Creating HomeFragment... " + System.identityHashCode(this))

        // check arguments
        uriString = arguments?.getString("uri") ?: throw Exception("No Uri provided as argument")

        // Get UI binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize renderer with the engine view model
        renderer = GLRenderer(modelEngineViewModel)

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // debug
        Log.i(TAG, "HomeFragment created: " + System.identityHashCode(this))

        super.onViewCreated(view, savedInstanceState)

        // Observe loading state for this specific URI
        modelEngineViewModel.loadingState.observe(viewLifecycleOwner) { loadingMap ->
            val message = loadingMap[uriString]

            // debug
            Log.v(TAG, "Loading status changed. uri: $uriString, message: $message")

            if (message != null) {
                binding.loadingLayout.visibility = View.VISIBLE
                binding.loadingText.text = message
            } else {
                binding.loadingLayout.visibility = View.GONE
            }

            // debug
            Log.v(TAG, "Dialog visibility: " + (binding.loadingLayout.isVisible))
        }

        // Observe the active engine to trigger setup
        sharedViewModel.activeFragment.observe(viewLifecycleOwner) { activeEngine ->

            activateEngine(uriString);
        }

        sharedViewModel.setActiveFragment(uriString)
    }

    private fun activateEngine(uriString: String) {

        // load engine
        val modelEngine = modelEngineViewModel.loadEngine(uriString)

        // setup engine
        setupEngine(modelEngine)

        // activate engine
        modelEngineViewModel.setActiveEngine(modelEngine);
    }

    private fun setupEngine(engine: ModelEngine) {
        try {

            // debug
            Log.i(TAG, "Setting up Engine... uri: $uriString")

            // Register the GL components in our Engine
            engine.beanFactory.addOrReplace("gl.surfaceView", surface)
            engine.beanFactory.addOrReplace("gl.renderer", renderer)

            // Register UI components
            engine.beanFactory.addOrReplace("ui.settings", SettingsOptions())

            // boot engine
            if (!engine.isInitialized) {

                // initialize engine
                engine.init()

                // debug
                Log.d(TAG, "Setting up HomeFragment...");

                // configure this fragment
                engine.beanFactory.configure(this);
                engine.beanFactory.setUpBean(this);

                // boot engine
                engine.start()
            }

            // apply saved preferences
            SettingsFragment.applySavedPreferences(engine, requireContext())

            // debug
            Log.i(TAG, "Engine setup finished")

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
