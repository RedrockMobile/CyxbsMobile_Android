package com.mredrock.cyxbs.ufield.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 *  description :用于辅助Rv行列的分布。控制间距大小
 *
 *  使用方法如下
 *
 *  在正常写完Rv后加入如下代码
 *  mRv.addItemDecoration(GridSpacingItemDecoration(count：Int))
 *  count这里支持2 即两列的rv分布
 *  目的是为了调整两列的间距
 *
 *  author : lytMoon
 *  date : 2023/8/21 16:30
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */

class GridSpacingItemDecoration(
    private val count: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        if (count == 2) {
            val spanCount = 2
            val rowCount = kotlin.math.ceil(itemCount.toDouble() / spanCount).toInt()
            val row = position / spanCount
            val column = position % spanCount

            if (column == 0) {
                // 第一列
                outRect.left = 50
                outRect.right = 5
            } else {
                // 第二列
                outRect.left = 5
                outRect.right = 50
            }
        }
    }
}