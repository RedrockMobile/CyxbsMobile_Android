package com.mredrock.cyxbs.mine.util.widget

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by roger on 2019/12/11
 *
 * Put spaces between items.
 *  @param spacing The width of spaces.
 */

class SpaceDecoration(
        @Px
        private val spacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        outRect.set(spacing, spacing, spacing, spacing)
    }
}