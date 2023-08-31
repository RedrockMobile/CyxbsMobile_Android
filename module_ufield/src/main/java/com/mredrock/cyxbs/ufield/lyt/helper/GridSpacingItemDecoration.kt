package com.mredrock.cyxbs.ufield.lyt.helper

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
 *  spacingUPDp：间距的dp（默认设置上下间距）
 *  spacingLRDp：左右间距（目前仅仅支持2列）
 *
 *
 *  author : lytMoon
 *  date : 2023/8/21 16:30
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacingUPDp: Int,
    private val spacingLRDp: Int
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
            spacingPx = dpToPx(view.context, spacingUPDp)
        }

        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.top = spacingPx
        outRect.right = if (column == 1) spacingLRDp else spacingLRDp / 2
        outRect.left = if (column == 1) spacingLRDp / 2 else spacingLRDp
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return (dp * displayMetrics.density).toInt()
    }
}