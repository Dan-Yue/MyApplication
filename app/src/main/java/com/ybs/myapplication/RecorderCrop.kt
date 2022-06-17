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
    private var layout: View? = null
    private var view: View? = null
    private var lImg: ImageView? = null
    private var rImg: ImageView? = null
    private var click: ((Boolean, Double, Double) -> Unit)? = null
    private var lText: TextView? = null
    private var rText: TextView? = null
    private var duration = 1000L
    private var ex = 0f
    private var lx = 0f
    private var lw = 0
    private var rx = 0f
    private var rw = 0
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

    fun setClick(block: (Boolean, Double, Double) -> Unit) {
        click = block
    }

    fun setDuration(duration: Long) {
        this.duration = duration
    }

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

    private fun initEvent(event: MotionEvent): Int {
        ex = event.rawX
        lx = lImg!!.x
        lw = lImg!!.width
        rx = rImg!!.x
        rw = rImg!!.width
        vx = view!!.x
        return event.action
    }

    //动态设置view的宽高
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