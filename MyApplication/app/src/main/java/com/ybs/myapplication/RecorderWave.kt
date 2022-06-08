package com.ybs.myapplication

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*
import kotlin.random.Random


/**
 * Created by DanYue on 2022/6/7 14:23.
 */
class RecorderWave(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var defaultSize = 0

    init {
        //第二个参数就是我们在styles.xml文件中的<declare-styleable>标签
        //即属性集合的标签，在R文件中名称为R.styleable+name
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RecorderWave)
        //第一个参数为属性集合里面的属性，R文件名称：R.styleable+属性集合名称+下划线+属性名称
        //第二个参数为，如果没有设置这个属性，则设置的默认的值
        defaultSize = a.getDimensionPixelSize(R.styleable.RecorderWave_default_size, 100)
        //最后记得将TypedArray对象回收
        a.recycle()
    }

    var value: Float = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint()
        val y = (height / 2).toFloat()
        paint.strokeWidth = 3f
        paint.color = Color.RED
        canvas.drawLine(0f, y, width.toFloat(), y, paint)
        for (i in 0..50) {
            val volume = list[i]
            val x = value + i * width / 50
            canvas.drawLine(x, y, x, y + volume, paint)
            canvas.drawLine(x, y - volume, x, y, paint)
        }
        val value2 = value - width
        for (i in 0..50) {
            val volume = list[i]
            val x = value2 + i * width / 50
            canvas.drawLine(x, y, x, y + volume, paint)
            canvas.drawLine(x, y - volume, x, y, paint)
        }
    }


    fun startAnimation() {
        val time = 5000L
        val anim = ValueAnimator.ofFloat(value, width.toFloat())
        anim.repeatCount = ValueAnimator.INFINITE
        anim.repeatMode = ValueAnimator.RESTART
        anim.duration = time
        anim.interpolator = LinearInterpolator()
        anim.addUpdateListener { animation ->
            value = animation.animatedValue as Float
            postInvalidate()
        }
        anim.start()
        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                pop()
            }
        }
        timer.schedule(timerTask, time / 50, time / 50)
    }

    val list by lazy {
        val s = mutableListOf<Float>()
        for (i in 0..50) {
            s.add(i * 2f)
        }
        s
    }

    fun getVolume(): Float {
        return Random(System.currentTimeMillis()).nextFloat() * 120f
    }


    fun pop() {
        list[0] = getVolume()
    }
}

