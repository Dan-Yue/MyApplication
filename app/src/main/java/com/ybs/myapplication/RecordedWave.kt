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
import kotlin.math.abs


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
    private var a2 = 0f
    private var b1 = 0f
    private var b2 = 0f
    private var p = 0f //中心点
    private var m = 1f//倍数
    private var m1 = 1f
    private var p1 = 0f
    private var move = 0f
    private var move1 = 0f
    private var move2 = 0f
    private var move3 = 0f

    private fun drawView() {
        val h = height / 2f
        val w = width * 1f
        val mo = move2 - move1
        val mm = (b2 - b1) / (a2 - a1)
        if (isStart) {
            if (WaveUtil.validChange(mm - 1f, 0.01f)) {
                m = m1 * mm
                p = WaveUtil.calculationCenterPoint(a2, a1)
            }
            m = WaveUtil.limitedSize(m, 1f, 100f)
            val leftMove = 0 - (m * p - p) + move3
            val rightMove = m * w - (m * p - p) + move3
            if (leftMove > 0 || rightMove < w) {
                m = 1f
                p = 0f
                move = 0f
            }
        } else {
            m = m1
            p = p1
            move = if (isMove && abs(mo) > 10f) {
                val leftMove = 0 - (m * p - p) + (move3 + mo)
                val rightMove = m * w - (m * p - p) + (move3 + mo)
                when {
                    leftMove > 0 -> {
                        m * p - p
                    }
                    rightMove < w -> {
                        w - (m * w - (m * p - p))
                    }
                    else -> {
                        move3 + mo
                    }
                }
            } else {
                move3
            }
        }
        canvas.drawLine(0f, h, w, h, paint)
        val flagDrawList = mutableListOf<Pair<Float, Int>>()
        //绘制音波线
        for (i in 0 until data.size) {
            val volume = data[i] * h
            val k = i * w / data.size
            val x = m * k - (m * p - p) + move
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
        val baseLineY = WaveUtil.getBaseLineY(textPaint, flagViewHeight + (flagHeight / 2))
        canvas.drawText(i.toString(), x + (flagWidth / 2), baseLineY, textPaint)
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
                move1 = event.getX(0)
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
            isMove = false
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
        if (x1 != 0f && x2 == 0f) {
            move2 = x1
            isMove = true
        }
    }

    var isStart = false
    var isMove = false

    private fun setScalingEnd() {
        isStart = false
        isMove = false
        m1 = m
        p1 = p
        move3 = move
    }

}

