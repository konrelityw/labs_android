package com.example.lab1_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val recyclerView: RecyclerView = findViewById(R.id.training_sessions_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val sessions = intent.getParcelableArrayListExtra<TrainingSession>("training_sessions") ?: arrayListOf()
        recyclerView.adapter = TrainingSessionAdapter(sessions)

        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
}

class TrainingSessionAdapter(private val sessions: List<TrainingSession>) :
    RecyclerView.Adapter<TrainingSessionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sessionInfo: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val session = sessions[position]
        val durationMinutes = session.duration / 1000 / 60
        val durationSeconds = (session.duration / 1000) % 60
        holder.sessionInfo.text = "Тренування ${position + 1} (${session.timestamp}): ${session.steps} кроків, " +
                "Тривалість: $durationMinutes хв $durationSeconds сек"
    }

    override fun getItemCount(): Int = sessions.size
}