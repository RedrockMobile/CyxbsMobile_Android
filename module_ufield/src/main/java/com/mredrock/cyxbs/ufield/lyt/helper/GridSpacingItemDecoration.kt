package com.mredrock.cyxbs.ufield.lyt.helper

import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.Math.ceil

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
        val spanCount = 2  // 假设是两列的布局
        val itemCount = parent.adapter?.itemCount ?: 0
        //因为有奇数的可能，所以向上取整
        val rowCount = kotlin.math.ceil(itemCount.toDouble() / spanCount).toInt()
        Log.d("965966", "测试结果-->> ${rowCount}");

        // 计算当前item所在的行数和列数
        val row = position / spanCount
        val column = position % spanCount



        // 如果是最后一行，则设置底部间距
        if (row == rowCount - 1) {
            outRect.bottom = 80
        }

        // 根据列数设置相应的间距
        if (column == 0) {
            // 第一列
            // 设置左侧间距
            outRect.left = 50
            outRect.right = 5
        } else {
            // 第二列
            // 设置右侧间距
            outRect.left = 5
            outRect.right = 50
        }

    }

    private fun dpToPx(context: Context, dp: Int): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return (dp * displayMetrics.density).toInt()
    }
}