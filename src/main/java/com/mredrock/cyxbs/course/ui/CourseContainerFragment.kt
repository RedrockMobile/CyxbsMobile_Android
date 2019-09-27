package com.mredrock.cyxbs.course.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.common.bean.WidgetCourse
import com.mredrock.cyxbs.common.event.ShowModeChangeEvent
import com.mredrock.cyxbs.common.event.WidgetCourseEvent
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.ScheduleVPAdapter
import com.mredrock.cyxbs.course.databinding.CourseFragmentCourseContainerBinding
import com.mredrock.cyxbs.course.event.*
import com.mredrock.cyxbs.course.lifecycle.VPOnPagerChangeObserver
import com.mredrock.cyxbs.course.utils.AffairToCalendar
import com.mredrock.cyxbs.course.utils.changeLibBeanToCourse
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
        const val OTHERS_STU_NUM = "others_stu_num"

        fun getOthersCourseContainerFragment(stuNum: String): CourseContainerFragment =
                CourseContainerFragment().apply {
                    arguments = Bundle().apply { putString(OTHERS_STU_NUM, stuNum) }
                }
    }

    override val openStatistics: Boolean
        get() = false

    private var mOthersStuNum: String? = null
    private lateinit var mScheduleAdapter: ScheduleVPAdapter
    private var mCoursesViewModel: CoursesViewModel? = null
    private lateinit var mBinding: CourseFragmentCourseContainerBinding
    private lateinit var mRawWeeks: Array<String>
    private lateinit var mWeeks: Array<String>

    private val mDialogHelper: ScheduleDetailDialogHelper by lazy(LazyThreadSafetyMode.NONE) {
        ScheduleDetailDialogHelper(context!!)
    }

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

        if(!isAdded) return//如果没有被添加进Activity，Fragment会抛出not attach a context的错误

        arguments?.let {
            mOthersStuNum = it.getString(OTHERS_STU_NUM)
        }

        setHasOptionsMenu(true)
        resources.getStringArray(R.array.course_course_weeks_strings).let {
            mRawWeeks = it
            mWeeks = mRawWeeks.copyOf()
            mScheduleAdapter = ScheduleVPAdapter(mWeeks, childFragmentManager)
        }
        mBinding.vp.adapter = mScheduleAdapter
        mBinding.tabLayout.setupWithViewPager(mBinding.vp)

        // 给ViewPager添加OnPageChangeListener
        lifecycle.addObserver(VPOnPagerChangeObserver(mBinding.vp,
                mOnPageSelected = {
                    // 当ViewPager发生了滑动，清理课表上加备忘的View
                    EventBus.getDefault().post(DismissAddAffairViewEvent())
                    // 当ViewPager发生了滑动，通过EventBus对Toolbar上的周数进行改变
                    EventBus.getDefault().post(WeekNumEvent(mScheduleAdapter.getPageTitle(it).toString(), mOthersStuNum != null))
                }))

        // 获取依赖于CourseContainerFragment的Activity的CoursesViewModel。在WeekFragment的切换的时候，不会
        // 重复获取数据。
        mCoursesViewModel = ViewModelProviders.of(activity!!).get(CoursesViewModel::class.java)

        mCoursesViewModel?.let {model->
            model.getSchedulesDataFromDataBase(activity!!, mOthersStuNum)
            model.nowWeek.observe(this, Observer { nowWeek ->
                if (nowWeek != null && nowWeek != 0) {
                    // 过时的本周的位置以及将其替换为原始周数显示
                    val oldNowWeek = mWeeks.indexOf(resources.getString(R.string.course_now_week))
                    if (oldNowWeek != -1) {
                        mWeeks[oldNowWeek] = mRawWeeks[oldNowWeek]
                    }
                    // 设置现在的本周显示
                    mWeeks[nowWeek] = resources.getString(R.string.course_now_week)
                    mScheduleAdapter.notifyDataSetChanged()
                }
                // 跳转到当前周
                mBinding.vp.currentItem = nowWeek ?: 0

            })
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        mCoursesViewModel?.let {
            if (it.isGetOthers.value == false) {//如果是他人课表，不加载添加事物的btn
                activity?.menuInflater?.inflate(R.menu.course_course_menu, menu)
            }
        }
        super.onPrepareOptionsMenu(menu)
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
     * 这个方法用于小部件点击打开课表详情页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun showDialogFromWidget(event: WidgetCourseEvent<WidgetCourse.DataBean>) {
        val mCourse = changeLibBeanToCourse(event.mutableList[0])
        mDialogHelper.showDialog(mutableListOf(mCourse))
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
        EventBus.getDefault().post(RefreshEvent(true))
        mCoursesViewModel?.refreshScheduleData(this.context!!)
    }

    /**
     * 这个方法用于添加事务后重新进行网络请求获取数据
     *
     * @param addAffairEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addAffairs(addAffairEvent: AddAffairEvent) {
        EventBus.getDefault().post(RefreshEvent(true))
        mCoursesViewModel?.refreshScheduleData(this.context!!)
    }

    /**
     * 这个方法用于接收设置课表上备忘项目显示模式的更新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showModeChange(e: ShowModeChangeEvent) {
        EventBus.getDefault().post(RefreshEvent(true))
        mCoursesViewModel?.refreshScheduleData(this.context!!)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun modifyAffairs(modifyAffairEvent: ModifyAffairEvent) {
        EventBus.getDefault().post(RefreshEvent(true))
        mCoursesViewModel?.refreshScheduleData(this.context!!)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addTheAffairsToTheCalendar(affairFromInternetEvent: AffairFromInternetEvent) {
        activity?.let { activity ->
            if (affairFromInternetEvent.affairs.isNotEmpty()) {
                val affairToCalendar = AffairToCalendar(activity as AppCompatActivity, affairFromInternetEvent.affairs)
                if (affairFromInternetEvent.affairs.find { it.affairTime.isNullOrBlank() } != null) {
                    affairToCalendar.getPermissionToInsert()
                }
            }
        }
    }
}