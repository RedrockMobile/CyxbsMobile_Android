package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.COURSE_OTHER_COURSE
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.course.R

@Route(path = COURSE_OTHER_COURSE)
class OthersCourseActivity : BaseActivity() {

    companion object {
        const val OTHERS_STU_NUM = "others_stu_num"
    }

    @Autowired(name = "stuNum")
    @JvmField
    var mOthersStuNum: String = ""

    override val isFragmentActivity: Boolean
        get() = true

    // 用于记录当前Toolbar要显示的字符串
    private lateinit var mToolbarTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_activity_others_course)
        common_toolbar.init("同学课表")
        ARouter.getInstance().inject(this)
        replaceFragment(CourseContainerEntryFragment.getOthersCourseContainerFragment(mOthersStuNum))

    }


    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl, fragment)
        transaction.commit()
    }

}
