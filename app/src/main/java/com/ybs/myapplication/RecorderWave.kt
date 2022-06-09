package com.ybs.myapplication

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import java.util.*


/**
 * Created by DanYue on 2022/6/7 14:23.
 */
class RecorderWave(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private var defaultSize = 0

    //音波图线条的个数
    private val count = 100

    //音波图运动一个屏幕宽度需要的时间
    private val time = 10000L

    //音量值列表
    private val data = mutableListOf<Float>()

    //定时器，用来形成音波图的变化
    private var timer: Timer? = null

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

    /**
     * 绘制视图
     */
    private fun drawView() {
        val h = height / 2f
        val w = width * 1f
        canvas.drawLine(0f, h, w, h, paint)
        val end = data.size
        val start = if (end < count) 0 else end - count
        val flagDrawList = mutableListOf<Pair<Float, Int>>()
        //绘制音波线
        for (i in start until end) {
            val volume = data[i] * h
            val x = (i - start) * w / count
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

    /**
     * 绘制旗子
     *
     * @param x 中心点x坐标
     * @param i 旗子上的数字
     * @param h 视图的半高，用以确定旗子的y坐标
     */
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

    /**
     * 启动动画
     */
    fun startAnimation() {
        if (timer == null) {
            val timerTask = object : TimerTask() {
                override fun run() {
                    setVolume()
                    postInvalidate()
                }
            }
            timer = Timer()
            timer?.schedule(timerTask, 0, time / count)
        }
    }

    /**
     * 停止动画
     */
    fun stopAnimation() {
        timer?.cancel()
        timer = null
    }


    /**
     * 插入音量值方法
     */
    fun setVolume() {
        val volume = (0..1000).random() / 1000f - 0.03f
        data.add(volume)
    }


    /**
     * 插入标记旗
     */
    fun setFlag() {
        val time = System.currentTimeMillis()
        if (time - minTimeTag > minTimeInterval) {
            minTimeTag = time
            flag.add(data.size - 1)
        }
    }


    /**
     * 计算基线坐标
     *
     * @param paint 画笔，其粗细、字体大小等会影响到计算基线
     * @param centerY 中心点Y坐标
     */
    private fun getBaseLineY(paint: Paint, centerY: Float): Float {
        val fontMetrics = paint.fontMetrics
        return centerY + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
    }

}

