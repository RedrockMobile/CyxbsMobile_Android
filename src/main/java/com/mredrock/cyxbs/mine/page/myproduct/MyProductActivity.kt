package com.mredrock.cyxbs.mine.page.myproduct

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.util.ui.TabPagerAdapter
import kotlinx.android.synthetic.main.mine_activity_tablayout_common.*

class MyProductActivity(override val isFragmentActivity: Boolean = false) : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_tablayout_my_product)
        common_toolbar.initWithSplitLine("", false, R.drawable.mine_ic_arrow_left_my_product)
        val fragmentList = listOf<Fragment>(MyProductFragment(MyProductFragment.UNCLAIMED), MyProductFragment(MyProductFragment.CLAIMED))
        val titleList = listOf("未领取", "已领取")
        init(fragmentList, titleList)
    }

    fun init(fragmentList: List<Fragment>, titleList: List<String>) {

        //设置viewPager
        mine_tl_view_pager.adapter = TabPagerAdapter(supportFragmentManager, fragmentList, titleList)
        mine_tl_view_pager.offscreenPageLimit = fragmentList.size

        mine_tl_tablayout.tabMode = TabLayout.MODE_FIXED
        mine_tl_tablayout.setupWithViewPager(mine_tl_view_pager)

    }
}
