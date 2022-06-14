package com.ybs.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

/**
 * Created by DanYue on 2022/6/14 16:48.
 */
class RecorderWave(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var frame: View? = null

    private var recorded: RecordedWave? = null

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
        recorded?.setFlagClickListener { point, _, _, _ ->
            flag?.setRecorderX(point.x)
        }
    }
}