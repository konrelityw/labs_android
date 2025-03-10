package com.example.lab1_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lab1_android.databinding.FragmentInputBinding

class InputFragment : Fragment() {
    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fontSizeGroup.check(R.id.smallFont)

        binding.okButton.setOnClickListener {
            val inputText = binding.inputText.text.toString()

            if (inputText.isEmpty()) {
                Toast.makeText(requireContext(), "Введіть текст", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fontSize = when (binding.fontSizeGroup.checkedRadioButtonId) {
                R.id.smallFont -> 14f
                R.id.mediumFont -> 18f
                R.id.largeFont -> 22f
                else -> 14f
            }

            (requireActivity() as MainActivity).showResultFragment(inputText, fontSize)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}