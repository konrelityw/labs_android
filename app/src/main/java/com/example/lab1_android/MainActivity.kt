package com.example.lab1_android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.lab1_android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, InputFragment())
            }
        }
    }

    fun showResultFragment(text: String, fontSize: Float) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, ResultFragment.newInstance(text, fontSize))
            addToBackStack(null)
        }
    }

    fun openStoredDataActivity() {
        val intent = Intent(this, StoredDataActivity::class.java)
        startActivity(intent)
    }
}