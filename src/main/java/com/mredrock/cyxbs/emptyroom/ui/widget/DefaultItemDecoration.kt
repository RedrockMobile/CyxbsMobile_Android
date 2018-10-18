package com.mredrock.cyxbs.emptyroom.ui.widget

import android.graphics.Rect
import android.support.annotation.Px
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Cynthia on 2018/9/19
 */
class DefaultItemDecoration(@Px head: Int, @Px middle: Int, @Px tail: Int) : RecyclerView.ItemDecoration() {
    private var mHead: Int = head
    private var mMiddle: Int = middle
    private var mTail: Int = tail


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager && layoutManager.orientation == LinearLayoutManager.HORIZONTAL) {
            horizontalLinearLayoutManager(outRect, view, parent, state)
        }
    }

    private fun horizontalLinearLayoutManager(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)
        when (position) {
            0 -> outRect.set(mHead, 0, mMiddle, 0)
            state.itemCount - 1 -> outRect.set(mMiddle, 0, mTail, 0)
            else -> outRect.set(mMiddle, 0, mMiddle, 0)
        }
    }

    //更多布局的支持需要时再说。。。
}