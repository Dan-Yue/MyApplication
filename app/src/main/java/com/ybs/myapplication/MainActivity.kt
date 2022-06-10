package com.ybs.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recordedWave = findViewById<RecordedWave>(R.id.RecordedWave)
        recordedWave.initData()
    }
}