package com.mredrock.cyxbs.course.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.common.config.WEEK_NUM
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.bindingadapter.ScheduleViewBidingAdapter
import com.mredrock.cyxbs.course.databinding.CourseFragmentCourseBinding
import com.mredrock.cyxbs.course.event.DismissAddAffairViewEvent
import com.mredrock.cyxbs.course.viewmodels.CoursePageViewModel
import com.mredrock.cyxbs.course.viewmodels.CoursesViewModel
import com.mredrock.cyxbs.course.viewmodels.NoCourseInviteViewModel
import kotlinx.android.synthetic.main.course_fragment_course.*
import kotlinx.android.synthetic.main.course_fragment_course.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.textColor


/**
 * Created by anriku on 2018/8/14.
 */

class CourseFragment : BaseFragment() {


    override val openStatistics: Boolean
        get() = false

    lateinit var courseContainerEntryFragment:CourseContainerEntryFragment

    //当前课表页面代表的第几周[默认0 代表整学期，1代表第一周。。。。。。]
    private var mWeek: Int = 0
    private lateinit var mCoursesViewModel: CoursesViewModel
    private lateinit var mCoursePageViewModel: CoursePageViewModel
    //用于没课约的ViewModel
    private var mNoCourseInviteViewModel: NoCourseInviteViewModel? = null

    private lateinit var mBinding: CourseFragmentCourseBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        courseContainerEntryFragment = parentFragment as CourseContainerEntryFragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.course_fragment_course,
                container, false)
        mBinding.scheduleView.layoutAnimation = LayoutAnimationController(AnimationUtils.loadAnimation(context,R.anim.course_schedule_view))
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun initFragment() {
        mBinding.lifecycleOwner = this
        mWeek = arguments?.getInt(WEEK_NUM) ?: 0

        activity?.let { activity ->
            /**
             * 这里没有使用[ViewModelProviders]来获取ViewModel
             * 是因为这里必须使用[CourseContainerEntryFragment]所使用的ViewModel
             * 但是[CourseContainerEntryFragment]的ViewModel根据使用场景不一样所用的依赖的ViewModel
             * 也不一样
             */
            mCoursesViewModel = courseContainerEntryFragment.mCoursesViewModel
            mNoCourseInviteViewModel = courseContainerEntryFragment.mNoCourseInviteViewModel
            //获取生命周期与当前Fragment绑定的DateViewModel
            mCoursePageViewModel = ViewModelProviders.of(this,
                    CoursePageViewModel.DateViewModelFactory(mWeek)).get(CoursePageViewModel::class.java)
            mCoursePageViewModel.nowWeek = mWeek

            val color = ContextCompat.getColor(activity, R.color.levelOneFontColor)
            red_rock_tv_course_day_of_month.textColor = Color.argb(153, Color.red(color), Color.green(color), Color.blue(color))
            when(courseContainerEntryFragment.courseState){
                CourseContainerEntryFragment.CourseState.OrdinaryCourse,CourseContainerEntryFragment.CourseState.OtherCourse->{
                    mCoursesViewModel.courses.observe(this, Observer{
                        ScheduleViewBidingAdapter.setScheduleData(schedule_view,it,mWeek, mCoursesViewModel.isGetOthers.value!!)
                    })
                }
                CourseContainerEntryFragment.CourseState.NoClassInvitationCourse->{
                    mNoCourseInviteViewModel?.studentsCourseMap?.observe(this, Observer {
                        ScheduleViewBidingAdapter.setNoCourseInvite(schedule_view,mWeek,mNoCourseInviteViewModel?.studentsCourseMap?.value,mNoCourseInviteViewModel?.nameList!!)
                        schedule_view.mIsDisplayCourse = false
                    })
                }
                else -> {}
            }
        }


        mBinding.coursesViewModel = mCoursesViewModel
        mBinding.coursePageViewModel = mCoursePageViewModel

        mCoursesViewModel.toastEvent.observe(this, Observer {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        // 当当前周数进行了改变后有可能SchoolCalendar进行了更新，这时候就对DateViewModel中的日期进行更新
        mCoursesViewModel.schoolCalendarUpdated.observe(this, Observer {
            if (it == true) {
                mCoursePageViewModel.getDate()
            }
        })

        schedule_view.adapterChangeListener = {
            val position = schedule_view.adapter?.getHighLightPosition()
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
        mBinding.root.schedule_view.clearTouchView()
    }
}