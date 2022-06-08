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
    private var endX: Float = 0f
    private var endLastX = 0
    private var endImage: ImageView? = null
    private var startX: Float = 0f
    private var startLastX = 0
    private var startImage: ImageView? = null
    private val px = (7.5f * context.resources.displayMetrics.density + 0.5f).toInt()
    private var click: ((Boolean, Double, Double) -> Unit)? = null

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
        startImage = findViewById(R.id.left)
        endImage = findViewById(R.id.right)
        startImage!!.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    endLastX = endImage!!.left
                    startX = event.rawX
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - startX
                    if (v.right + dx <= endLastX && v.left + dx > 0) {
                        v.layout((v.left + dx).toInt(), v.top, (v.right + dx).toInt(), v.bottom)
                        view!!.layout((v.right + dx - px).toInt(), v.top, endLastX + px, v.bottom)
                        startX = event.rawX
                    }
                }
                MotionEvent.ACTION_UP -> {
                    startLastX = v.right
                }
            }
            true
        }
        endImage!!.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startLastX = startImage!!.right
                    endX = event.rawX
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - endX
                    if (v.left + dx > startLastX && v.right + dx <= layout!!.right) {
                        v.layout((v.left + dx).toInt(), v.top, (v.right + dx).toInt(), v.bottom)
                        view!!.layout(startLastX - px, v.top, (v.left + dx + px).toInt(), v.bottom)
                        endX = event.rawX
                    }
                }
                MotionEvent.ACTION_UP -> {
                    endLastX = v.left
                }
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

    private fun handling(type: Boolean) {
        val start = startImage!!.left
        val end = endImage!!.right
        val all = layout!!.width
        val s = start.toDouble() / all
        val e = end.toDouble() / all
        if (click != null) {
            click!!(type, if (s > 100) 100.0 else s, if (e > 100) 100.0 else e)
        }
    }
}