package com.mredrock.cyxbs.main.utils

import android.widget.CheckedTextView
import com.mredrock.cyxbs.main.R
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @author  Jon
 * @date  2020/4/6 18:18
 * description：
 */
class BottomNavigationHelper(private val tabList: Array<CheckedTextView>,
                             private val selectIcons: Array<Int>,
                             private val unSelectIcons: Array<Int>,
                             private var selectedListener: ((Int) -> Unit)? = null
) {

    init {
        for ((i, value) in tabList.withIndex()) {
            value.onClick {
                value.toggle()
                if (value.isChecked) {
                    selectTab(i)
                }
            }
            value.setCompoundDrawablesWithIntrinsicBounds(0, unSelectIcons[i], 0, 0)
        }
    }


    private fun removeOtherCheckStatus(i:Int) {
        for ((p, value) in tabList.withIndex()) {
            if (p != i) {
                value.isChecked = false
                value.setCompoundDrawablesWithIntrinsicBounds(0, unSelectIcons[p], 0, 0)
            }
        }
    }

    fun selectTab(i: Int) {
        tabList[i].isChecked = true
        removeOtherCheckStatus(i)
        tabList[i].setCompoundDrawablesWithIntrinsicBounds(0, selectIcons[i], 0, 0)
        selectedListener?.invoke(i)
    }
}