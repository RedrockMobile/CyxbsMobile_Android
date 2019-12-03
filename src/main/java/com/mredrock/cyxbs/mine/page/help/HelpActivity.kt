package com.mredrock.cyxbs.mine.page.help

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.mine.util.ui.BaseTabLayoutActivity
import kotlinx.android.synthetic.main.mine_activity_tablayout_common.*


/**
 * Created by zzzia on 2018/8/14.
 * 帮一帮
 */
class HelpActivity(override val isFragmentActivity: Boolean = false) : BaseTabLayoutActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mine_tl_tv_title.text = "我的回答"

        val fragmentList = listOf<Fragment>(HelpAdoptFm(), HelpAdoptFm())
        val titleList = listOf<String>("已回答", "草稿箱")
        init(fragmentList, titleList)
    }

//    private fun init() {
//        val overFragment = HelpAdoptFm()
//        overFragment.type = overFragment.TYPE_ADOPT_OVER
//        val waitFragment = HelpAdoptFm()
//        waitFragment.type = waitFragment.TYPE_ADOPT_WAIT
//
//        val fragmentList = listOf(overFragment, waitFragment)
//        val titleList = listOf("已解决", "待解决")
//
//        //设置viewPager
//        mine_help_view_pager.adapter = TabPagerAdapter(supportFragmentManager, fragmentList, titleList)
//        mine_help_view_pager.offscreenPageLimit = fragmentList.size
//
//        mine_help_tab_layout.tabMode = TabLayout.MODE_FIXED
//        mine_help_tab_layout.setupWithViewPager(mine_help_view_pager)
//
//        //设置tabLayout，隐藏小红点
//        for (i in 0 until mine_help_tab_layout.tabCount) {
//            val view = LayoutInflater.from(this).inflate(R.layout.mine_item_tablayout_with_point, null)
//            view.mine_item_tv_tab_title.text = titleList[i]
//            view.mine_item_iv_tab_red.visibility = View.GONE
//            mine_help_tab_layout.getTabAt(i)!!.customView = view
//        }
//    }
}
