package com.ybs.myapplication

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
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

    //最开始双指间的距离和结束时双指间的距离
    private var scaleDistance = Pair(0f, 0f)

    //中心点和中心点缓存
    private var centerPoint = Pair(0f, 0f)

    //缩放倍数和缩放倍数缓存
    private var scale = Pair(1f, 1f)

    //移动距离和移动距离缓存
    private var moveDistance = Pair(0f, 0f)

    //最开始手指的坐标和结束时手指的坐标
    private var movePoint = Pair(PointF(0f, 0f), PointF(0f, 0f))

    //是否是双指滑动
    private var isScale = false

    //是否是单指滑动
    private var isMove = false

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

    private val flagPointList = mutableListOf<Pair<PointF, Int>>()

    private fun drawView() {
        val h = height / 2f
        val w = width * 1f
        val moveLength = movePoint.second.x - movePoint.first.x
        val distance = scaleDistance.second / scaleDistance.first
        if (isScale) {
            scaleCalculate(distance, w)
        } else {
            moveCalculate(moveLength, w)
        }
        canvas.drawLine(0f, h, w, h, paint)
        val flagDrawList = mutableListOf<Pair<Float, Int>>()
        //绘制音波线
        for (i in 0 until data.size) {
            val volume = data[i] * h
            val k = i * w / data.size
            val x =
                scale.first * k - (scale.first * centerPoint.first - centerPoint.first) + moveDistance.first
            canvas.drawLine(x, h, x, h + volume, paint)
            canvas.drawLine(x, h - volume, x, h, paint)
            if (flag.contains(i)) {
                flagDrawList.add(Pair(x, flag.indexOf(i)))
            }
        }
        //绘制标志旗
        flagPointList.clear()
        flagDrawList.forEach {
            val flagX = it.first + flagWidth / 2
            val flagY = flagHeight / 2
            val pointF = PointF(flagX, flagY)
            flagPointList.add(Pair(pointF, it.second + 1))
            drawFlag(it.first, it.second + 1, h)
        }
    }

    private fun moveCalculate(moveLength: Float, w: Float) {
        scale = WaveUtil.setFirst(scale, scale.second)
        centerPoint = WaveUtil.setFirst(centerPoint, centerPoint.second)
        val move = if (isMove && abs(moveLength) > 10f) {
            val leftMove =
                0 - (scale.first * centerPoint.first - centerPoint.first) + (moveDistance.second + moveLength)
            val rightMove =
                scale.first * w - (scale.first * centerPoint.first - centerPoint.first) + (moveDistance.second + moveLength)
            when {
                leftMove > 0 -> scale.first * centerPoint.first - centerPoint.first
                rightMove < w -> w - (scale.first * w - (scale.first * centerPoint.first - centerPoint.first))
                else -> moveDistance.second + moveLength
            }
        } else {
            moveDistance.second
        }
        moveDistance = WaveUtil.setFirst(moveDistance, move)
    }

    private fun scaleCalculate(distance: Float, w: Float) {
        if (WaveUtil.validChange(distance - 1f, 0.01f)) {
            scale = WaveUtil.setFirst(scale, scale.second * distance)
            centerPoint = WaveUtil.setFirst(centerPoint, w / 2)
        }
        scale = WaveUtil.setFirst(scale, WaveUtil.limitedSize(scale.first, 1f, 100f))
        val leftMove =
            0 - (scale.first * centerPoint.first - centerPoint.first) + moveDistance.second
        val rightMove =
            scale.first * w - (scale.first * centerPoint.first - centerPoint.first) + moveDistance.second
        if (leftMove > 0 || rightMove < w) {
            scale = WaveUtil.setFirst(scale, 1f)
            centerPoint = WaveUtil.setFirst(centerPoint, 0f)
            moveDistance = WaveUtil.setFirst(moveDistance, 0f)
        }
    }

    private fun drawFlag(x: Float, i: Int, h: Float) {
        val flagPath = Path()
        flagPath.moveTo(x, 0f)
        flagPath.lineTo(x + flagWidth, 0f)
        flagPath.lineTo(x + flagWidth, flagHeight)
        flagPath.lineTo(x, flagHeight)
        flagPath.close()
        canvas.drawPath(flagPath, flagPaint)
        canvas.drawLine(x, 0f, x, h * 2 - flagHeight, flagPaint)
        val baseLineY = WaveUtil.getBaseLineY(textPaint, flagHeight / 2)
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
                isScale = false
                val firstPointF = PointF(event.getX(0), event.getY(0))
                val secondPointF = movePoint.second
                movePoint = Pair(firstPointF, secondPointF)
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
        if (x1 != 0f && x2 != 0f) {
            isMove = false
            scaleDistance = WaveUtil.setSecond(scaleDistance, x2 - x1)
            if (!isScale) {
                scaleDistance = WaveUtil.setFirst(scaleDistance, x2 - x1)
                isScale = true
            }
        }
        if (x1 != 0f && x2 == 0f) {
            val firstPointF = movePoint.first
            val secondPointF = PointF(x1, event.getY(0))
            movePoint = Pair(firstPointF, secondPointF)
            isMove = true
        }
    }

    private fun setScalingEnd() {
        isScale = false
        isMove = false
        scale = WaveUtil.setSecond(scale, scale.first)
        centerPoint = WaveUtil.setSecond(centerPoint, centerPoint.first)
        moveDistance = WaveUtil.setSecond(moveDistance, moveDistance.first)
        if (!WaveUtil.validChange(movePoint.second.x - movePoint.first.x, 10f)) {
//            Log.d("--test", ">>>${movePoint.first}")
//            Log.d("--test", "++$flagPointList")
            val index = WaveUtil.isPointFlag(movePoint.first, flagPointList, flagWidth, flagHeight)
            Log.d("--test", "======> $index")
        }
    }


}


