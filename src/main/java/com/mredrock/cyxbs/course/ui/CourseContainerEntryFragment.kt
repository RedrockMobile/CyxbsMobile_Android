package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.bean.WidgetCourse
import com.mredrock.cyxbs.common.config.COURSE_ENTRY
import com.mredrock.cyxbs.common.event.*
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
            mWeekTitle.set(activity!!.getString(R.string.course_all_week))
        } else {
            mBinding.vp.adapter?.notifyDataSetChanged()
            // replaceFragment(NoneLoginFragment())
            mWeekTitle.set(activity!!.getString(R.string.common_course))
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

        if (!isAdded) return//如果没有被添加进Activity，Fragment会抛出not attach a context的错误

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
                    // 当ViewPager发生了滑动，对Head上的周数进行改变
                    if (mOthersStuNum != null) {
                        return@VPOnPagerChangeObserver
                    }
                    mCoursesViewModel?.isShowPresentTips?.set(
                            when {
                                it == 0 -> {
                                    View.GONE
                                }
                                mCoursesViewModel?.nowWeek?.value == it -> {
                                    View.VISIBLE
                                }
                                else -> {
                                    View.GONE
                                }
                            }
                    )
                    mCoursesViewModel?.isShowBackPresentWeek?.set(
                            if (mCoursesViewModel?.nowWeek?.value == it) {
                                View.GONE
                            } else {
                                View.VISIBLE
                            }
                    )
                    mWeekTitle.set(mScheduleAdapter.getPageTitle(it).toString())
                }))

        course_back_present_week.setOnClickListener {
            mCoursesViewModel?.nowWeek?.value?.let {
                mBinding.vp.currentItem = it
            }
        }

        // 获取依赖于CourseContainerFragment的Activity的CoursesViewModel。在WeekFragment的切换的时候，不会
        // 重复获取数据。
        mCoursesViewModel = ViewModelProviders.of(activity!!).get(CoursesViewModel::class.java)
        mBinding.coursesViewModel = mCoursesViewModel
        mBinding.fragment = this

        mCoursesViewModel?.let { model ->
            model.getSchedulesDataFromDataBase(activity!!, mOthersStuNum)
            model.nowWeek.observe(this, Observer { nowWeek ->
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
    var mWeekTitle: ObservableField<String> = ObservableField("")

    private fun initCourseEntryFragment() {
        activity?.let { activity ->
            // 如果用户登录了就显示CourseContainerFragment；如果用户没有登录就行显示NoneLoginFragment进行登录
            mWeekTitle.set(activity.getString(R.string.course_all_week))
            initHead()
            course_current_course_container.setOnClickListener {
                EventBus.getDefault().post(NotifyBottomSheetToExpandEvent(true))
            }
        }
    }


    /**
     * 初始化课表头部信息
     */
    private fun initHead() {
        //便于下面复用代码
        val isShow:()->Unit = {
            TransitionManager.beginDelayedTransition(fl, TransitionSet().apply {
                addTransition(Slide().apply {
                    slideEdge = Gravity.START
                    duration = 250
                })
            })
            course_header_select_content.visibility = View.VISIBLE
            course_header_show.visibility = View.GONE
        }
        course_tv_which_week.setOnClickListener { isShow() }
        course_this_week_tips.setOnClickListener { isShow() }
        course_header_back.setOnClickListener {
            TransitionManager.beginDelayedTransition(fl, TransitionSet().apply {
                addTransition(Slide().apply {
                    slideEdge = Gravity.START
                })
            })
            course_header_select_content.visibility = View.GONE
            course_header_show.visibility = View.VISIBLE
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun bottomSheetSlideStateReceive(bottomSheetStateEvent: BottomSheetStateEvent) {
        course_current_course_week_select_container.visibility = View.VISIBLE
        if (course_header_select_content.visibility == View.GONE) {
            course_current_course_container.alpha = 1 - bottomSheetStateEvent.state
            course_current_course_week_select_container.alpha = bottomSheetStateEvent.state
            if (bottomSheetStateEvent.state == 0f) {
                course_header_select_content.visibility = View.GONE
                course_header_show.visibility = View.VISIBLE
                course_current_course_week_select_container.visibility = View.GONE
            }
        } else {
            if (bottomSheetStateEvent.state == 0f) {
                TransitionManager.beginDelayedTransition(fl, TransitionSet().apply {
                    addTransition(Slide().apply {
                        slideEdge = Gravity.START
                        duration = 200
                    })
                })
                course_header_select_content.visibility = View.GONE
                course_header_show.visibility = View.VISIBLE
                course_current_course_container.alpha = 1 - bottomSheetStateEvent.state
                course_current_course_week_select_container.alpha = bottomSheetStateEvent.state
                course_current_course_week_select_container.visibility = View.GONE
            }
        }
    }
}