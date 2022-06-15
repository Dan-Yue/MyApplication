package com.ybs.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
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

    //最开始手指的坐标
    private var startPoint = PointF(0f, 0f)

    //结束时手指的坐标
    private var endPoint = PointF(0f, 0f)

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

    //旗子点击监听，参数（被点击旗子的中心点，被点击旗子的数字）
    private var listener: ((PointF, Int) -> Unit)? = null

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
        canvas.drawLine(0f, h, w, h, paint)
        val flagDrawList = mutableListOf<Pair<Float, Int>>()
        //绘制音波线
        for (i in 0 until data.size) {
            val volume = data[i] * h
            val x = i * w / data.size
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

    private fun drawFlag(x: Float, i: Int, h: Float) {
        val flagPath = Path()
        flagPath.moveTo(x, 0f)
        flagPath.lineTo(x + flagWidth, 0f)
        flagPath.lineTo(x + flagWidth, flagHeight)
        flagPath.lineTo(x, flagHeight)
        flagPath.close()
        canvas.drawPath(flagPath, flagPaint)
        canvas.drawLine(x, 0f, x, h * 2 - flagHeight * 2, flagPaint)
        val baseLineY = WaveUtil.getBaseLineY(textPaint, flagHeight / 2)
        canvas.drawText(i.toString(), x + (flagWidth / 2), baseLineY, textPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> startPoint = PointF(event.x, event.y)
            MotionEvent.ACTION_MOVE -> endPoint = PointF(event.x, event.y)
            MotionEvent.ACTION_UP -> actionUp()
        }
        return true
    }

    private fun actionUp() {
        val move = endPoint.x - startPoint.x
        if (abs(move) < 10f) {
            val i = WaveUtil.isPointFlag(startPoint, flagPointList, flagWidth, flagHeight)
            if (listener != null && i.first >= 0) listener!!(i.second, i.first + 1)
        }
    }

    fun setFlagClickListener(flagListener: (PointF, Int) -> Unit) {
        this.listener = flagListener
    }

    fun initData() {
        for (i in 0 until 100) {
            val volume = (0..1000).random() / 1000f
            data.add(volume)
        }
        flag.add(2)
        flag.add(39)
        flag.add(50)
    }

    fun setData() {
        flag.add(75)
        postInvalidate()
    }
}


