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

    //偏移距离和偏移距离缓存
    private var offset = Pair(0f, 0f)

    //最开始手指的坐标和结束时手指的坐标
    private var sliding = Pair(PointF(0f, 0f), PointF(0f, 0f))

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

    //旗子坐标列表
    private val flagPointList = mutableListOf<Pair<PointF, Int>>()

    //旗子点击监听，参数（被点击旗子的中心点，被点击旗子的数字，音波图的缩放倍数，音波图的偏移）
    private var flagClickListener: ((PointF, Int, Float, Float) -> Unit)? = null

    //缩放移动监听，参数（音波图的缩放倍数，音波图的位移）
    private var scaleOffsetListener: ((Float, Float) -> Unit)? = null

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

    private fun drawView() {
        val h = height / 2f
        val w = width * 1f
        val moveLength = sliding.second.x - sliding.first.x
        val distance = scaleDistance.second / scaleDistance.first
        if (isScale) {
            scaleCalculate(distance, w)
        } else {
            moveCalculate(moveLength, w)
        }
        if (scaleOffsetListener != null) {
            Log.d("scaleOffsetListener", "${scale.first}-${offset.first}")
            scaleOffsetListener!!(scale.first, offset.first)
        }
        canvas.drawLine(0f, h, w, h, paint)
        val flagDrawList = mutableListOf<Pair<Float, Int>>()
        //绘制音波线
        for (i in 0 until data.size) {
            val volume = data[i] * h
            val k = i * w / data.size
            val x =
                scale.first * k - (scale.first * centerPoint.first - centerPoint.first) + offset.first
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
                0 - (scale.first * centerPoint.first - centerPoint.first) + (offset.second + moveLength)
            val rightMove =
                scale.first * w - (scale.first * centerPoint.first - centerPoint.first) + (offset.second + moveLength)
            when {
                leftMove > 0 -> scale.first * centerPoint.first - centerPoint.first
                rightMove < w -> w - (scale.first * w - (scale.first * centerPoint.first - centerPoint.first))
                else -> offset.second + moveLength
            }
        } else {
            offset.second
        }
        offset = WaveUtil.setFirst(offset, move)
    }

    private fun scaleCalculate(distance: Float, w: Float) {
        if (WaveUtil.validChange(distance - 1f, 0.01f)) {
            scale = WaveUtil.setFirst(scale, scale.second * distance)
            centerPoint = WaveUtil.setFirst(centerPoint, w / 2)
        }
        scale = WaveUtil.setFirst(scale, WaveUtil.limitedSize(scale.first, 1f, 100f))
        val leftMove =
            0 - (scale.first * centerPoint.first - centerPoint.first) + offset.second
        val rightMove =
            scale.first * w - (scale.first * centerPoint.first - centerPoint.first) + offset.second
        if (leftMove > 0 || rightMove < w) {
            scale = WaveUtil.setFirst(scale, 1f)
            centerPoint = WaveUtil.setFirst(centerPoint, 0f)
            offset = WaveUtil.setFirst(offset, 0f)
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isScale = false
                val firstPointF = PointF(event.getX(0), event.getY(0))
                val secondPointF = sliding.second
                sliding = Pair(firstPointF, secondPointF)
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
            val firstPointF = sliding.first
            val secondPointF = PointF(x1, event.getY(0))
            sliding = Pair(firstPointF, secondPointF)
            isMove = true
        }
    }

    private fun setScalingEnd() {
        isScale = false
        isMove = false
        scale = WaveUtil.setSecond(scale, scale.first)
        centerPoint = WaveUtil.setSecond(centerPoint, centerPoint.first)
        offset = WaveUtil.setSecond(offset, offset.first)
        if (!WaveUtil.validChange(sliding.second.x - sliding.first.x, 10f)) {
            if (flagClickListener != null) {
                val index =
                    WaveUtil.isPointFlag(sliding.first, flagPointList, flagWidth, flagHeight)
                Log.d(
                    "flagClickListener",
                    "${index.second}-${index.first}-${scale.second}-${offset.second}"
                )
                flagClickListener!!(index.second, index.first + 1, scale.second, offset.second)
            }
        }
    }

    fun setFlagClickListener(flagListener: (PointF, Int, Float, Float) -> Unit) {
        this.flagClickListener = flagListener
    }

    fun setScaleOffsetListener(scaleListener: (Float, Float) -> Unit) {
        this.scaleOffsetListener = scaleListener
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

}


