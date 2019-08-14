package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.COURSE_NO_COURSE_INVITE
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.NoCourseInviteVPAdapter
import com.mredrock.cyxbs.course.databinding.CourseActivityNoCourseInviteBinding
import com.mredrock.cyxbs.course.viewmodels.NoCourseInviteViewModel
import org.jetbrains.anko.toast

@Route(path = COURSE_NO_COURSE_INVITE)
class NoCourseInviteActivity : BaseActivity() {

    companion object {
        private const val TAG = "NoCourseInviteActivity"

        const val STU_NUM_LIST = "stu_num_list"
        const val STU_NAME_LIST = "stu_name_list"
    }

    private lateinit var mNoCourseInviteViewModel: NoCourseInviteViewModel

    @Autowired(name = "stuNumList")
    @JvmField
    var mStuNumList: ArrayList<String> = arrayListOf()

    @Autowired(name = "stuNameList")
    @JvmField
    var mNameList: ArrayList<String> = arrayListOf()

    private lateinit var mBinding: CourseActivityNoCourseInviteBinding
    private lateinit var mNoCourseInviteVPAdapter: NoCourseInviteVPAdapter

    override val isFragmentActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.course_activity_no_course_invite)
        initActivity()
    }

    private fun initActivity() {
        common_toolbar.init(getString(R.string.course_no_courese_invite))

//        mStuNumList = mutableListOf("2016215039", "2016211541")
//        mNameList = mutableListOf("文一鹏", "姜子来")
        ARouter.getInstance().inject(this)

        mNoCourseInviteViewModel = ViewModelProviders.of(this,
                NoCourseInviteViewModel.Factory(mStuNumList, mNameList)).get(NoCourseInviteViewModel::class.java)
        mNoCourseInviteViewModel.getCourses()

        //为适应获取的当前周会超出已给周数的问题进行判断
        resources.getStringArray(R.array.course_course_weeks_strings).let {
            val calendarWeek = SchoolCalendar().weekOfTerm
            val nowWeek = if (calendarWeek < 0) {
                0
            } else {
                if (calendarWeek > it.size - 1) it.size - 1 else calendarWeek
            }
            it[nowWeek] = getString(R.string.course_now_week)
            mNoCourseInviteVPAdapter = NoCourseInviteVPAdapter(it.drop(1), supportFragmentManager)
        }

        mBinding.viewPager.adapter = mNoCourseInviteVPAdapter
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager)
        mBinding.viewPager.currentItem = SchoolCalendar().weekOfTerm - 1
    }
}
