package com.ybs.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recordingWave = findViewById<RecordingWave>(R.id.RecordingWave)
        val recordedWave = findViewById<RecordedWave>(R.id.RecordedWave)
        findViewById<Button>(R.id.recording).setOnClickListener {
            recordingWave.stopAnimation()
            recordingWave.visibility = View.VISIBLE
            recordedWave.stopAnimation()
            recordedWave.visibility = View.GONE
        }
        findViewById<Button>(R.id.recorded).setOnClickListener {
            recordingWave.stopAnimation()
            recordingWave.visibility = View.GONE
            recordedWave.stopAnimation()
            recordedWave.visibility = View.VISIBLE
        }
        findViewById<Button>(R.id.start).setOnClickListener {
            if (recordingWave.visibility == View.VISIBLE) {
                recordingWave.startAnimation()
            }
            if (recordedWave.visibility == View.VISIBLE) {
                recordedWave.startAnimation()
            }
        }
        findViewById<Button>(R.id.stop).setOnClickListener {
            if (recordingWave.visibility == View.VISIBLE) {
                recordingWave.stopAnimation()
            }
            if (recordedWave.visibility == View.VISIBLE) {
                recordedWave.stopAnimation()
            }
        }
        findViewById<Button>(R.id.flag).setOnClickListener {
            if (recordingWave.visibility == View.VISIBLE) {
                recordingWave.setFlag()
            }
            if (recordedWave.visibility == View.VISIBLE) {
                recordedWave.setFlag()
            }
        }
    }
}