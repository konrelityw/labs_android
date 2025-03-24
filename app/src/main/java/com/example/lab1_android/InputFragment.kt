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
    private lateinit var fileHelper: FileHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        fileHelper = FileHelper(requireContext())
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

            val entry = TextEntry(inputText, fontSize)
            val isSaved = fileHelper.saveTextEntry(entry)

            if (isSaved) {
                Toast.makeText(requireContext(), "Текст успішно збережено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Помилка при збереженні тексту", Toast.LENGTH_SHORT).show()
            }

            (requireActivity() as MainActivity).showResultFragment(inputText, fontSize)
        }

        binding.openButton.setOnClickListener {
            (requireActivity() as MainActivity).openStoredDataActivity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}