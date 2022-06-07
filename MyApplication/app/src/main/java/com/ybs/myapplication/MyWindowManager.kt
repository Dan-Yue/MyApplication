package com.ybs.myapplication

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

/**
 * Created by DanYue on 2022/5/9 15:23.
 */
internal object MyWindowManager {
    /**
     * 小悬浮窗View的实例
     */
    private var smallWindow: FloatWindowSmallView? = null

    /**
     * 小悬浮窗View的参数
     */
    private var smallWindowParams: WindowManager.LayoutParams? = null

    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private var mWindowManager: WindowManager? = null

    /**
     * 用于获取手机可用内存
     */
    private var mActivityManager: ActivityManager? = null

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    @SuppressLint("RtlHardcoded")
    fun createSmallWindow(context: Context) {
        val windowManager = getWindowManager(context)
        val screenWidth = windowManager!!.defaultDisplay.width
        val screenHeight = windowManager.defaultDisplay.height
        if (smallWindow == null) {
            smallWindow = FloatWindowSmallView(context)
            if (smallWindowParams == null) {
                smallWindowParams = WindowManager.LayoutParams()
                smallWindowParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                smallWindowParams!!.format = PixelFormat.RGBA_8888
                smallWindowParams!!.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                smallWindowParams!!.gravity = Gravity.LEFT or Gravity.TOP
                smallWindowParams!!.width = FloatWindowSmallView.viewWidth
                smallWindowParams!!.height = FloatWindowSmallView.viewHeight
                smallWindowParams!!.x = screenWidth
                smallWindowParams!!.y = screenHeight / 2
            }
            smallWindow!!.setParams(smallWindowParams)
            windowManager.addView(smallWindow, smallWindowParams)
        }
    }

    /**
     * 将小悬浮窗从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    fun removeSmallWindow(context: Context) {
        if (smallWindow != null) {
            val windowManager = getWindowManager(context)
            windowManager!!.removeView(smallWindow)
            smallWindow = null
        }
    }

    /**
     * 更新小悬浮窗的TextView上的数据，显示内存使用的百分比。
     *
     * @param context 可传入应用程序上下文。
     */
    fun updateUsedPercent(context: Context) {
        if (smallWindow != null) {
            val percentView = smallWindow!!.findViewById(R.id.percent) as TextView
            percentView.text = getUsedPercentValue(context)
        }
    }

    fun updateSmallWindowText(text: String?) {
        if (smallWindow != null && text != null) {
            val percentView = smallWindow!!.findViewById(R.id.percent) as TextView
            percentView.text = text
        }
    }

    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    fun isWindowShowing(): Boolean {
        return smallWindow != null
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context 必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private fun getWindowManager(context: Context): WindowManager? {
        if (mWindowManager == null) {
            mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }
        return mWindowManager
    }

    /**
     * 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。否则返回当前已创建的ActivityManager。
     *
     * @param context 可传入应用程序上下文。
     * @return ActivityManager的实例，用于获取手机可用内存。
     */
    private fun getActivityManager(context: Context): ActivityManager? {
        if (mActivityManager == null) {
            mActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        }
        return mActivityManager
    }

    /**
     * 计算已使用内存的百分比，并返回。
     *
     * @param context 可传入应用程序上下文。
     * @return 已使用内存的百分比，以字符串形式返回。
     */
    fun getUsedPercentValue(context: Context): String {
        val dir = "/proc/meminfo"
        try {
            val fr = FileReader(dir)
            val br = BufferedReader(fr, 2048)
            val memoryLine = br.readLine()
            val subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"))
            br.close()
            val totalMemorySize = subMemoryLine.replace("\\D+".toRegex(), "").toInt().toLong()
            val availableSize = getAvailableMemory(context) / 1024
            val percent =
                ((totalMemorySize - availableSize) / totalMemorySize.toFloat() * 100).toInt()
            return "$percent%"
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "悬浮窗"
    }

    /**
     * 获取当前可用内存，返回数据以字节为单位。
     *
     * @param context 可传入应用程序上下文。
     * @return 当前可用内存。
     */
    private fun getAvailableMemory(context: Context): Long {
        val mi = ActivityManager.MemoryInfo()
        getActivityManager(context)!!.getMemoryInfo(mi)
        return mi.availMem
    }
}
