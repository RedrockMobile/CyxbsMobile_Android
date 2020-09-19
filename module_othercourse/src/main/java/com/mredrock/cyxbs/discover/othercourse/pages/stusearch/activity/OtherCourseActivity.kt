package com.mredrock.cyxbs.discover.othercourse.pages.stusearch.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.config.DISCOVER_OTHER_COURSE
import com.mredrock.cyxbs.common.config.TAB_INDEX
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.discover.othercourse.OtherCourseViewPagerAdapter
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.pages.stusearch.fragment.OtherStuCourseSearchFragment
import com.mredrock.cyxbs.discover.othercourse.pages.stusearch.fragment.OtherTeacherCourseSearchFragment
import com.mredrock.cyxbs.discover.othercourse.pages.stusearch.viewmodel.OtherCourseViewModel
import kotlinx.android.synthetic.main.othercourse_discover_activity_other_course.*

@Route(path = DISCOVER_OTHER_COURSE)
class OtherCourseActivity : BaseViewModelActivity<OtherCourseViewModel>() {
    override val viewModelClass = OtherCourseViewModel::class.java

    override val isFragmentActivity = true
    private val otherCourseStuSearchFragment by lazy(LazyThreadSafetyMode.NONE) {
        OtherStuCourseSearchFragment()
    }
    private val otherCourseTeacherSearchFragment by lazy(LazyThreadSafetyMode.NONE) {
        OtherTeacherCourseSearchFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        isSlideable = false
        super.onCreate(savedInstanceState)
        setContentView(R.layout.othercourse_discover_activity_other_course)
        common_toolbar.initWithSplitLine("查课表", false)
        common_toolbar.setTitleLocationAtLeft(false)

        vp_other_course.adapter = OtherCourseViewPagerAdapter(this, listOf(otherCourseStuSearchFragment, otherCourseTeacherSearchFragment))
        TabLayoutMediator(tl_other_course, vp_other_course) { tab, position ->
            when (position) {
                0 -> tab.text = "同学课表"
                1 -> tab.text = "老师课表"
            }
        }.attach()

        // 内部跳转协议参数处理
        val tabIndex = intent.getStringExtra(TAB_INDEX).toIntOrNull()
        tabIndex?.let { vp_other_course.setCurrentItem(it, false) }
    }
}
