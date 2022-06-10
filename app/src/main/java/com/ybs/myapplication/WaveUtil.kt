package com.ybs.myapplication

import android.graphics.Paint
import kotlin.math.abs

/**
 * Created by DanYue on 2022/6/10 14:27.
 */
object WaveUtil {

    /**
     * 限定数值的大小
     */
    fun limitedSize(value: Float, min: Float, max: Float): Float {
        var v = value
        if (value < min) v = min
        if (value > max) v = max
        return v
    }

    /**
     * 计算中心点
     */
    fun calculationCenterPoint(from: Float, to: Float): Float {
        return (to - from) / 2 + from
    }

    /**
     * 基线纵坐标
     */
    fun getBaseLineY(paint: Paint, centerY: Float): Float {
        val fontMetrics = paint.fontMetrics
        return centerY + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
    }

    fun validChange(value: Float, range: Float): Boolean {
        return abs(value) > range
    }
}