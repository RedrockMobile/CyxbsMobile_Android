package com.mredrock.cyxbs.discover.emptyroom.ui.widget

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * Created by Cynthia on 2018/9/19
 */
class DefaultItemDecoration(@Px head: Int, @Px middle: Int, @Px tail: Int) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
    private var mHead: Int = head
    private var mMiddle: Int = middle
    private var mTail: Int = tail

    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager && layoutManager.orientation == LinearLayoutManager.HORIZONTAL) {
            horizontalLinearLayoutManager(outRect, view, parent, state)
        }
    }

    private fun horizontalLinearLayoutManager(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)
        when (position) {
            0 -> outRect.set(mHead, 0, mMiddle, 0)
            state.itemCount - 1 -> outRect.set(mMiddle, 0, mTail, 0)
            else -> outRect.set(mMiddle, 0, mMiddle, 0)
        }
    }

    //更多布局的支持需要时再说。。。
}