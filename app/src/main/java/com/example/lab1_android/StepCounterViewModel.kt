package com.example.lab1_android

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TrainingSession(
    val steps: Int,
    val duration: Long,
    val timestamp: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        steps = parcel.readInt(),
        duration = parcel.readLong(),
        timestamp = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(steps)
        parcel.writeLong(duration)
        parcel.writeString(timestamp)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TrainingSession> {
        override fun createFromParcel(parcel: Parcel): TrainingSession {
            return TrainingSession(parcel)
        }

        override fun newArray(size: Int): Array<TrainingSession?> {
            return arrayOfNulls(size)
        }

        fun fromJson(json: String): TrainingSession {
            val parts = json.split("|")
            return TrainingSession(parts[0].toInt(), parts[1].toLong(), parts[2])
        }

        fun toJson(session: TrainingSession): String {
            return "${session.steps}|${session.duration}|${session.timestamp}"
        }
    }
}

class StepCounterViewModel : ViewModel() {
    var stepCount: Int = 0
    var lastMagnitude: Double = 0.0
    var lastStepTime: Long = 0L
    var gravity: FloatArray = FloatArray(3) { 0f }
    var linearAcceleration: FloatArray = FloatArray(3) { 0f }

    var isTrainingActive: Boolean = false
    private var trainingStartTime: Long = 0L
    private val trainingSessions = mutableListOf<TrainingSession>()

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)
        val sessionsJson = prefs.getString("training_sessions", null)
        if (sessionsJson != null) {
            trainingSessions.clear()
            sessionsJson.split(";").filter { it.isNotEmpty() }.forEach {
                trainingSessions.add(TrainingSession.fromJson(it))
            }
        }
    }

    fun startTraining() {
        isTrainingActive = true
        trainingStartTime = System.currentTimeMillis()
        stepCount = 0
    }

    fun stopTraining(context: Context) {
        if (isTrainingActive) {
            isTrainingActive = false
            val duration = System.currentTimeMillis() - trainingStartTime
            val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            val session = TrainingSession(stepCount, duration, timestamp)
            trainingSessions.add(session)
            saveSessions(context)
        }
    }

    fun addManualSteps(steps: Int) {
        stepCount += steps
    }

    fun resetSteps() {
        stepCount = 0
    }

    fun getTrainingSessions(): List<TrainingSession> {
        return trainingSessions.toList()
    }

    private fun saveSessions(context: Context) {
        val prefs = context.getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)
        val sessionsJson = trainingSessions.joinToString(";") { TrainingSession.toJson(it) }
        with(prefs.edit()) {
            putString("training_sessions", sessionsJson)
            apply()
        }
    }
}