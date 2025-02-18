package com.example.lab1_android


import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var inputText: EditText
    private lateinit var outputText: TextView
    private lateinit var fontSizeGroup: RadioGroup
    private lateinit var okButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputText = findViewById(R.id.inputText)
        outputText = findViewById(R.id.outputText)
        fontSizeGroup = findViewById(R.id.fontSizeGroup)
        okButton = findViewById(R.id.okButton)
        cancelButton = findViewById(R.id.cancelButton)

        if (fontSizeGroup.checkedRadioButtonId == -1) {
            fontSizeGroup.check(R.id.smallFont)
        }

        okButton.setOnClickListener {
            if (inputText.text.toString().isEmpty()) {
                showAlert("Помилка", "Будь ласка, введіть текст")
                return@setOnClickListener
            }

            val fontSize = when (fontSizeGroup.checkedRadioButtonId) {
                R.id.smallFont -> 14f
                R.id.mediumFont -> 18f
                R.id.largeFont -> 22f
                else -> 14f
            }

            outputText.text = inputText.text.toString()
            outputText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
        }

        cancelButton.setOnClickListener {
            inputText.setText("")
            outputText.text = ""
        }
    }



    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}