package com.ybs.myapplication

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View


/**
 * Created by DanYue on 2022/6/7 14:23.
 */
class RecordedWave(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : View(context, attrs, defStyleAttr) {

    private var defaultSize = 0

    //音量值列表
    private val data = mutableListOf<Float>()

    //旗子插入的地方，列表形式，内容为音量值列表的下标
    private val flag = mutableListOf<Int>()

    //旗子的高度
    private val flagHeight = 32f

    //旗子的宽度
    private val flagWidth = 50f

    //画布
    private var canvas = Canvas()

    //用来记录上次插旗时间戳
    private var minTimeTag = 0L

    //插旗的最小时间间隔，不允许快速连续插旗
    private val minTimeInterval = 1000L

    //绘制音波图的画笔
    private val paint by lazy {
        Paint().also {
            it.strokeWidth = 3f
            it.color = Color.parseColor("#FFB8B8")
        }
    }

    //绘制旗子的画笔
    private val flagPaint by lazy {
        Paint().also {
            it.strokeWidth = 3f
            it.color = Color.parseColor("#8EB7FF")
        }
    }

    //绘制旗子上数字的画笔
    private val textPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.textAlign = Paint.Align.CENTER
            it.color = Color.WHITE
            it.textSize = 24f
            it.isFakeBoldText = true
        }
    }

    init {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RecorderWave)
        defaultSize = a.getDimensionPixelSize(R.styleable.RecorderWave_default_size, 100)
        a.recycle()
    }

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas
        drawView()
    }

    private var a1 = 0f
    private var a2 = 1080f
    private var b1 = 0f
    private var b2 = 720f
    private var p = 1080f //中心点
    private var m = 1f//倍数
    private var m1 = 1f
    private var p1 = 0f

    private fun drawView() {
        val max = data.size / 100 * 10
        if (isStart) {
            m = m1 * (b2 - b1) / (a2 - a1)
            p = (a2 - a1) / 2 + a1
        } else {
            m = m1
            p = p1
        }

        if (m < 1.0f) {
            m = 1.0f
        }
        if (m > max.toFloat()) {
            m = max.toFloat()
        }
        val h = height / 2f
        val w = width * 1f
        canvas.drawLine(0f, h, w, h, paint)
        val end = data.size
        val start = 0
        val flagDrawList = mutableListOf<Pair<Float, Int>>()
        //绘制音波线
        for (i in start until end) {
            val volume = data[i] * h
            val k = (i - start) * w / end
            val x = m * k - (m * p - p)
            canvas.drawLine(x, h, x, h + volume, paint)
            canvas.drawLine(x, h - volume, x, h, paint)
            if (flag.contains(i)) {
                flagDrawList.add(Pair(x, flag.indexOf(i)))
            }
        }
        //绘制标志旗
        flagDrawList.forEach {
            drawFlag(it.first, it.second + 1, h)
        }
    }

    private fun drawFlag(x: Float, i: Int, h: Float) {
        val flagPath = Path()
        val flagViewHeight = h / 4
        flagPath.moveTo(x, flagViewHeight)
        flagPath.lineTo(x + flagWidth, flagViewHeight)
        flagPath.lineTo(x + flagWidth, flagViewHeight + flagHeight)
        flagPath.lineTo(x, flagViewHeight + flagHeight)
        flagPath.close()
        canvas.drawPath(flagPath, flagPaint)
        canvas.drawLine(x, flagViewHeight, x, h * 2, flagPaint)
        val baseLineY = getBaseLineY(textPaint, flagViewHeight + (flagHeight / 2))
        canvas.drawText(i.toString(), x + (flagWidth / 2), baseLineY, textPaint)
    }

    private fun getBaseLineY(paint: Paint, centerY: Float): Float {
        val fontMetrics = paint.fontMetrics
        return centerY + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
    }


    fun initData() {
        for (i in 0 until 1000) {
            val volume = (0..1000).random() / 1000f - 0.03f
            data.add(volume)
        }
        flag.add(20)
        flag.add(390)
        flag.add(500)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isStart = false
            }
            MotionEvent.ACTION_MOVE -> {
                setScalingStart(event)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                setScalingEnd()
            }
        }
        return true
    }

    private fun setScalingStart(event: MotionEvent) {
        val x1 = event.getX(0)
        var x2 = 0f
        if (event.pointerCount == 2) {
            x2 = event.getX(1)
        }
        Log.d("--x", "x1 = $x1 , x2 = $x2")
        if (x1 != 0f && x2 != 0f) {
            if (!isStart) {
                a1 = x1
                a2 = x2
                b1 = x1
                b2 = x2
                isStart = true
            } else {
                b1 = x1
                b2 = x2
            }
        }
    }

    var isStart = false

    private fun setScalingEnd() {
        isStart = false
        m1 = m
        p1 = p
    }
}

