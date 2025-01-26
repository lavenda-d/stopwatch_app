package com.example.stopwatch

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class StopwatchActivity : AppCompatActivity() {

    private lateinit var timeText: TextView
    private lateinit var lapText: TextView
    private var running = false
    private var isStopped = true
    private val handler = Handler()
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var lapCount = 1
    private var lastLapTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeText = findViewById(R.id.time_text)
        lapText = findViewById(R.id.lap_text)

        findViewById<Button>(R.id.start_button).setOnClickListener { start() }
        findViewById<Button>(R.id.lap_button).setOnClickListener { lap() }
        findViewById<Button>(R.id.reset_button).setOnClickListener { reset() }
        findViewById<Button>(R.id.stop_button).setOnClickListener { stop() }
    }

    private fun start() {
        if (isStopped) {

            // Resume from where it was stopped
            startTime = SystemClock.elapsedRealtime() - elapsedTime

        } else {
            // Start fresh if reset
            startTime = SystemClock.elapsedRealtime()
            elapsedTime = 0
            isStopped = false
        }
        running = true
        handler.postDelayed(updateRunnable, 10) // Start updating every 10ms
    }

    private fun lap() {
        if (running) {
            // Calculate the duration of the current lap
            val currentLapTime = elapsedTime - lastLapTime
            lastLapTime = elapsedTime // Update the last lap time to the current elapsed time

            // Format lap time
            val hours = (currentLapTime / 3600000).toInt()
            val minutes = (currentLapTime / 60000 % 60).toInt()
            val seconds = (currentLapTime / 1000 % 60).toInt()
            val milliseconds = (currentLapTime % 1000).toInt()

            // Record the lap
            val lapTextDisplay = "Lap $lapCount: ${String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds)}"
            lapText.text = lapText.text.toString() + "\n" + lapTextDisplay // Append new lap without overwriting
            lapCount++
        }
    }

    private fun stop() {
        running = false
        isStopped = true
        handler.removeCallbacks(updateRunnable)

        // Update elapsedTime to reflect the time when stopped
        elapsedTime = SystemClock.elapsedRealtime() - startTime
    }

    private fun reset() {
        running = false
        isStopped = true
        elapsedTime = 0
        startTime = 0
        lapCount = 1
        lastLapTime = 0
        handler.removeCallbacks(updateRunnable) // Stop updates completely
        timeText.text = "00:00:00:000" // Reset display
        lapText.text = "" // Reset lap text
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (running) {
                val currentTime = SystemClock.elapsedRealtime()
                elapsedTime = currentTime - startTime

                val hours = (elapsedTime / 3600000).toInt()
                val minutes = (elapsedTime / 60000 % 60).toInt()
                val seconds = (elapsedTime / 1000 % 60).toInt()
                val milliseconds = (elapsedTime % 1000).toInt()

                timeText.text = String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds)

                handler.postDelayed(this, 10) // Continue updating every 10ms
            }
        }
    }
}