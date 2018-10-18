package com.mredrock.cyxbs.course.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.*
import com.mredrock.cyxbs.course.event.TabIsFoldEvent
import com.mredrock.cyxbs.course.event.WeekNumEvent
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.ScheduleVPAdapter
import com.mredrock.cyxbs.course.databinding.CourseFragmentCourseContainerBinding
import com.mredrock.cyxbs.course.event.*
import com.mredrock.cyxbs.course.lifecycle.VPOnPagerChangeObserver
import com.mredrock.cyxbs.course.utils.AffairToCalendar
import com.mredrock.cyxbs.course.viewmodels.CoursesViewModel
import kotlinx.android.synthetic.main.course_fragment_course_container.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by anriku on 2018/8/16.
 */

class CourseContainerFragment : BaseFragment() {

    companion object {
        private const val TAG = "CourseContainerFragment"
    }

    private lateinit var mScheduleAdapter: ScheduleVPAdapter
    private lateinit var mCoursesViewModel: CoursesViewModel
    private lateinit var mBinding: CourseFragmentCourseContainerBinding
    private lateinit var mRawWeeks: Array<String>
    private lateinit var mWeeks: Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.course_fragment_course_container, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFragment()
    }


    private fun initFragment() {
        activity ?: return

        setHasOptionsMenu(true)
        resources.getStringArray(R.array.course_course_weeks_strings).let {
            mRawWeeks = it
            mWeeks = mRawWeeks.copyOf()
            mScheduleAdapter = ScheduleVPAdapter(mWeeks, fragmentManager)
        }
        mBinding.vp.adapter = mScheduleAdapter
        mBinding.tabLayout.setupWithViewPager(mBinding.vp)

        // 给ViewPager添加OnPageChangeListener
        lifecycle.addObserver(VPOnPagerChangeObserver(mBinding.vp,
                mOnPageSelected = {
                    LogUtils.d(TAG, mScheduleAdapter.getPageTitle(it).toString())
                    // 当ViewPager发生了滑动，通过EventBus对Toolbar上的周数进行改变
                    EventBus.getDefault().post(WeekNumEvent(mScheduleAdapter.getPageTitle(it).toString()))
                }))

        // 获取依赖于CourseContainerFragment的Activity的CoursesViewModel。在WeekFragment的切换的时候，不会
        // 重复获取数据。
        mCoursesViewModel = ViewModelProviders.of(activity!!).get(CoursesViewModel::class.java)
        mCoursesViewModel.getSchedulesData(activity!!)

        mCoursesViewModel.nowWeek.observe(activity!!, Observer { nowWeek ->
            if (nowWeek != null && nowWeek != 0) {
                // 过时的本周的位置以及将其替换为原始周数显示
                val oldNowWeek = mWeeks.indexOf("本周")
                if (oldNowWeek != -1) {
                    mWeeks[oldNowWeek] = mRawWeeks[oldNowWeek]
                }
                // 设置现在的本周显示
                mWeeks[nowWeek] = "本周"
                mScheduleAdapter.notifyDataSetChanged()
            }
            // 跳转到当前周
            vp.currentItem = nowWeek ?: 0

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity?.menuInflater?.inflate(R.menu.course_course_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add_affair -> {
                startActivity(Intent(activity, EditAffairActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * 这个方法用于进行TabLayout的显示和折叠
     *
     * @param tabIsFoldEvent 表示TabLayout是否折叠
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun isWeekTabsFold(tabIsFoldEvent: TabIsFoldEvent) {
        if (tabIsFoldEvent.isFold) {
            tab_layout.visibility = View.GONE
        } else {
            tab_layout.visibility = View.VISIBLE
        }
    }


    /**
     * 这个方法用于删除事务之后重新获取进行网络请求获取数据
     *
     * @param deleteAffairEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun deleteAffair(deleteAffairEvent: DeleteAffairEvent) {
        mCoursesViewModel.refreshScheduleData(false)
    }

    /**
     * 这个方法用于添加事务后重新进行网络请求获取数据
     *
     * @param addAffairEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addAffairs(addAffairEvent: AddAffairEvent) {
        mCoursesViewModel.refreshScheduleData(false)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun modifyAffairs(modifyAffairEvent: ModifyAffairEvent) {
        mCoursesViewModel.refreshScheduleData(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addTheAffairsToTheCalendar(affairFromInternetEvent: AffairFromInternetEvent) {
        activity?.let {
            if (affairFromInternetEvent.affairs.isNotEmpty()) {
                val affairToCalendar = AffairToCalendar(it, affairFromInternetEvent.affairs)
                affairToCalendar.getPermissionToInsert()
            }
        }
    }
}