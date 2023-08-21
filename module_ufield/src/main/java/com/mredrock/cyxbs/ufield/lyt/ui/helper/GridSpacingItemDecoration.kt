package com.mredrock.cyxbs.ufield.lyt.ui.helper

import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 *  description :用于辅助Rv行列的分布。控制间距大小
 *
 *  使用方法如下
 *  在正常写完Rv后加入如下代码
 *  mRv.addItemDecoration(GridSpacingItemDecoration(spanCount, spacingDp, includeEdge))
 *  spanCount：多少列
 *  spacingDp：间距的dp（默认设置上下间距）
 *  includeEdge：是否包括边距，为true的时候，左右也会有spacingDp
 *
 *
 *  author : lytMoon
 *  date : 2023/8/21 16:30
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacingDp: Int,
    private val includeEdge: Boolean
) :
    RecyclerView.ItemDecoration() {
    private var spacingPx: Int = 0
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (spacingPx == 0) {
            spacingPx = dpToPx(view.context, spacingDp)
        }

        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.left = column * spacingPx / spanCount
        outRect.right = spacingPx - (column + 1) * spacingPx / spanCount

        if (includeEdge) {
            outRect.left = spacingPx - column * spacingPx / spanCount
            outRect.right = (column + 1) * spacingPx / spanCount
        } else {
            outRect.left = column * spacingPx / spanCount
            outRect.right = spacingPx - (column + 1) * spacingPx / spanCount
        }

        if (position >= spanCount) {
            outRect.top = spacingPx
        }
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return (dp * displayMetrics.density).toInt()
    }
}