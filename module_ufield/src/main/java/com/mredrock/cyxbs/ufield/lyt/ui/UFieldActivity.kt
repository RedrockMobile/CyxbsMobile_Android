package com.mredrock.cyxbs.ufield.lyt.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.adapter.FragmentVpAdapter
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lyt.fragment.ufieldfragment.AllFragment
import com.mredrock.cyxbs.ufield.lyt.fragment.ufieldfragment.CultureFragment
import com.mredrock.cyxbs.ufield.lyt.fragment.ufieldfragment.EducationFragment
import com.mredrock.cyxbs.ufield.lyt.fragment.ufieldfragment.SportsFragment
import com.mredrock.cyxbs.ufield.lyt.viewmodel.ui.UFieldViewModel

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
    private val mAdmin: ImageView by R.id.uField_activity_isAdmin.view()
    private val mCup: ImageView by R.id.uField_cup.view()

    private val mTabLayout: TabLayout by R.id.uField_tabLayout.view()
    private val mVp: ViewPager2 by R.id.uField_viewpager2.view()
    private val mViewModel by viewModels<UFieldViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ufield_activity_ufield)
        iniTab()
        iniIsAdmin()
        iniClick()

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
                0 -> tab.text = "全部活动"
                1 -> tab.text = "文娱活动"
                2 -> tab.text = "体育活动"
                else -> tab.text = "教育活动"
            }
        }.attach()
        //让初始化的第一个先变色
        (mTabLayout.getChildAt(0) as ViewGroup).getChildAt(0)
            .setBackgroundResource(R.drawable.ufield_ic_tab_shape_selected)
        for (i in 0 until mTabLayout.tabCount) {
            val tab = (mTabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val params = tab.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(10, params.topMargin, 10, params.bottomMargin)
            tab.requestLayout()
        }
        /**
         * 实现滑动到特定区域和没有的颜色区分
         */
        mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.view.setBackgroundResource(R.drawable.ufield_ic_tab_shape_selected)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.view.setBackgroundResource(R.drawable.ufield_ic_tab_shape)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


    }

    /**
     * 判读是否为管理员账号，动态显示图标
     */
    private fun iniIsAdmin() {
        mViewModel.apply {
            getIsAdmin()
            isAdmin.observe(this@UFieldActivity) {
                if (it[0].admin) {
                    mAdmin.visibility = View.VISIBLE
                }

            }
        }
    }

    /**
     * 由于点击事件比较多而且多为跳转事件，所以这里集中处理一下
     */
    private fun iniClick() {
        mBack.setOnClickListener { finish() }
        mSearch.setOnClickListener {

        }
        mAdmin.setOnClickListener {

        }
        mAdd.setOnClickListener {

        }
        mCup.setOnClickListener {

        }

    }


}