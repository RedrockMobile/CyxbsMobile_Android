package com.mredrock.cyxbs.mine.page.aboutme

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.mine.util.ui.BaseTabLayoutActivity
import kotlinx.android.synthetic.main.mine_activity_tablayout_common.*


/**
 * Created by zzzia on 2018/8/14.
 * 与我相关
 */
class AboutMeActivity : BaseTabLayoutActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mine_tl_tv_title.text = "评论回复"

        val fragmentList = listOf<Fragment>(AboutMeFragment(), AboutMeFragment())
        val titleList = listOf<String>("发出评论", "收到回复")
        init(fragmentList, titleList)
    }

//    private fun init() {
//        //初始化Fragment
//        val all = AboutMeFragment()
//        all.setType(AboutMeFragment.ALL)
//        val remark = AboutMeFragment()
//        remark.setType(AboutMeFragment.REMARK)
//        val star = AboutMeFragment()
//        star.setType(AboutMeFragment.STAR)
//
//        val fragmentList = listOf(all, remark, star)
//        val titleList = listOf("全部", "评论", "点赞")
//
//        //设置viewPager
//        mine_aboutme_view_pager.adapter = TabPagerAdapter(supportFragmentManager, fragmentList, titleList)
//        mine_aboutme_view_pager.offscreenPageLimit = 2
//
//        mine_aboutme_tab_layout.tabMode = TabLayout.MODE_FIXED
//        mine_aboutme_tab_layout.setupWithViewPager(mine_aboutme_view_pager)
//
//        //设置tabLayout，隐藏小红点
//        for (i in 0 until mine_aboutme_tab_layout.tabCount) {
//            val view = LayoutInflater.from(this).inflate(R.layout.mine_item_tablayout_with_point, null)
//            view.mine_item_tv_tab_title.text = titleList[i]
//            view.mine_item_iv_tab_red.visibility = View.GONE
//            mine_aboutme_tab_layout.getTabAt(i)!!.customView = view
//        }
//    }

}
