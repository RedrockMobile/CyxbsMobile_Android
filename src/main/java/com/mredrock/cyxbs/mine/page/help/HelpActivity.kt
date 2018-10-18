package com.mredrock.cyxbs.mine.page.help

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.util.ui.TabPagerAdapter
import kotlinx.android.synthetic.main.mine_activity_help.*
import kotlinx.android.synthetic.main.mine_item_tablayout_with_point.view.*


/**
 * Created by zzzia on 2018/8/14.
 * 帮一帮
 */
class HelpActivity(override val isFragmentActivity: Boolean = false) : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_help)

        common_toolbar.init("帮一帮")

        init()
    }

    private fun init() {
        val overFragment = HelpAdoptFm()
        overFragment.type = overFragment.TYPE_ADOPT_OVER
        val waitFragment = HelpAdoptFm()
        waitFragment.type = waitFragment.TYPE_ADOPT_WAIT

        val fragmentList = listOf(overFragment, waitFragment)
        val titleList = listOf("已解决", "未解决")

        //设置viewPager
        mine_help_view_pager.adapter = TabPagerAdapter(supportFragmentManager, fragmentList, titleList)
        mine_help_view_pager.offscreenPageLimit = fragmentList.size

        mine_help_tab_layout.tabMode = TabLayout.MODE_FIXED
        mine_help_tab_layout.setupWithViewPager(mine_help_view_pager)

        //设置tabLayout，隐藏小红点
        for (i in 0 until mine_help_tab_layout.tabCount) {
            val view = LayoutInflater.from(this).inflate(R.layout.mine_item_tablayout_with_point, null)
            view.mine_item_tv_tab_title.text = titleList[i]
            view.mine_item_iv_tab_red.visibility = View.GONE
            mine_help_tab_layout.getTabAt(i)!!.customView = view
        }
    }
}
