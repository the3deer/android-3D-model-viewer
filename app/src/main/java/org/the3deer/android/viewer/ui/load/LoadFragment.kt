package org.the3deer.android.viewer.ui.load

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import org.the3deer.android.viewer.SharedViewModel
import org.the3deer.android.viewer.databinding.FragmentLoadBinding

class LoadFragment : DialogFragment() {

    private var _binding: FragmentLoadBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
        // Make the dialog wider
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loadViewModel =
            ViewModelProvider(this).get(LoadViewModel::class.java)

        _binding = FragmentLoadBinding.inflate(inflater, container, false)
        val root: View = binding.root

        loadViewModel.text.observe(viewLifecycleOwner) {
            binding.textLoad.text = it
        }

        binding.buttonLoadTriangle.setOnClickListener {
            sharedViewModel.loadTriangle()
            dismiss()
        }

        binding.buttonLoadCube.setOnClickListener {
            sharedViewModel.loadCube()
            dismiss()
        }

        binding.buttonLoadSquare.setOnClickListener {
            sharedViewModel.loadSquare()
            dismiss()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}