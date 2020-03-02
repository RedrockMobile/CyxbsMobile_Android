package com.mredrock.cyxbs.discover.othercourse.pages.stusearch

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.config.DISCOVER_OTHER_COURSE
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.discover.othercourse.OtherCourseViewPagerAdapter
import com.mredrock.cyxbs.discover.othercourse.R
import kotlinx.android.synthetic.main.othercourse_discover_activity_other_course.*

@Route(path = DISCOVER_OTHER_COURSE)
class OtherCourseActivity : BaseViewModelActivity<OtherCourseViewModel>() {
    override val viewModelClass = OtherCourseViewModel::class.java

    override val isFragmentActivity = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.othercourse_discover_activity_other_course)
        common_toolbar.initWithSplitLine("查课表",false)
        common_toolbar.setTitleLocationAtLeft(false)

        vp_other_course.adapter = OtherCourseViewPagerAdapter(this)
        TabLayoutMediator(tl_other_course, vp_other_course) { tab, position ->
            when (position) {
                0 -> tab.text = "同学课表"
                1 -> tab.text = "老师课表"
            }
        }.attach()

    }
}
