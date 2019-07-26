package com.mredrock.cyxbs.course.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.databinding.CourseFragmentCourseBinding
import com.mredrock.cyxbs.course.event.DismissAddAffairViewEvent
import com.mredrock.cyxbs.course.event.RefreshEvent
import com.mredrock.cyxbs.course.viewmodels.CoursesViewModel
import com.mredrock.cyxbs.course.viewmodels.DateViewModel
import kotlinx.android.synthetic.main.course_fragment_course.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by anriku on 2018/8/14.
 */

class CourseFragment : BaseFragment() {

    companion object {
        private const val TAG = "CourseFragment"
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
        mBinding.setLifecycleOwner(this)
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

        //下拉刷新
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            mCoursesViewModel.refreshScheduleData(this.context!!)
        }
    }

    /**
     * 这个方法用于清理课表上加备忘的View
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun dissmissAddAffairEventView(e: DismissAddAffairViewEvent) {
        mBinding.root.schedule_view.clearTouchView()
    }

    /**
     * 这个方法用于在进行网络请求课程数据后，关闭旋转的刷新提示
     *
     * @param refreshEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun finishRefresh(refreshEvent: RefreshEvent) {
        mBinding.swipeRefreshLayout.isRefreshing = refreshEvent.isRefresh
    }
}