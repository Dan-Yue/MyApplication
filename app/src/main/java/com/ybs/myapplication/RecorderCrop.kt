package com.ybs.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


/** java **/
//      findViewById<RecorderCrop>(R.id.crop).setClick { b, s, e ->
//            val str = "$s - $e"
//            if (b) {
//                Toast.makeText(this, "保留 - $str", Toast.LENGTH_LONG).show()
//            } else {
//                Toast.makeText(this, "删除 - $str", Toast.LENGTH_LONG).show()
//            }
//        }

/** xml **/
//    <com.ybs.myapplication.RecorderCrop
//        android:id="@+id/crop"
//        android:layout_width="match_parent"
//        android:layout_height="300dp" />

/**
 * Created by DanYue on 2022/3/31 18:52.
 */
class RecorderCrop(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {

    //根View
    private var layout: View? = null

    //范围View
    private var view: View? = null

    //裁剪开始图片按钮
    private var lImg: ImageView? = null

    //裁剪结束图片按钮
    private var rImg: ImageView? = null

    //取消和保存裁剪操作结果
    private var click: ((Boolean, Double, Double) -> Unit)? = null

    //裁剪开始时间显示
    private var lText: TextView? = null

    //裁剪结束时间显示
    private var rText: TextView? = null

    //录音总时长
    private var duration = 1000L

    //手指的绝对X坐标
    private var ex = 0f

    //裁剪开始图片的X坐标
    private var lx = 0f

    //裁剪开始图片的宽度
    private var lw = 0

    //裁剪结束时间的X坐标
    private var rx = 0f

    //裁剪结束时间图片的宽度
    private var rw = 0

    //裁剪范围控件的X坐标
    private var vx = 0f

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        initView(context)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.layout_recorder_crop, this, true)
        layout = findViewById(R.id.layout)
        view = findViewById(R.id.view)
        rImg = findViewById(R.id.left)
        lImg = findViewById(R.id.right)
        lText = findViewById(R.id.start_progress)
        rText = findViewById(R.id.end_progress)
        rImg!!.setOnTouchListener { v, event ->
            when (initEvent(event)) {
                MotionEvent.ACTION_DOWN -> lText!!.visibility = View.VISIBLE
                MotionEvent.ACTION_MOVE -> {
                    val viewWidth = (lx - ex).toInt()
                    val time = (ex * duration / rootView.width).toLong()
                    if (ex < (lx - lw) && ex > 0) {
                        setWHX(v, ex, v.width, v.height)
                        setWHX(view, ex + lw / 2, viewWidth, v.height)
                        lText!!.x = vx
                        lText!!.text = time.toString()
                    }
                }
                MotionEvent.ACTION_UP -> lText!!.visibility = View.INVISIBLE
            }
            true
        }
        lImg!!.setOnTouchListener { v, event ->
            when (initEvent(event)) {
                MotionEvent.ACTION_DOWN -> rText!!.visibility = View.VISIBLE
                MotionEvent.ACTION_MOVE -> {
                    val viewWidth = (ex - vx + rw / 2).toInt()
                    val time = ((ex + rw) * duration / rootView.width).toLong()
                    if (ex > (rx + rw) && ex < (rootView.width - rw)) {
                        setWHX(v, ex, v.width, v.height)
                        setWHX(view, vx, viewWidth, v.height)
                        rText!!.x = vx + view!!.width - rText!!.width
                        rText!!.text = time.toString()
                    }
                }
                MotionEvent.ACTION_UP -> rText!!.visibility = View.INVISIBLE
            }
            true
        }
        findViewById<TextView>(R.id.reserved).setOnClickListener {
            handling(true)
        }
        findViewById<TextView>(R.id.del).setOnClickListener {
            handling(false)
        }
    }

    /**
     * 设置取消和裁剪事件监听
     *
     * @param block 取消和裁剪监听器
     */
    fun setClick(block: (Boolean, Double, Double) -> Unit) {
        click = block
    }

    /**
     * 设置录音总时长
     *
     * @param duration 录音总时长，毫秒值
     */
    fun setDuration(duration: Long) {
        this.duration = duration
    }

    /**
     * 取消和裁剪处理方法
     *
     * @param type 是否是取消
     */
    private fun handling(type: Boolean) {
        val start = rImg!!.left
        val end = lImg!!.right
        val all = layout!!.width
        val s = start.toDouble() / all
        val e = end.toDouble() / all
        if (click != null) {
            click!!(type, if (s > 100) 100.0 else s, if (e > 100) 100.0 else e)
        }
    }

    /**
     * 处理触摸时间前变量赋值
     *
     * @param event 触摸的MotionEvent对象
     */
    private fun initEvent(event: MotionEvent): Int {
        ex = event.rawX
        lx = lImg!!.x
        lw = lImg!!.width
        rx = rImg!!.x
        rw = rImg!!.width
        vx = view!!.x
        return event.action
    }

    /**
     * 动态设置view的宽高和x坐标
     *
     * @param view 视图
     * @param x x坐标值
     * @param width 视图宽度
     * @param height 视图高度
     */
    private fun setWHX(view: View?, x: Float, width: Int, height: Int) {
        if (view != null) {
            val layoutParams = view.layoutParams
            layoutParams.width = width
            layoutParams.height = height
            view.x = x
            view.layoutParams = layoutParams
        }
    }
}