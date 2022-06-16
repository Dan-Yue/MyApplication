package com.ybs.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import java.util.*

/**
 * Created by DanYue on 2022/6/14 16:48.
 */
class RecorderWave(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var frame: View? = null

    private var recorded: RecordedBackground? = null

    private var flag: RecorderIndex? = null

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        initView(context)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.layout_recorder_wave, this, true)
        frame = findViewById(R.id.frame)
        recorded = findViewById(R.id.recorded)
        flag = findViewById(R.id.flag)
        recorded?.initData()
        recorded?.setFlagClickListener { point, _ ->
            Log.d("recordedClick", "$point")
            flag?.setRecorderX(point.x)
        }
        recorded?.setCrossClickListener { _, i ->
            recorded?.delData(i)
        }
        flag?.setClickListener {
            recorded?.setData(it)
        }
        flag?.setDuration(10000)
    }

    private var isStop = false
    var time = 0

    fun start() {
        if (time > 10000) {
            time = 0
        }
        isStop = false
        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                if (isStop) {
                    timer.cancel()
                } else {
                    flag?.setProgress(time)
                    time += 100
                }
            }
        }
        timer.schedule(timerTask, 0, 100)
    }

    fun stop() {
        isStop = true
    }
}