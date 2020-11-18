package com.mredrock.cyxbs.mine.util.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_activity_tablayout_common.*

/**
 * Created by roger on 2019/12/3
 */
abstract class BaseTabLayoutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_tablayout_common)
        common_toolbar.initWithSplitLine("", false)
    }



    fun init(fragmentList: List<Fragment>, titleList: List<String>) {

        //设置viewPager
        mine_tl_view_pager.adapter = TabPagerAdapter(supportFragmentManager, fragmentList, titleList)
        mine_tl_view_pager.offscreenPageLimit = fragmentList.size

        mine_tl_tablayout.tabMode = TabLayout.MODE_FIXED
        mine_tl_tablayout.setupWithViewPager(mine_tl_view_pager)

    }

    /**
     * 由于Arouter的fragment跳转不会调用fragment的onActivityResult,具体见这篇文章：
     * https://juejin.im/post/5c31d4bff265da61307506a6
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val allFragments: List<Fragment>? = supportFragmentManager.fragments
        if (allFragments != null) {
            for (fragment in allFragments) {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}