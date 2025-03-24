package com.example.lab1_android

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lab1_android.databinding.ActivityStoredDataBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StoredDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoredDataBinding
    private lateinit var fileHelper: FileHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoredDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileHelper = FileHelper(this)

        displayStoredData()

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.clearButton.setOnClickListener {
            if (fileHelper.clearAllEntries()) {
                displayStoredData()
            }
        }
    }

    private fun displayStoredData() {
        binding.entriesContainer.removeAllViews()

        val entries = fileHelper.getAllEntries()

        if (entries.isEmpty()) {
            binding.emptyDataMessage.visibility = View.VISIBLE
            binding.clearButton.visibility = View.GONE
        } else {
            binding.emptyDataMessage.visibility = View.GONE
            binding.clearButton.visibility = View.VISIBLE

            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

            entries.forEachIndexed { index, entry ->
                val entryView = LayoutInflater.from(this)
                    .inflate(R.layout.item_stored_entry, binding.entriesContainer, false)

                val textView = entryView.findViewById<TextView>(R.id.entryText)
                val infoView = entryView.findViewById<TextView>(R.id.entryInfo)

                textView.text = entry.text
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, entry.fontSize)

                val date = Date(entry.timestamp)
                infoView.text = "Запис #${index + 1} | Розмір: ${entry.fontSize}sp | Час: ${dateFormat.format(date)}"

                binding.entriesContainer.addView(entryView)
                if (index < entries.size - 1) {
                    val divider = View(this)
                    val dividerParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        2
                    )
                    dividerParams.setMargins(0, 16, 0, 16)
                    divider.layoutParams = dividerParams
                    divider.setBackgroundColor(getColor(android.R.color.darker_gray))
                    binding.entriesContainer.addView(divider)
                }
            }
        }
    }
}