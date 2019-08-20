package com.mredrock.cyxbs.freshman.util.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import org.jetbrains.anko.dip

/**
 * Create by yuanbing
 * on 2019/8/13
 */
class CustomSecondTabLayoutItemDecoration : RecyclerView.ItemDecoration() {
    private val mPadding = BaseApp.context.dip(12)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val count = parent.adapter?.itemCount ?: 0
        if (parent.getChildAdapterPosition(view) < count - 1) {
            outRect.set(0, 0, mPadding, 0)
        }
    }
}