package com.mredrock.cyxbs.mine.page.aboutme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.util.ui.TabPagerAdapter
import kotlinx.android.synthetic.main.mine_activity_about_me.*
import kotlinx.android.synthetic.main.mine_item_tablayout_with_point.view.*


/**
 * Created by zzzia on 2018/8/14.
 * 与我相关
 */
class AboutMeActivity : BaseActivity() {
    override val isFragmentActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_about_me)

        common_toolbar.init("与我相关")

        init()
    }

    private fun init() {
        //初始化Fragment
        val all = AboutMeFragment()
        all.setType(AboutMeFragment.ALL)
        val remark = AboutMeFragment()
        remark.setType(AboutMeFragment.REMARK)
        val star = AboutMeFragment()
        star.setType(AboutMeFragment.STAR)

        val fragmentList = listOf(all, remark, star)
        val titleList = listOf("全部", "评论", "点赞")

        //设置viewPager
        mine_aboutme_view_pager.adapter = TabPagerAdapter(supportFragmentManager, fragmentList, titleList)
        mine_aboutme_view_pager.offscreenPageLimit = 2

        mine_aboutme_tab_layout.tabMode = TabLayout.MODE_FIXED
        mine_aboutme_tab_layout.setupWithViewPager(mine_aboutme_view_pager)

        //设置tabLayout，隐藏小红点
        for (i in 0 until mine_aboutme_tab_layout.tabCount) {
            val view = LayoutInflater.from(this).inflate(R.layout.mine_item_tablayout_with_point, null)
            view.mine_item_tv_tab_title.text = titleList[i]
            view.mine_item_iv_tab_red.visibility = View.GONE
            mine_aboutme_tab_layout.getTabAt(i)!!.customView = view
        }
    }

}
