package com.mredrock.cyxbs.qa.pages.search.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mredrock.cyxbs.qa.pages.search.ui.fragment.SearchHotWordFragment

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class SearchHotVpAdapter(private val fragments: List<SearchHotWordFragment>, private val titles: List<String>,
                         fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = titles.size

    override fun getPageTitle(position: Int): CharSequence? = titles[position]
}