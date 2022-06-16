package com.ybs.myapplication

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recorderWave = findViewById<RecorderWave>(R.id.RecorderWave)
        findViewById<Button>(R.id.test).setOnClickListener {
            recorderWave.test()
        }
//        recordedWave.initData()
//        recordedWave.setFlagClickListener { pointF, i, fl, fl2 -> }
//        recordedWave.setScaleOffsetListener { fl, fl2 -> }
    }
}