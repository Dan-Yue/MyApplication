package com.ybs.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

/**
 * Created by DanYue on 2022/6/14 17:14.
 */
@SuppressLint("AppCompatCustomView")
class RecorderIndex(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private var clickPoint: PointF = PointF(0f, 0f)
    private var moveX = 0f
    private var clickListener: ((PointF) -> Unit)? = null
    private var eventX = 0f
    private var canvas = Canvas()
    private val flagHeight = 32f
    private val flagBackWidth = 50f
    private var flagWidth = 50f
    private val flagPaint by lazy { WaveUtil.getIndexFlagPaint() }
    private val textPaint by lazy { WaveUtil.getTextPaint() }
    private var text = "12:00"

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas
        drawFlag()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mWidth = text.length * 10 + 30f + flagHeight
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth.toInt(), heightSpecSize)
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth.toInt(), heightSpecSize)
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, heightSpecSize)
        }
    }

    private fun drawFlag() {
        val x = flagHeight
        flagWidth = text.length * 10 + 30f
        val flagPath = Path()
        flagPath.moveTo(x, 0f)
        flagPath.lineTo(x + flagWidth, 0f)
        flagPath.lineTo(x + flagWidth, flagHeight)
        flagPath.lineTo(x, flagHeight)
        flagPath.close()
        canvas.drawPath(flagPath, flagPaint)
        canvas.drawLine(x, 0f, x, height - flagHeight, flagPaint)
        val baseLineY = WaveUtil.getBaseLineY(textPaint, flagHeight / 2)
        canvas.drawText(text, x + (flagWidth / 2), baseLineY, textPaint)
        canvas.drawCircle(x, height - flagHeight, flagHeight, flagPaint)
        canvas.drawCircle(x, height - flagHeight, flagHeight / 2, textPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                moveX = event.x
                clickPoint.set(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                eventX = calX(x + (event.x - moveX))
                translationX = eventX
            }
            MotionEvent.ACTION_UP -> {
                val pointF = PointF(event.x, event.y)
                val a = abs(clickPoint.x - pointF.x) < 10f
                val b = abs(clickPoint.y - pointF.y) < 10f
                val c = clickPoint.y < 54f
                if (a && b && c && clickListener != null) {
                    clickListener!!(clickPoint)
                }
            }
        }
        return true
    }

    private fun calX(x: Float): Float {
        val min = 0f - flagHeight
        return if (x > min) x else min
    }

    fun setRecorderX(recorderX: Float) {
        eventX = calX(recorderX - flagHeight - flagBackWidth / 2)
        translationX = eventX
    }

    fun setClickListener(clickListener: ((PointF) -> Unit)) {
        this.clickListener = clickListener
    }

    fun initView() {
        eventX = 0f - flagHeight
        translationX = 0f - flagHeight
    }
}