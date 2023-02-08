package com.mredrock.cyxbs.declare.pages.main.utils

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/4
 * @Description:
 */
fun View.setOnFiveAndDoubleClickListener(
    interval: Long = 500,
    listener: FiveAndDoubleClickListener
) {
    /**
     * 用于区分双击和五击的延时判定
     */
    val mHandler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            listener.doubleClick(this@setOnFiveAndDoubleClickListener)
        }
    }
    var count = 1
    setOnClickListener {
        val tag = getTag(2078660398) as? Long
        if (System.currentTimeMillis() - (tag ?: 0L) < interval || count == 0) {
            count++
            when (count) {
                5 -> {
                    listener.fiveClick(it)
                }
                2 -> {
                    //因为五次连击包括了双击，所以这里做了一个延时判断，双击后短时间内没按第三下就是双击，按了就不是双击。
                    mHandler.sendEmptyMessageDelayed(0, 200)
                }
                3 -> {
                    mHandler.removeCallbacksAndMessages(null)
                }
            }
        } else {
            count = 1
        }
        it.setTag(2078660398, System.currentTimeMillis())
    }
}

interface FiveAndDoubleClickListener {
    fun doubleClick(view: View)
    fun fiveClick(view: View)
}