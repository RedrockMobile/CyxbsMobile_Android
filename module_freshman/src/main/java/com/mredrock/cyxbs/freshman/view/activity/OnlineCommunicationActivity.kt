package com.mredrock.cyxbs.freshman.view.activity

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.util.listener.FreshmanOnMainTabSelectedListener
import com.mredrock.cyxbs.freshman.view.adapter.OnlineCommunicationPagerAdapter
import kotlinx.android.synthetic.main.freshman_activity_online_communication.*

/**
 * Create by yuanbing
 * on 2019/8/3
 * 在线交流
 */
class OnlineCommunicationActivity : BaseActivity() {
    override val isFragmentActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.freshman_activity_online_communication)

        initToolbar()
        initViewPager()
        initTabLayout()
    }

    private fun initToolbar() {
        common_toolbar.init(
                title = resources.getString(R.string.freshman_online_communication),
                listener = View.OnClickListener { finish() }
        )
    }

    private fun initViewPager() {
        val manager = supportFragmentManager
        vp_online_communication.adapter = OnlineCommunicationPagerAdapter(manager)
        vp_online_communication.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {  }
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {  }
                    override fun onPageSelected(position: Int) {
                        tl_online_communication.getTabAt(position)?.select()
                    }
                }
        )
    }

    private fun initTabLayout() {
        tl_online_communication.addOnTabSelectedListener(
            object : FreshmanOnMainTabSelectedListener() {
                override fun doOnTabSelected(p0: TabLayout.Tab) {
                    vp_online_communication.currentItem = p0.position
                }
            }
        )
        tl_online_communication.getTabAt(0)?.select()
    }
}