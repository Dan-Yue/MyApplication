package com.ybs.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout

/**
 * Created by DanYue on 2022/6/14 16:48.
 */
class RecorderWave(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        initView(context)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.layout_recorder_wave, this, true)

    }
}