package com.ybs.myapplication

import android.graphics.Color
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

    fun isPointCross(
        pointF: PointF,
        flagList: MutableList<Pair<PointF, Int>>,
        flagSize: Float,
        crossY: Float,
    ): Pair<Int, PointF> {
        var indexFlag = -1
        var pointFlag = pointF
        flagList.forEachIndexed { index, pair ->
            val point = pair.first
            if (abs(pointF.x - point.x) < flagSize && abs(pointF.y - crossY) < flagSize) {
                indexFlag = index
                pointFlag = point
            }
        }
        return Pair(indexFlag, pointFlag)
    }

    fun getIndexFlagPaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.strokeWidth = 3f
            it.color = Color.RED
        }
    }

    fun getFlagPaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.strokeWidth = 3f
            it.color = Color.parseColor("#8EB7FF")
        }
    }

    fun getWavePaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.strokeWidth = 3f
            it.color = Color.parseColor("#FFB8B8")
        }
    }

    fun getTextPaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.textAlign = Paint.Align.CENTER
            it.color = Color.WHITE
            it.textSize = 24f
            it.isFakeBoldText = true
        }
    }
}