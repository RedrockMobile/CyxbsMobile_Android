package com.mredrock.cyxbs.mine.util.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_activity_tablayout_common.*

/**
 * Created by roger on 2019/12/3
 */
abstract class BaseTabLayoutActivity(override val isFragmentActivity: Boolean = false) : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_tablayout_common)
        mine_tl_toolbar.init("")
    }



    fun init(fragmentList: List<Fragment>, titleList: List<String>) {

        //设置viewPager
        mine_tl_view_pager.adapter = TabPagerAdapter(supportFragmentManager, fragmentList, titleList)
        mine_tl_view_pager.offscreenPageLimit = fragmentList.size

        mine_tl_tablayout.tabMode = TabLayout.MODE_FIXED
        mine_tl_tablayout.setupWithViewPager(mine_tl_view_pager)

    }
}