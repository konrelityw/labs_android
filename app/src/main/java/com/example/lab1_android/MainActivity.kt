package com.example.lab1_android

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.absoluteValue
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null
    private lateinit var stepCountText: TextView
    private lateinit var startTrainingButton: Button
    private lateinit var stopTrainingButton: Button
    private lateinit var manualStepInput: EditText
    private lateinit var addManualStepsButton: Button
    private lateinit var resetStepsButton: Button
    private lateinit var viewStatsButton: Button
    private val viewModel: StepCounterViewModel by viewModels()
    private val threshold = 0.5
    private val minStepInterval = 250L
    private val alpha = 0.8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stepCountText = findViewById(R.id.step_count_text)
        startTrainingButton = findViewById(R.id.start_training_button)
        stopTrainingButton = findViewById(R.id.stop_training_button)
        manualStepInput = findViewById(R.id.manual_step_input)
        addManualStepsButton = findViewById(R.id.add_manual_steps_button)
        resetStepsButton = findViewById(R.id.reset_steps_button)
        viewStatsButton = findViewById(R.id.view_stats_button)

        viewModel.initialize(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometerSensor == null) {
            stepCountText.text = "Акселерометр не підтримується на цьому пристрої"
        } else {
            stepCountText.text = "Кількість кроків: ${viewModel.stepCount}"
        }

        startTrainingButton.setOnClickListener {
            viewModel.startTraining()
            startTrainingButton.isEnabled = false
            stopTrainingButton.isEnabled = true
            stepCountText.text = "Кількість кроків: ${viewModel.stepCount}"
        }

        stopTrainingButton.setOnClickListener {
            viewModel.stopTraining(this)
            startTrainingButton.isEnabled = true
            stopTrainingButton.isEnabled = false
        }

        addManualStepsButton.setOnClickListener {
            val steps = manualStepInput.text.toString().toIntOrNull() ?: 0
            if (steps > 0) {
                viewModel.addManualSteps(steps)
                stepCountText.text = "Кількість кроків: ${viewModel.stepCount}"
                manualStepInput.text.clear()
            }
        }

        resetStepsButton.setOnClickListener {
            viewModel.resetSteps()
            stepCountText.text = "Кількість кроків: ${viewModel.stepCount}"
        }

        viewStatsButton.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            intent.putParcelableArrayListExtra("training_sessions", ArrayList(viewModel.getTrainingSessions()))
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometerSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER && viewModel.isTrainingActive) {
                viewModel.gravity[0] = (alpha * viewModel.gravity[0] + (1 - alpha) * it.values[0]).toFloat()
                viewModel.gravity[1] = (alpha * viewModel.gravity[1] + (1 - alpha) * it.values[1]).toFloat()
                viewModel.gravity[2] = (alpha * viewModel.gravity[2] + (1 - alpha) * it.values[2]).toFloat()

                viewModel.linearAcceleration[0] = it.values[0] - viewModel.gravity[0]
                viewModel.linearAcceleration[1] = it.values[1] - viewModel.gravity[1]
                viewModel.linearAcceleration[2] = it.values[2] - viewModel.gravity[2]

                val magnitude = sqrt(
                    (viewModel.linearAcceleration[0] * viewModel.linearAcceleration[0] +
                            viewModel.linearAcceleration[1] * viewModel.linearAcceleration[1] +
                            viewModel.linearAcceleration[2] * viewModel.linearAcceleration[2]).toDouble()
                )

                Log.d("StepCounter", "Magnitude: $magnitude, Diff: ${(magnitude - viewModel.lastMagnitude).absoluteValue}")

                val currentTime = System.currentTimeMillis()
                if (viewModel.lastMagnitude > 0 && (magnitude - viewModel.lastMagnitude).absoluteValue > threshold &&
                    (currentTime - viewModel.lastStepTime) > minStepInterval) {
                    viewModel.stepCount++
                    viewModel.lastStepTime = currentTime
                    stepCountText.text = "Кількість кроків: ${viewModel.stepCount}"
                    Log.d("StepCounter", "Step detected! Count: ${viewModel.stepCount}")
                }
                viewModel.lastMagnitude = magnitude
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}