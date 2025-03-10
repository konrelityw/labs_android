package com.example.lab1_android

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lab1_android.databinding.FragmentResultBinding

class ResultFragment : Fragment() {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_TEXT = "text"
        private const val ARG_FONT_SIZE = "font_size"

        fun newInstance(text: String, fontSize: Float): ResultFragment {
            val fragment = ResultFragment()
            val args = Bundle()
            args.putString(ARG_TEXT, text)
            args.putFloat(ARG_FONT_SIZE, fontSize)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val text = arguments?.getString(ARG_TEXT) ?: ""
        val fontSize = arguments?.getFloat(ARG_FONT_SIZE) ?: 14f

        binding.outputText.text = text
        binding.outputText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)

        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}