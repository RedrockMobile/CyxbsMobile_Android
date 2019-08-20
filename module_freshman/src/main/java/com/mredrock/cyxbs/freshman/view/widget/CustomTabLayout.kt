package com.mredrock.cyxbs.freshman.view.widget

import android.graphics.Typeface
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.freshman.R

/**
 * Create by yuanbing
 * on 2019/8/12
 */
class CustomTabLayout(
    mTab: ConstraintLayout
) {
    var mCurrentTab: Int = 0
    private var mListener: ((Int) -> Unit)? = null
    private val mTabs: List<TextView> = listOf(
            mTab.findViewById(R.id.tv_custom_tab_1),
            mTab.findViewById(R.id.tv_custom_tab_2),
            mTab.findViewById(R.id.tv_custom_tab_3),
            mTab.findViewById(R.id.tv_custom_tab_4)
    )

    init {
        for (i in 0 until mTabs.size) {
            val textView = mTabs[i]
            textView.setOnClickListener { select(i) }
        }
        select(mCurrentTab)
    }

    private fun select(position: Int) {
        mCurrentTab = position
        val selectedTab = mTabs[mCurrentTab]
        selectedTab.typeface = Typeface.DEFAULT_BOLD
        selectedTab.setTextColor(BaseApp.context.resources.getColor(R.color.freshman_tab_selected_color))
        for (i in 0 until mTabs.size) {
            if (i == mCurrentTab) {
                continue
            } else {
                val textView = mTabs[i]
                textView.typeface = Typeface.DEFAULT
                textView.setTextColor(BaseApp.context.resources.getColor(R.color.freshman_custom_tab_layout_tab_text_color))
            }
        }
        mListener?.let { it(position) }
    }

    fun addOnTabSelectedListener(listener: (Int) -> Unit) {
        mListener = listener
    }
}