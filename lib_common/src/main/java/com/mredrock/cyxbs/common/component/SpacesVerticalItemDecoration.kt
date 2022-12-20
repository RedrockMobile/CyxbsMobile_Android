package com.mredrock.cyxbs.common.component

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Jovines
 * create 2020-09-26 5:56 PM
 * description:
 */
class SpacesVerticalItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        outRect.top = space
        if (parent.getChildAdapterPosition(view) == 0) outRect.top = 0
    }

    fun attach(recyclerView: RecyclerView) {
        var isNotExist = true
        for (i in 0 until recyclerView.itemDecorationCount) {
            val itemDecorationAt = recyclerView.getItemDecorationAt(i)
            if (itemDecorationAt is SpacesVerticalItemDecoration) {
                isNotExist = false
                break
            }
        }
        if (isNotExist) {
            recyclerView.addItemDecoration(this)
        }
    }
}