package com.mredrock.cyxbs.ufield.lyt.ui.helper

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver

/**
 *  description :这是一个辅助类，来检测系统的软键盘是否打开，用法如下
 *  SoftKeyBoardListener.setListener(this, object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
 *     override fun keyBoardShow(height: Int) {
 *     //处理软键盘打开的逻辑
 *         Toast.makeText(this@yourActivity, "输入法打开", Toast.LENGTH_SHORT).show()
 *     }
 *
 *     override fun keyBoardHide(height: Int) {
 *     //处理软键盘关闭的逻辑
 *         Toast.makeText(this@yourActivity, "输入法关闭", Toast.LENGTH_SHORT).show()
 *     }
 * })
 *
 *  author : lytMoon
 *  date : 2023/8/22 16:35
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */



class SoftKeyBoardListener(private val activity: Activity) {
    private var rootView: View = activity.window.decorView
    private var rootViewVisibleHeight = 0
    private var onSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener? = null

    init {
        rootView.viewTreeObserver.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val visibleHeight = r.height()

            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight
                return@OnGlobalLayoutListener
            }

            if (rootViewVisibleHeight == visibleHeight) {
                return@OnGlobalLayoutListener
            }

            if (rootViewVisibleHeight - visibleHeight > 200) {
                onSoftKeyBoardChangeListener?.keyBoardShow(rootViewVisibleHeight - visibleHeight)
                rootViewVisibleHeight = visibleHeight
                return@OnGlobalLayoutListener
            }

            if (visibleHeight - rootViewVisibleHeight > 200) {
                onSoftKeyBoardChangeListener?.keyBoardHide(visibleHeight - rootViewVisibleHeight)
                rootViewVisibleHeight = visibleHeight
                return@OnGlobalLayoutListener
            }
        })
    }

    fun setOnSoftKeyBoardChangeListener(listener: OnSoftKeyBoardChangeListener) {
        onSoftKeyBoardChangeListener = listener
    }

    interface OnSoftKeyBoardChangeListener {
        fun keyBoardShow(height: Int)
        fun keyBoardHide(height: Int)
    }

    companion object {
        fun setListener(activity: Activity, listener: OnSoftKeyBoardChangeListener) {
            val softKeyBoardListener = SoftKeyBoardListener(activity)
            softKeyBoardListener.setOnSoftKeyBoardChangeListener(listener)
        }
    }
}
