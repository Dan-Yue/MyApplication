package com.ybs.myapplication

import android.graphics.Paint
import android.graphics.PointF
import kotlin.math.abs

/**
 * Created by DanYue on 2022/6/10 14:27.
 */
object WaveUtil {

    /**
     * 基线纵坐标
     */
    fun getBaseLineY(paint: Paint, centerY: Float): Float {
        val fontMetrics = paint.fontMetrics
        return centerY + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
    }

    fun isPointFlag(
        pointF: PointF,
        flagList: MutableList<Pair<PointF, Int>>,
        flagWidth: Float,
        flagHeight: Float
    ): Pair<Int, PointF> {
        var indexFlag = -1
        var pointFlag = pointF
        flagList.forEachIndexed { index, pair ->
            val point = pair.first
            if (abs(pointF.x - point.x) < flagWidth && abs(pointF.y - point.y) < flagHeight) {
                indexFlag = index
                pointFlag = point
            }
        }
        return Pair(indexFlag, pointFlag)
    }
}