package com.mredrock.cyxbs.qa.pages.dynamic.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.qa.R

/**
 * @class
 * @author YYQF
 * @data 2021/9/18
 * @description
 **/
class HeaderBehavior(context: Context, attr: AttributeSet) :
    CoordinatorLayout.Behavior<View>(context, attr) {
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency.id == R.id.rl_tab_view_pager
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        child.findViewById<RecyclerView>(R.id.qa_rv_circles_List).apply {
            repeat(adapter!!.itemCount) {
                findViewHolderForAdapterPosition(it)?.itemView?.let { itemView ->
                    itemView.alpha = (dependency.translationY - top) / measuredHeight
                    itemView.scaleX = (dependency.translationY - top) / measuredHeight
                    itemView.scaleY = (dependency.translationY - top) / measuredHeight
                }
            }
        }
        return true
    }
}