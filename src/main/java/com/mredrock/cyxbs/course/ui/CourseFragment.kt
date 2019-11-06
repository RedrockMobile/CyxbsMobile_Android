package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.common.event.CourseSlipsTopEvent
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.databinding.CourseFragmentCourseBinding
import com.mredrock.cyxbs.course.event.DismissAddAffairViewEvent
import com.mredrock.cyxbs.course.viewmodels.CoursesViewModel
import com.mredrock.cyxbs.course.viewmodels.DateViewModel
import kotlinx.android.synthetic.main.course_fragment_course.*
import kotlinx.android.synthetic.main.course_fragment_course.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode



/**
 * Created by anriku on 2018/8/14.
 */

class CourseFragment : BaseFragment(){

    companion object {
        const val WEEK_NUM = "week_num"
    }

    override val openStatistics: Boolean
        get() = false

    private var mWeek: Int = 0
    private lateinit var mCoursesViewModel: CoursesViewModel
    private lateinit var mDateViewModel: DateViewModel
    private lateinit var mBinding: CourseFragmentCourseBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.course_fragment_course,
                container, false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFragment()
    }

    private fun initFragment() {
        mBinding.lifecycleOwner = this
        mWeek = arguments?.getInt(WEEK_NUM) ?: 0

        activity ?: return
        mCoursesViewModel = ViewModelProviders.of(activity!!).get(CoursesViewModel::class.java)
        mDateViewModel = ViewModelProviders.of(this,
                DateViewModel.DateViewModelFactory(mWeek)).get(DateViewModel::class.java)
        mDateViewModel.nowWeek = mWeek

        mBinding.coursesViewModel = mCoursesViewModel
        mBinding.dateViewModel = mDateViewModel

        // 当当前周数进行了改变后有可能SchoolCalendar进行了更新，这时候就对DateViewModel中的日期进行更新
        mCoursesViewModel.schoolCalendarUpdated.observe(this, Observer {
            if (it == true) {
                mDateViewModel.getDate()
            }
        })

        //防止课表还没滑倒顶部就能够滑动bottomSheet
        course_sv.setScrollViewListener(object : CourseScrollView.ScrollViewListener{
            override fun onScrollChanged(scrollView: CourseScrollView, x: Int, y: Int, oldx: Int, oldy: Int) {
                if (y == 0) {
                    EventBus.getDefault().post(CourseSlipsTopEvent(true))
                } else {
                    EventBus.getDefault().post(CourseSlipsTopEvent(false))
                }
            }
        })
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            Log.d("top", "${course_sv?.scrollY}")
//            CourseScrollView.isCurrentCourseTop = course_sv?.isTop ?: true
            CourseScrollView.isCurrentCourseTop = if (course_sv==null) true else course_sv.scrollY == 0
            if (CourseScrollView.isCurrentCourseTop) {
                EventBus.getDefault().post(CourseSlipsTopEvent(true))
            }else{
                EventBus.getDefault().post(CourseSlipsTopEvent(false))
            }
        }
    }

    /**
     * 这个方法用于清理课表上加备忘的View
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun dissmissAddAffairEventView(e: DismissAddAffairViewEvent) {
        mBinding.root.schedule_view.clearTouchView()
    }
}