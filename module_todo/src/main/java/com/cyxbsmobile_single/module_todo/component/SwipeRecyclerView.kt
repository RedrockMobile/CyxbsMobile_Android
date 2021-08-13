package com.cyxbsmobile_single.module_todo.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Author: RayleighZ
 * Time: 2021-08-12 20:08
 * 可侧滑展示删除按钮的RecyclerView
 */
class SwipeRecyclerView(context: Context) : RecyclerView(context) {

    var curItemPosition = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent?): Boolean {

        e ?: return super.onTouchEvent(e)

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                //当按下的时候，需要获取当前是那一条item
                //计算选中哪个Item
                val rect = Rect()
                val firstPosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                for (i in 0 until childCount) {
                    val child = getChildAt(i)
                    child?.apply {
                        //如果是可见的
                        if (visibility == View.VISIBLE) {
                            getHitRect(rect)
                            if (rect.contains(x.toInt(), y.toInt())){
                                curItemPosition = i + firstPosition
                            }
                        }
                    }
                }
            }
        }

        return super.onTouchEvent(e)
    }


}