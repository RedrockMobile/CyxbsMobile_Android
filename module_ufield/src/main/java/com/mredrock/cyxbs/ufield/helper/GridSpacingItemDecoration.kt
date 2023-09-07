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
 *  count = 1 或者 2
 *  为1的时候是单列的分布
 *  为2的时候是双列的分布
 *  为3的时候是为审核界面提供的特殊情况
 *
 *
 *  其实只看核心的代码的话，原理很简单，跟双列分布的注释比较详细
 *
 *
 *
 *
 *  author : lytMoon
 *  date : 2023/8/21 16:30
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */

class GridSpacingItemDecoration(
    private val count: Int,
) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        if (count == 2) {
            val spanCount = 2  // 假设是两列的布局
            //因为有奇数的可能，所以向上取整(行数)
            val rowCount = kotlin.math.ceil(itemCount.toDouble() / spanCount).toInt()

            // 计算当前item所在的行数和列数
            val row = position / spanCount
            val column = position % spanCount


            // 如果是最后一行，则设置底部间距
            if (row == rowCount - 1) {
                outRect.bottom = 80
            }
            /**
             *
             * 这里重点就是两边间距的比例，因为约束布局按照比例来的
             */

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
        if (count == 1) {
            if (position == itemCount - 1) {
                outRect.bottom = 140
            }
        }

        /**
         * 这是一种特殊情况，为审核界面提供，为最后一项设置到底部120dp的间距
         */
        if (count == 3) {
            if (position == itemCount - 1) {
                outRect.bottom = 120
            }
        }

    }

}