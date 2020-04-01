package com.mredrock.cyxbs.qa.pages.main

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mredrock.cyxbs.qa.pages.question.ui.QuestionListFragment

/**
 * Created By jay68 on 2018/8/22.
 */
class QAViewPagerAdapter(private val fragments: List<QuestionListFragment>,
                         fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size

    override fun getPageTitle(position: Int) = fragments[position].arguments?.getString(QuestionListFragment.FRAGMENT_TITLE)
}