package com.ybs.myapplication

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recorder = findViewById<RecorderWave>(R.id.recorder)
        findViewById<Button>(R.id.start).setOnClickListener {
            recorder.startAnimation()
        }
        findViewById<Button>(R.id.stop).setOnClickListener {
            recorder.stopAnimation()
        }
        findViewById<Button>(R.id.flag).setOnClickListener {
            recorder.setFlag()
        }
    }
}