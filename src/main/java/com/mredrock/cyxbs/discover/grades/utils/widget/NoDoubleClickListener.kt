package com.mredrock.cyxbs.discover.grades.utils.widget

import android.view.View

/**
 * Created by roger on 2020/4/8
 * 一个防止快速双击的ClickListener
 */
abstract class NoDoubleClickListener : View.OnClickListener {

    companion object {
        @JvmStatic
        val MIN_CLICK_DELAY_TIME = 400
    }

    private var lastClickTime: Long = 0

    abstract fun onNoDoubleClick(v: View)

    override fun onClick(v: View?) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            v?.let {
                onNoDoubleClick(it)
            }
        }
    }
}