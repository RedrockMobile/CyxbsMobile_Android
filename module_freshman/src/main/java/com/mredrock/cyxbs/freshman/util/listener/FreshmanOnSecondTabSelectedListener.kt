package com.mredrock.cyxbs.freshman.util.listener

import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.freshman.R

abstract class FreshmanOnSecondTabSelectedListener : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
    private var mIsNeedDo = true
    private var mIsUseReSelect = useReSelect()

    override fun onTabReselected(p0: TabLayout.Tab?) {
        mIsNeedDo = !mIsNeedDo
        onTabSelected(p0)
        mIsNeedDo = !mIsNeedDo
    }

    override fun onTabUnselected(p0: TabLayout.Tab?) { p0?.customView = null }

    override fun onTabSelected(p0: TabLayout.Tab?) {
        if (p0 == null) return
        if (p0.customView == null) {
            p0.setCustomView(R.layout.freshman_second_tab_layout_custom_view)
            val text: TextView = p0.customView!!.findViewById(R.id.tv_second_tab_custom_view)
            text.text = p0.text
        }
        if (mIsUseReSelect || mIsNeedDo) doOnTabSelected(p0)
    }

    fun useReSelect() = false

    abstract fun doOnTabSelected(p0: TabLayout.Tab)
}