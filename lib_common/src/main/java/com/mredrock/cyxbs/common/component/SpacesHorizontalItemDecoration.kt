package com.mredrock.cyxbs.common.component

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * @author Jovines
 * create 2020-09-26 5:39 PM
 * description: RecyclerView 的间隔工具类
 * @param space 间隔，这里是像素
 * 使用方式 RecyclerView.addItemDecoration(SpacesItemDecoration(space)))
 */
class SpacesHorizontalItemDecoration(private val space: Int) : ItemDecoration() {
    override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        outRect.left = space
        if (parent.getChildAdapterPosition(view) == 0) outRect.left = 0
    }

    fun attach(recyclerView: RecyclerView) {
        var isNotExist = true
        for (i in 0 until recyclerView.itemDecorationCount) {
            val itemDecorationAt = recyclerView.getItemDecorationAt(i)
            if (itemDecorationAt is SpacesHorizontalItemDecoration) {
                isNotExist = false
                break
            }
        }
        if (isNotExist) {
            recyclerView.addItemDecoration(this)
        }
    }
}