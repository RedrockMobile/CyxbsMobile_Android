package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.WidgetCourse
import com.mredrock.cyxbs.common.config.COURSE_ENTRY
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.event.NotifyBottomSheetToExpandEvent
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
import kotlinx.android.synthetic.main.course_title_present_course.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by anriku on 2018/8/16.
 */
@Route(path = COURSE_ENTRY)
class CourseContainerEntryFragment : BaseFragment() {

    companion object {
        const val OTHERS_STU_NUM = "others_stu_num"

        fun getOthersCourseContainerFragment(stuNum: String): CourseContainerEntryFragment =
                CourseContainerEntryFragment().apply {
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
    /**
     * 在用于的登录状态发生改变后进行调用。
     *
     * @param event 包含当前用户登录状态的事件。
     */
    override fun onLoginStateChangeEvent(event: LoginStateChangeEvent) {
        super.onLoginStateChangeEvent(event)
        if (event.newState) {
            // replaceFragment(CourseContainerFragment())
            mBinding.vp.adapter?.notifyDataSetChanged()
            mToolbarTitle = activity!!.getString(R.string.course_all_week)
        } else {
            mBinding.vp.adapter?.notifyDataSetChanged()
            // replaceFragment(NoneLoginFragment())
            mToolbarTitle = activity!!.getString(R.string.common_course)
            Thread {
                ViewModelProviders.of(activity!!).get(CoursesViewModel::class.java).clearCache()
            }.start()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
        initCourseEntryFragment()
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

        var lastWeek:String = "本周"
        mCoursesViewModel?.let {model->
            model.getSchedulesDataFromDataBase(activity!!, mOthersStuNum)
            model.nowWeek.observe(this, Observer { nowWeek ->
                if (nowWeek != null && nowWeek != 0) {
                    // 过时的本周的位置以及将其替换为原始周数显示
                    val oldNowWeek = mWeeks.indexOf(lastWeek)
                    if (oldNowWeek != -1) {
                        mWeeks[oldNowWeek] = mRawWeeks[oldNowWeek]
                    }
                    // 设置现在的本周显示
                    lastWeek = "${mWeeks[nowWeek]}(${resources.getString(R.string.course_now_week)})"
                    mWeeks[nowWeek] = lastWeek
                    mScheduleAdapter.notifyDataSetChanged()
                }
                // 跳转到当前周
                mBinding.vp.currentItem = nowWeek ?: 0
            })
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.clear()
        mCoursesViewModel?.let {
            if (it.isGetOthers.value == false) {//如果是他人课表，不加载添加事物的btn
                activity?.menuInflater?.inflate(R.menu.course_course_menu, menu)
            }
        }
        super.onPrepareOptionsMenu(menu)
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

    // 表示是否TabLayout折叠
    private var mIsFold = true

    // 用于记录当前Toolbar要显示的字符串
    private lateinit var mToolbarTitle: String

    // 用于检测用户登陆状态是否改变，用于刷新
    private var curIndex = 0



    private fun initCourseEntryFragment() {
        activity ?: return
        // 如果用户登录了就显示CourseContainerFragment；如果用户没有登录就行显示NoneLoginFragment进行登录
        mToolbarTitle = activity!!.getString(R.string.course_all_week)
        setToolbar()
        course_current_course_container.setOnClickListener {
            EventBus.getDefault().post(NotifyBottomSheetToExpandEvent(true))
        }
    }


    /**
     * 用于对Toolbar进行一些设置。如果用户登录了，就增加折叠图标，并添加相应的点击事件。
     * 如果用户没登录，就取消折叠图标以及取消点击事件。
     */
    private fun setToolbar() {
        // 设置当前Toolbar的内容
        if (curIndex == 0 || userVisibleHint) course_tv_which_week.text = mToolbarTitle

        if (BaseApp.isLogin && curIndex == 0) {
            course_tv_which_week.setOnClickListener {
                mIsFold = !mIsFold
                if (mIsFold) {
                    tab_layout.visibility = View.GONE
                } else {
                    tab_layout.visibility = View.VISIBLE
                }
            }
        } else {
            course_tv_which_week.setOnClickListener(null)
        }
    }


    /**
     * 在[CourseContainerEntryFragment]中的ViewPager的Page发生了变化后进行接收对应的事件来对[mToolbar]的标题进行
     * 修改
     * @param weekNumEvent 包含当前周字符串的事件。
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getWeekNumFromFragment(weekNumEvent: WeekNumEvent) {
        if (weekNumEvent.isOthers) {
            return
        }
        mToolbarTitle = weekNumEvent.weekString
        setToolbar()
    }
}