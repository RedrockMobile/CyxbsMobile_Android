package com.mredrock.cyxbs.grades.ui.main

import android.os.Bundle
import com.jude.swipbackhelper.SwipeBackHelper
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.grades.R
import com.mredrock.cyxbs.grades.ui.adapter.FPAdapter
import com.mredrock.cyxbs.grades.ui.exam.ExamFragment
import com.mredrock.cyxbs.grades.ui.grades.GradesFragment
import kotlinx.android.synthetic.main.grades_activity_container.*

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
class ContainerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grades_activity_container)
        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false)//滑动会影响体验
        common_toolbar.init("考试与成绩")
        initVP()
    }

    override val isFragmentActivity = true

    private fun initVP() {
        val examFragment = ExamFragment()
        val makeUpFragment = ExamFragment()
        examFragment.kind = "考试安排"
        makeUpFragment.kind = "补考安排"

        val fragments = listOf(GradesFragment(), examFragment, makeUpFragment)
        val titles = listOf("成绩", "考试安排", "补考安排")

        if (supportFragmentManager != null && titles.isNotEmpty() && fragments.isNotEmpty()) {
            vp_grades.adapter = FPAdapter(supportFragmentManager, titles, fragments)
        }

        vp_grades.offscreenPageLimit = 3
        vp_grades.currentItem = 0

        tl_grades.setupWithViewPager(vp_grades)

    }
}
