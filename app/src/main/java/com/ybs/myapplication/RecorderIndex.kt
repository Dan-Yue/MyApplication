package com.ybs.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView


/**
 * Created by DanYue on 2022/6/14 17:14.
 */
@SuppressLint("AppCompatCustomView")
class RecorderIndex(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ImageView(context, attrs, defStyleAttr) {

    private var moveX = 0f

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                moveX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                translationX = x + (event.x - moveX)
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return super.onTouchEvent(event)
    }

    fun setRecorderX(recorderX: Float) {
        translationX = recorderX - width
    }
}