package org.the3deer.android.viewer.ui.slideshow

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import org.the3deer.android.engine.renderer.SceneRenderer
import org.the3deer.android.viewer.SharedViewModel
import org.the3deer.dddmodel2.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up OpenGL Surface View using the engine's SceneRenderer
        binding.glSurfaceView.setEGLContextClientVersion(2)
        val renderer = SceneRenderer()
        binding.glSurfaceView.setRenderer(renderer)
        binding.glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        sharedViewModel.activeModel.observe(viewLifecycleOwner) { model ->
            renderer.updateModel(model)
            binding.glSurfaceView.requestRender()
        }

        sharedViewModel.modelColor.observe(viewLifecycleOwner) { color ->
            renderer.updateColor(color)
            binding.glSurfaceView.requestRender()
        }

        slideshowViewModel.text.observe(viewLifecycleOwner) {
            binding.textSlideshow.text = it
        }
        return root
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
        _binding = null
    }
}