package com.mredrock.cyxbs.ufield.lyt.ui

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.adapter.FragmentVpAdapter
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lyt.fragment.AllFragment
import com.mredrock.cyxbs.ufield.lyt.fragment.CultureFragment
import com.mredrock.cyxbs.ufield.lyt.fragment.DoneFragment
import com.mredrock.cyxbs.ufield.lyt.fragment.EducationFragment
import com.mredrock.cyxbs.ufield.lyt.fragment.SportsFragment
import com.mredrock.cyxbs.ufield.lyt.fragment.TodoFragment

/**
 * description ：最初从中心模块跳转到这个activity
 * author : lytMoon
 * email : yytds@foxmail.com
 * date : 2023/8/7 19:49
 * version: 1.0
 */
class UFieldActivity : BaseActivity() {

    private val mBack: ImageView by R.id.uField_back.view()
    private val mSearch: ImageView by R.id.uField_search.view()
    private val mAdd: ImageView by R.id.uField_add.view()
    private val mTabLayout: TabLayout by R.id.uField_tabLayout.view()
    private val mVp: ViewPager2 by R.id.uField_viewpager2.view()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ufield_activity_ufield)
        iniTab()
    }

    /**
     * 初始化tabLayout
     */
    private fun iniTab() {
        mVp.adapter = FragmentVpAdapter(this)
            .add { AllFragment() }
            .add { CultureFragment() }
            .add { SportsFragment() }
            .add { EducationFragment() }
        TabLayoutMediator(mTabLayout, mVp) { tab, position ->
            when (position) {
                0 -> tab.text = "全部"
                1 -> tab.text = "文娱活动"
                3 -> tab.text = "体育活动"
                else -> tab.text = "教育活动"
            }
        }.attach()


    }


}