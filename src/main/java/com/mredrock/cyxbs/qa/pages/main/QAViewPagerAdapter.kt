package com.mredrock.cyxbs.qa.pages.main

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mredrock.cyxbs.qa.pages.question.ui.QuestionListFragment

/**
 * Created By jay68 on 2018/8/22.
 */
class QAViewPagerAdapter(private val fragments: List<QuestionListFragment>,
                         fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {
    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size

    override fun getPageTitle(position: Int) = fragments[position].title
}