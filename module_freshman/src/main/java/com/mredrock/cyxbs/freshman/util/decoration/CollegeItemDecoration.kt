package com.mredrock.cyxbs.freshman.util.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import org.jetbrains.anko.dip

class CollegeItemDecoration(private val mFooterMarginBottom: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val count = parent.adapter?.itemCount ?: 0
        val position = parent.getChildAdapterPosition(view)
        if (position == count - 1) {
            outRect.set(0, 0, 0, BaseApp.context.dip(mFooterMarginBottom))
        }
    }
}