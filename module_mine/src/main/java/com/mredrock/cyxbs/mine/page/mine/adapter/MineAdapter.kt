package com.mredrock.cyxbs.mine.page.mine.adapter



import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import androidx.viewpager2.adapter.FragmentStateAdapter

class MineAdapter(context: FragmentActivity,val list: List<Fragment>): FragmentStateAdapter(context) {

    override fun getItemCount()=list.size

    override fun createFragment(position: Int)= list[position]

}