package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.common.config.WEEK_NUM
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.component.ScheduleView
import com.mredrock.cyxbs.course.databinding.CourseFragmentCourseBinding
import com.mredrock.cyxbs.course.databinding.CourseNoClassInviteScheduleBinding
import com.mredrock.cyxbs.course.databinding.CourseOrdinaryScheduleBinding
import com.mredrock.cyxbs.course.event.DismissAddAffairViewEvent
import com.mredrock.cyxbs.course.viewmodels.CoursePageViewModel
import com.mredrock.cyxbs.course.viewmodels.CoursesViewModel
import com.mredrock.cyxbs.course.viewmodels.NoCourseInviteViewModel
import kotlinx.android.synthetic.main.course_fragment_course.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by anriku on 2018/8/14.
 */

class CourseFragment : BaseFragment() {

    override val openStatistics: Boolean
        get() = false

    private lateinit var courseContainerEntryFragment: CourseContainerEntryFragment

    //当前课表页面代表的第几周[默认0 代表整学期，1代表第一周。。。。。。]
    private var mWeek: Int = 0
    private lateinit var mCoursesViewModel: CoursesViewModel
    private lateinit var mCoursePageViewModel: CoursePageViewModel
    //用于没课约的ViewModel
    private var mNoCourseInviteViewModel: NoCourseInviteViewModel? = null

    private lateinit var mBinding: CourseFragmentCourseBinding

    private lateinit var scheduleView: ScheduleView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        courseContainerEntryFragment = parentFragment as CourseContainerEntryFragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.course_fragment_course,
                container, false)
        mBinding.courseSv.layoutAnimation = LayoutAnimationController(AnimationUtils.loadAnimation(context, R.anim.course_nest_sc_enter))
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun initFragment() {
        mBinding.lifecycleOwner = this
        mWeek = arguments?.getInt(WEEK_NUM) ?: 0


        /**
         * 虽然这里可能有点耦合度高，但是这两个fragment一般是一起用的
         * 这里没有使用[ViewModelProviders]来获取ViewModel
         * 是因为这里必须使用[CourseContainerEntryFragment]所使用的ViewModel
         * 但是[CourseContainerEntryFragment]的ViewModel根据使用场景不一样所用的依赖的ViewModel
         * 也不一样
         */
        mCoursesViewModel = courseContainerEntryFragment.mCoursesViewModel
        mNoCourseInviteViewModel = courseContainerEntryFragment.mNoCourseInviteViewModel
        //获取生命周期与当前Fragment绑定的CoursePageViewModel
        mCoursePageViewModel = ViewModelProviders.of(this,
                CoursePageViewModel.DateViewModelFactory(mWeek)).get(CoursePageViewModel::class.java)
        mCoursePageViewModel.nowWeek = mWeek


        //获取了ViewModel之后进行一些初始化操作
        mBinding.coursesViewModel = mCoursesViewModel
        mBinding.coursePageViewModel = mCoursePageViewModel

        // 当当前周数进行了改变后有可能SchoolCalendar进行了更新，这时候就对DateViewModel中的日期进行更新
        mCoursesViewModel.schoolCalendarUpdated.observe(this, Observer {
            if (it == true) {
                mCoursePageViewModel.getDate()
            }
        })

        //根据当前课表Fragment被复用的状态，获取相应的课表视图
        scheduleView = when (courseContainerEntryFragment.courseState) {
            CourseContainerEntryFragment.CourseState.OrdinaryCourse, CourseContainerEntryFragment.CourseState.OtherCourse, CourseContainerEntryFragment.CourseState.TeacherCourse -> {
                val mBinding = DataBindingUtil.inflate<CourseOrdinaryScheduleBinding>(LayoutInflater.from(context), R.layout.course_ordinary_schedule, course_schedule_container, false)
                mBinding.coursePageViewModel = mCoursePageViewModel
                mBinding.coursesViewModel = mCoursesViewModel
                course_schedule_container.addView(mBinding.root)
                mBinding.scheduleView
            }
            CourseContainerEntryFragment.CourseState.NoClassInvitationCourse -> {
                val mBinding = DataBindingUtil.inflate<CourseNoClassInviteScheduleBinding>(LayoutInflater.from(context), R.layout.course_no_class_invite_schedule, course_schedule_container, false)
                mBinding.noCourseInviteViewModel = mNoCourseInviteViewModel
                mBinding.nowWeek = mWeek
                course_schedule_container.addView(mBinding.root)
                mBinding.scheduleView
            }
        }

        scheduleView.adapterChangeListener = {
            val position = scheduleView.adapter?.getHighLightPosition()
            week_back_ground_view.position = position
            red_rock_tv_course_day_of_week.position = position
            red_rock_tv_course_day_of_month.position = position
            course_tiv.position = position
        }
    }

    override fun onResume() {
        super.onResume()
        course_tiv.invalidate()
    }

    /**
     * 这个方法用于清理课表上加备忘的View
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun dismissAddAffairEventView(e: DismissAddAffairViewEvent) {
        scheduleView.clearTouchView()
    }
}