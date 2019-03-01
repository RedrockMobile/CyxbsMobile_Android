package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.course.R

class OthersCourseActivity : BaseActivity() {

    companion object {
        const val OTHERS_STU_NUM = "others_stu_num"
    }

    private lateinit var mOthersStuNum: String

    override val isFragmentActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_activity_others_course)

        mOthersStuNum = intent.getStringExtra(OTHERS_STU_NUM)
        replaceFragment(CourseContainerFragment.getOthersCourseContainerFragment(mOthersStuNum))
    }


    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl, fragment)
        transaction.commit()
    }
}
