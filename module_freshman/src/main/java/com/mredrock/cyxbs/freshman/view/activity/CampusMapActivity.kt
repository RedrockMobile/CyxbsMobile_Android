package com.mredrock.cyxbs.freshman.view.activity

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.util.listener.FreshmanOnMainTabSelectedListener
import com.mredrock.cyxbs.freshman.view.adapter.CampusMapPagerAdapter
import org.jetbrains.anko.find

/**
 * Create by roger
 * 指路重邮
 * on 2019/8/3
 */
class CampusMapActivity : BaseActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    override val isFragmentActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.freshman_activity_campus_map)
        initToolbar()
        tabLayout = find(R.id.tl_campus_map_activity)
        viewPager = find(R.id.vp_campus_map)
        initViewPager()
    }


    private fun initToolbar() {
        common_toolbar.init(
                title = resources.getString(R.string.freshman_campus_map),
                listener = View.OnClickListener { finish() }
        )
    }

    private fun initViewPager() {
        val adapter = CampusMapPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(object : FreshmanOnMainTabSelectedListener() {
            override fun doOnTabSelected(p0: TabLayout.Tab) {
            }
        })
        tabLayout.getTabAt(0)?.select()
    }
}