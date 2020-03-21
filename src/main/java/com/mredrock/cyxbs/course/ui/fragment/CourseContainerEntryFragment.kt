package com.mredrock.cyxbs.course.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.bean.WidgetCourse
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.event.*
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.ScheduleVPAdapter
import com.mredrock.cyxbs.course.databinding.CourseFragmentCourseContainerBinding
import com.mredrock.cyxbs.course.event.*
import com.mredrock.cyxbs.course.lifecycle.VPOnPagerChangeObserver
import com.mredrock.cyxbs.course.ui.ScheduleDetailDialogHelper
import com.mredrock.cyxbs.course.utils.AffairToCalendar
import com.mredrock.cyxbs.course.utils.changeLibBeanToCourse
import com.mredrock.cyxbs.course.viewmodels.CoursesViewModel
import com.mredrock.cyxbs.course.viewmodels.NoCourseInviteViewModel
import kotlinx.android.synthetic.main.course_fragment_course_container.*
import kotlinx.android.synthetic.main.course_fragment_course_container.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * Created by anriku on 2018/8/16.
 */
@Route(path = COURSE_ENTRY)
class CourseContainerEntryFragment : BaseFragment() {
    override val openStatistics: Boolean
        get() = false

    //当前课表Fragment的显示的状态
    var courseState = CourseState.OrdinaryCourse

    //如果是在获取老师课表下面两值不为空
    private var mOthersTeaNum: String? = null
        set(value) {
            if (value != null) {
                field = value
                courseState = CourseState.TeacherCourse
            }
        }
    private var mOthersTeaName: String? = null
        set(value) {
            if (value != null) {
                field = value
                courseState = CourseState.TeacherCourse
            }
        }

    // 当前课表是哪个账号的，如果为空则表示在获取他人课表
    private var mStuNum: String? = null
        set(value) {
            field = value
            if (value != null) {
                courseState = CourseState.OtherCourse
            }
        }

    //如果是没课约，这两个值不为空
    private var mStuNumList: ArrayList<String>? = null
        set(value) {
            field = value
            if (value != null) {
                courseState = CourseState.NoClassInvitationCourse
            }
        }
    private var mNameList: ArrayList<String>? = null
        set(value) {
            field = value
            if (value != null) {
                courseState = CourseState.NoClassInvitationCourse
            }
        }

    //是否直接加载课表子页，默认直接加载，除非传值过来设置为false过来，后面可以懒加载
    private lateinit var directLoadCourse: String

    //每一周课表的ViewPager的Adapter
    private lateinit var mScheduleAdapter: ScheduleVPAdapter

    //普通课程和查课表需要的ViewModel
    lateinit var mCoursesViewModel: CoursesViewModel

    //用于没课约的ViewModel
    var mNoCourseInviteViewModel: NoCourseInviteViewModel? = null

    //当前Fragment的根布局的Binding
    private lateinit var mBinding: CourseFragmentCourseContainerBinding

    //每一周的Viewpager所对应的tab的显示的字符串
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
     * 登陆状态发生改变时会被回调的函数
     * @param event 包含当前用户登录状态的事件。
     */
    override fun onLoginStateChangeEvent(event: LoginStateChangeEvent) {
        super.onLoginStateChangeEvent(event)
        if (event.loginState) {
            // replaceFragment(CourseContainerFragment())
            mBinding.vp.adapter?.notifyDataSetChanged()
            mCoursesViewModel.mWeekTitle.set(activity!!.getString(R.string.course_all_week))
        } else {
            mBinding.vp.adapter?.notifyDataSetChanged()
            // replaceFragment(NoneLoginFragment())
            mCoursesViewModel.mWeekTitle.set(activity!!.getString(R.string.common_course))
            Thread {
                ViewModelProviders.of(activity!!).get(CoursesViewModel::class.java).clearCache()
            }.start()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }


    /**
     * 对当前Fragment进行一系列初始化
     */
    private fun initFragment() {
        activity ?: return
        //如果没有被添加进Activity，Fragment会抛出not attach a context的错误
        if (!isAdded) return

        //获取从其他模块传来的数据
        arguments?.let { bundle ->
            mStuNum = bundle.getString(OTHERS_STU_NUM)
            mStuNumList = bundle.getStringArrayList(STU_NUM_LIST)
            mNameList = bundle.getStringArrayList(STU_NAME_LIST)
            mOthersTeaName = bundle.getString(OTHERS_TEA_NAME)
            mOthersTeaNum = bundle.getString(OTHERS_TEA_NUM)
            directLoadCourse = bundle.getString(COURSE_DIRECT_LOAD) ?: TRUE
        }


        /**
         * 在接收从其他模块传来数据时，当前Fragment的状态发生了改变，根据当前EntryFragment的状态
         * 来获取相应的ViewModel,以此可以加载不同的页面，复用到不同的地方
         */
        when (courseState) {
            //如果是普通课表
            CourseState.OrdinaryCourse -> {
                //这里获取的ViewModel不同于其他几个，因为这这些数据需要跟随MainActivity的销毁才销毁
                mCoursesViewModel = ViewModelProviders.of(activity!!).get(CoursesViewModel::class.java)
                mBinding.coursesViewModel = mCoursesViewModel
                context?.let { mCoursesViewModel.getSchedulesDataFromLocalThenNetwork() }
            }
            //如果是没课约
            CourseState.NoClassInvitationCourse -> {
                //没课约获取的ViewModel声明周期是跟随Fragment的，即时销毁
                mCoursesViewModel = ViewModelProviders.of(this).get(CoursesViewModel::class.java)
                mBinding.coursesViewModel = mCoursesViewModel
                mNoCourseInviteViewModel = ViewModelProviders.of(this,
                                NoCourseInviteViewModel.Factory(mStuNumList!!, mNameList!!))
                        .get(NoCourseInviteViewModel::class.java)
                mNoCourseInviteViewModel?.getCourses()
            }
            //如果是查询其他同学的课表
            CourseState.OtherCourse -> {
                mCoursesViewModel = ViewModelProviders.of(this).get(CoursesViewModel::class.java)
                mBinding.coursesViewModel = mCoursesViewModel
                context?.let { mCoursesViewModel.getSchedulesDataFromLocalThenNetwork(mStuNum) }
            }
            //如果是老师课表
            CourseState.TeacherCourse -> {
                mCoursesViewModel = ViewModelProviders.of(this).get(CoursesViewModel::class.java)
                mBinding.coursesViewModel = mCoursesViewModel
                mCoursesViewModel.isTeaCourse = true
                mOthersTeaName?.let {
                    mCoursesViewModel.mUserName = it
                }
                context?.let { mCoursesViewModel.getSchedulesDataFromLocalThenNetwork(mOthersTeaNum) }
            }
        }

        //初始化周数tab的文字和下面课表的Adapter
        resources.getStringArray(R.array.course_course_weeks_strings).let {
            mRawWeeks = it
            mWeeks = mRawWeeks.copyOf()
            mScheduleAdapter = ScheduleVPAdapter(mWeeks, childFragmentManager)
        }

        //如果需要直接加载课表则直接加载课表，否则为了优化性能可以当需要的时候加载
        if (directLoadCourse == TRUE) {
            //给下方ViewPager添加适配器和绑定tab
            mBinding.vp.adapter = mScheduleAdapter
            mBinding.tabLayout.setupWithViewPager(mBinding.vp)
            course_lottie_load.visibility = View.GONE
            settingFollowBottomSheet(1f)
        }


        //获取到ViewModel后进行一些初始化操作
        course_tv_now_course.setOnClickListener {
            mCoursesViewModel.nowCourse.get()?.let { course ->
                mDialogHelper.showDialog(MutableList(1) { course })
            }
        }
        mCoursesViewModel.nowWeek.observe(this, Observer { mBinding.vp.currentItem = it ?: 0 })
        mCoursesViewModel.toastEvent.observe(activity!!, Observer { str -> str?.let { CyxbsToast.makeText(activity!!, it, Toast.LENGTH_SHORT).show() } })
        mCoursesViewModel.longToastEvent.observe(activity!!, Observer { str -> str?.let { CyxbsToast.makeText(activity!!, it, Toast.LENGTH_LONG).show() } })
        mCoursesViewModel.isShowBackPresentWeek.observe(activity!!, Observer {
            /**
             * 这里为什么要判空呢，因为 mCoursesViewModel.isShowBackPresentWeek
             * 在切换黑夜模式的时候这个变量的回调会启动，但是这时候[course_current_course_week_select_container]
             * 还没有生成，进一步说在应用内开启黑夜模式因为会重启activity但是[mCoursesViewModel]没有
             */
            course_current_course_week_select_container?.apply {
                TransitionManager.beginDelayedTransition(this, Slide().apply { slideEdge = Gravity.END })
                this.course_back_present_week?.visibility = it
            }
        })
        // 给ViewPager添加OnPageChangeListener
        lifecycle.addObserver(VPOnPagerChangeObserver(mBinding.vp,
                mOnPageSelected = {
                    // 当ViewPager发生了滑动，清理课表上加备忘的View
                    EventBus.getDefault().post(DismissAddAffairViewEvent())
                    // 当ViewPager发生了滑动，对Head上的周数进行改变
                    mCoursesViewModel.isShowPresentTips.set(
                            when (it) {
                                0 -> View.GONE
                                mCoursesViewModel.nowWeek.value -> View.VISIBLE
                                else -> View.GONE
                            }
                    )
                    mCoursesViewModel.isShowBackPresentWeek.value = if (mCoursesViewModel.nowWeek.value == it) View.GONE else View.VISIBLE
                    mCoursesViewModel.mWeekTitle.set(mScheduleAdapter.getPageTitle(it).toString())
                }))
        //对头部课表头部信息进行一系列初始化
        initHead()
    }

    /**
     * 初始化课表头部信息
     */
    private fun initHead() {
        //便于下面复用代码
        val isShow: () -> Unit = {
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
            TransitionManager.beginDelayedTransition(fl, Slide().apply { slideEdge = Gravity.START })
            course_header_select_content.visibility = View.GONE
            course_header_show.visibility = View.VISIBLE
        }
        mCoursesViewModel.mWeekTitle.set(activity?.getString(R.string.course_all_week))

        //给整个头部设置点击事件，点击展开整个BottomSheet
        course_header.setOnClickListener {
            EventBus.getDefault().post(NotifyBottomSheetToExpandEvent(true))
        }

        //回到本周按钮的点击事件
        course_back_present_week.setOnClickListener {
            mCoursesViewModel.nowWeek.value?.let {
                mBinding.vp.currentItem = it
            }
        }
    }

    /**
     * 根据主页的fragment的滑动状态来设置当前头部显示效果
     * @param state 从0到1，1表示BottomSheet完全展开
     */
    private fun settingFollowBottomSheet(state: Float) {
        //todo 给tip小块加上动画的话和BottomSheet一起会有性能问题
//        mCoursesViewModel.setTipsState(state, course_tip)
        if (course_header_select_content.visibility == View.GONE) {
            course_current_course_container.visibility = View.VISIBLE
            course_current_course_container.alpha = 1 - state
            actionWhenLoaded {
                course_current_course_week_select_container.alpha = state
            }
            if (state == 0f) {
                course_header_select_content.visibility = View.GONE
                course_header_show.visibility = View.VISIBLE
                course_current_course_week_select_container.visibility = View.GONE
            } else if (1 - state == 0f) {
                course_current_course_container.visibility = View.GONE
            }
        } else {
            if (state == 0f) {
                TransitionManager.beginDelayedTransition(fl, Slide().apply {
                    slideEdge = Gravity.START
                    duration = 200
                })
                course_header_select_content.visibility = View.GONE
                course_header_show.visibility = View.VISIBLE
                course_current_course_container.visibility = View.VISIBLE
                course_current_course_container.alpha = 1 - state
                course_current_course_week_select_container.alpha = state
                course_current_course_week_select_container.visibility = View.GONE
            }
        }
    }


    private fun actionWhenLoaded(action: () -> Unit) {
        if (vp.adapter == null) {
            course_current_course_week_select_container.visibility = View.GONE
        } else {
            course_current_course_week_select_container.visibility = View.VISIBLE
            action()
        }
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
        mCoursesViewModel.refreshAffairFromInternet()
    }

    /**
     * 这个方法用于添加事务后重新进行网络请求获取数据
     *
     * @param addAffairEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addAffairs(addAffairEvent: AddAffairEvent) {
        EventBus.getDefault().post(RefreshEvent(true))
        mCoursesViewModel.refreshAffairFromInternet()
    }

    /**
     * 这个方法用于接收设置课表上备忘项目显示模式的更新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showModeChange(e: ShowModeChangeEvent) {
        mCoursesViewModel.refreshAffairFromInternet()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun modifyAffairs(modifyAffairEvent: ModifyAffairEvent) {
        mCoursesViewModel.refreshAffairFromInternet()
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

    /**
     * 如果没有直接加载课表ViewPager子页，这个可以用来通知加载
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loadCoursePage(loadCourse: LoadCourse) {
        val load = {
            //给下方ViewPager添加适配器和绑定tab
            vp.adapter = mScheduleAdapter
            tab_layout.setupWithViewPager(mBinding.vp)
            mCoursesViewModel.nowWeek.value?.let { nowWeek ->
                vp.currentItem = nowWeek
            }
        }
        if (loadCourse.isUserSee) {
            course_lottie_load.visibility = View.VISIBLE
            course_lottie_load.speed = 2f
            course_lottie_load.addAnimatorUpdateListener {
                if (it.animatedFraction > 0.78) {
                    course_lottie_load.pauseAnimation()
                    load()
                    course_lottie_load.visibility = View.GONE
                    course_current_course_week_select_container.visibility = View.VISIBLE
                }
            }
            course_lottie_load.playAnimation()
        } else {
            load()
        }
    }


    /**
     * 接收main模块里BottomSheet滑动对应的头部变化
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun bottomSheetSlideStateReceive(bottomSheetStateEvent: BottomSheetStateEvent) {
        settingFollowBottomSheet(bottomSheetStateEvent.state)
    }

    enum class CourseState {
        OrdinaryCourse, TeacherCourse, OtherCourse, NoClassInvitationCourse
    }
}