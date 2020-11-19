package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.animation.ValueAnimator
import android.view.View
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_label.view.*


class CircleLabelAdapter(val labelList: ArrayList<String>) : RedRockAutoWarpView.Adapter() {
    override fun getItemCount(): Int {
        return labelList.size
    }

    override fun getItemId(position: Int): Int {
        return R.layout.qa_recycler_item_dynamic_label
    }

    override fun initItem(item: View, position: Int) {
        item.tv_dynamic_label.apply {
            text = labelList[position]
        }
        //点击动画效果
        item.tv_dynamic_label.setOnSingleClickListener {
            val animScale = ValueAnimator.ofFloat(1f, 0.8f, 1f)
            animScale.duration = 500
            animScale.addUpdateListener {
                item.tv_dynamic_label.scaleX = it.animatedValue as Float
                item.tv_dynamic_label.scaleY = it.animatedValue as Float
            }
            animScale.start()
        }
    }

}