package com.mredrock.cyxbs.qa.pages.quiz.ui.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.mredrock.cyxbs.qa.R

class RewardSetAdapter(list: List<Int>) : PagerAdapter() {
    private val dataList = list

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return LayoutInflater.from(container.context).inflate(R.layout.qa_view_pager_item_reward, container, false).apply {
            findViewById<TextView>(R.id.tv_reward_count).text = dataList[position].toString()
            container.addView(this)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}