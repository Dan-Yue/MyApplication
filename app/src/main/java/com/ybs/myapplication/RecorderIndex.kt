package com.ybs.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import kotlin.math.abs


/**
 * Created by DanYue on 2022/6/14 17:14.
 */
@SuppressLint("AppCompatCustomView")
class RecorderIndex(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ImageView(context, attrs, defStyleAttr) {

    private var clickPoint: PointF = PointF(0f, 0f)
    private var moveX = 0f
    private var clickListener: ((PointF) -> Unit)? = null

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                moveX = event.x
                clickPoint.set(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                translationX = x + (event.x - moveX)
            }
            MotionEvent.ACTION_UP -> {
                val pointF = PointF(event.x, event.y)
                val a = abs(clickPoint.x - pointF.x) < 10f
                val b = abs(clickPoint.y - pointF.y) < 10f
                if (a && b && clickListener != null) {
                    clickListener!!(clickPoint)
                }
            }
        }
        return true
    }

    fun setRecorderX(recorderX: Float) {
        translationX = recorderX - width
    }

    fun setClickListener(clickListener: ((PointF) -> Unit)) {
        this.clickListener = clickListener
    }
}