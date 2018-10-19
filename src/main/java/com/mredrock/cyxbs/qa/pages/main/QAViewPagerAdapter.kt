package com.mredrock.cyxbs.qa.pages.main

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.mredrock.cyxbs.qa.pages.question.ui.QuestionListFragment

/**
 * Created By jay68 on 2018/8/22.
 */
class QAViewPagerAdapter(private val fragments: List<QuestionListFragment>,
                         fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size

    override fun getPageTitle(position: Int) = fragments[position].title
}