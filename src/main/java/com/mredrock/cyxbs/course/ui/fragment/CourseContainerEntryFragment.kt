package com.mredrock.cyxbs.course.ui.fragment

import android.os.Bundle
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.bean.WidgetCourse
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.event.*
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
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
import kotlinx.android.synthetic.main.course_load_lottie_anim.view.*
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

    private var courseState = CourseState.OrdinaryCourse


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
    private lateinit var mCoursesViewModel: CoursesViewModel

    //用于没课约的ViewModel
    private var mNoCourseInviteViewModel: NoCourseInviteViewModel? = null

    //当前Fragment的根布局的Binding
    private lateinit var mBinding: CourseFragmentCourseContainerBinding

    //每一周的Viewpager所对应的tab的显示的字符串
    private lateinit var mRawWeeks: Array<String>
    private lateinit var mWeeks: Array<String>

    private val mDialogHelper: ScheduleDetailDialogHelper by lazy(LazyThreadSafetyMode.NONE) {
        ScheduleDetailDialogHelper(context!!)
    }

    private val accountService: IAccountService = ServiceManager.getService(IAccountService::class.java)

    //懒加载课表具体内容，加快启动速度
    private val inflateView: View by lazy(LazyThreadSafetyMode.NONE) {
        course_load_lottie_anim.inflate().apply {
            inflateListener?.invoke(this)
        }
    }

    //因为课表具体内容是懒加载
    //所以如果要对ViewStub加载的view或者里面的一些控件进行一些初始化的话
    //请把代码放在这里面，请不要在这个监听里面使用[inflateView]来取得具体view
    //我已经在形参里传给你了,如果你通过[inflateView]来取，极有可能造成死循环
    private var inflateListener: ((View) -> Unit)? = null

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
            initFragment()
        } else {
            Thread {
                ViewModelProvider(this).get(CoursesViewModel::class.java).clearCache()
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
        if (!accountService.getVerifyService().isLogin()) {
            return
        }
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
         * 在接收从其他模块传来数据时，根据当前EntryFragment的状态,
         * 以此可以加载不同的页面，复用到不同的地方
         */

        when (courseState) {
            //如果是普通课表
            CourseState.OrdinaryCourse -> {
                mCoursesViewModel = ViewModelProvider(this)[CoursesViewModel::class.java]
                mBinding.coursesViewModel = mCoursesViewModel
                context?.let { mCoursesViewModel.getSchedulesDataFromLocalThenNetwork(accountService.getUserService().getStuNum()) }
            }
            //如果是没课约
            CourseState.NoClassInvitationCourse -> {
                mCoursesViewModel = ViewModelProvider(this)[CoursesViewModel::class.java]
                mBinding.coursesViewModel = mCoursesViewModel
                mNoCourseInviteViewModel =
                        ViewModelProvider(this, NoCourseInviteViewModel.Factory(mStuNumList!!, mNameList!!))
                                .get(NoCourseInviteViewModel::class.java)
                mNoCourseInviteViewModel?.getCourses()
            }
            //如果是查询其他同学的课表
            CourseState.OtherCourse -> {
                mCoursesViewModel = ViewModelProvider(this)[CoursesViewModel::class.java]
                mBinding.coursesViewModel = mCoursesViewModel
                context?.let { mCoursesViewModel.getSchedulesDataFromLocalThenNetwork(mStuNum!!, true) }
            }
            //如果是老师课表
            CourseState.TeacherCourse -> {
                mCoursesViewModel = ViewModelProvider(this)[CoursesViewModel::class.java]
                mBinding.coursesViewModel = mCoursesViewModel
                mCoursesViewModel.isTeaCourse = true
                mOthersTeaName?.let {
                    mCoursesViewModel.mUserName = it
                }
                context?.let { mCoursesViewModel.getSchedulesDataFromLocalThenNetwork(mOthersTeaNum!!, true) }
            }
        }

        //初始化周数tab的文字和下面课表的Adapter
        resources.getStringArray(R.array.course_course_weeks_strings).let {
            mRawWeeks = it
            mWeeks = mRawWeeks.copyOf()
            mScheduleAdapter = ScheduleVPAdapter(mWeeks, childFragmentManager)
        }

        inflateListener = { inflate ->

            mCoursesViewModel.nowWeek.observe(viewLifecycleOwner, Observer {
                inflate.vp.currentItem = it ?: 0
            })
            // 给ViewPager添加OnPageChangeListener
            VPOnPagerChangeObserver(inflate.vp,
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
                        mCoursesViewModel.mWeekTitle.set(mWeeks[it])
                    })
            //回到本周按钮的点击事件
            course_back_present_week.setOnClickListener {
                mCoursesViewModel.nowWeek.value?.let {
                    inflate.vp.currentItem = it
                }
            }
        }

        //如果需要直接加载课表则直接加载课表，否则为了优化性能可以当需要的时候加载
        //这个判断必须得在上面的[inflateListener]赋值之后执行，否则，其中的初始化代码很有可能不会执行
        if (directLoadCourse == TRUE) {
            course_current_course_container.visibility = View.GONE
            Looper.myQueue().addIdleHandler {
                //给下方ViewPager添加适配器和绑定tab
                loadViewPager()
                settingFollowBottomSheet(1f)
                false
            }
        }


        //获取到ViewModel后进行一些初始化操作
        mCoursesViewModel.courseState = courseState
        course_tv_now_course.setOnClickListener {
            mCoursesViewModel.nowCourse.get()?.let { course ->
                mDialogHelper.showDialog(MutableList(1) { course })
            }
        }

//            mCoursesViewModel.
        mCoursesViewModel.toastEvent.observe(viewLifecycleOwner, Observer { str -> str?.let { CyxbsToast.makeText(activity, it, Toast.LENGTH_SHORT).show() } })
        mCoursesViewModel.longToastEvent.observe(viewLifecycleOwner, Observer { str -> str?.let { CyxbsToast.makeText(activity, it, Toast.LENGTH_LONG).show() } })
        mCoursesViewModel.isShowBackPresentWeek.observe(viewLifecycleOwner, Observer {
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
        //对头部课表头部信息进行一系列初始化
        initHead()
    }

    private fun loadViewPager() {
        inflateView.vp.adapter = mScheduleAdapter
        tab_layout.setupWithViewPager(inflateView.vp)
        mCoursesViewModel.nowWeek.value?.let { nowWeek ->
            inflateView.vp.currentItem = nowWeek
        }
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
            //显示周数选择，隐藏周数显示和最近课表
            course_header_week_select_content.visibility = View.VISIBLE
            course_header_show.visibility = View.GONE
        }

        course_tv_which_week.setOnClickListener { isShow() }
        course_this_week_tips.setOnClickListener { isShow() }

        course_header_back.setOnClickListener {
            TransitionManager.beginDelayedTransition(fl, Slide().apply { slideEdge = Gravity.START })
            course_header_week_select_content.visibility = View.GONE
            course_header_show.visibility = View.VISIBLE
        }
        mCoursesViewModel.mWeekTitle.set("")

        //给整个头部设置点击事件，点击展开整个BottomSheet
        course_header.setOnClickListener {
            EventBus.getDefault().post(NotifyBottomSheetToExpandEvent(true))
        }
    }

    /**
     * 根据主页的fragment的滑动状态来设置当前头部显示效果
     * @param state 从0到1，1表示BottomSheet完全展开
     */
    private fun settingFollowBottomSheet(state: Float) {
        //todo 给tip小块加上动画的话和BottomSheet一起会有性能问题
//        mCoursesViewModel.setTipsState(state, course_tip)
        //判断周数选择是不是显示的
        if (course_header_week_select_content.visibility == View.GONE) {
            headViewAlphaChange(state)
        } else {
            if (state == 0f) {
                //增加转场动画
                TransitionManager.beginDelayedTransition(fl, Slide().apply {
                    slideEdge = Gravity.START
                    duration = 200
                })
                course_header_week_select_content.visibility = View.GONE
                course_header_show.visibility = View.VISIBLE
                headViewAlphaChange(state)
            }
        }
    }

    private fun headViewAlphaChange(state: Float) {
        //如果周数选择没有显示就对最近课表和周数做透明度变换
        course_current_course_container.visibility = View.VISIBLE
        course_current_course_container.alpha = 1 - state
        //对周数选择做透明度变换
        course_current_course_week_select_container.alpha = state
        //如果课表子页还没有加载，则不显示周数
        if (inflateView.vp.adapter == null) {
            //没有加载隐藏周数显示
            course_current_course_week_select_container.visibility = View.GONE
        } else {
            //加载了就显示周数
            course_current_course_week_select_container.visibility = View.VISIBLE
        }
        //根据不同的最终状态确认,显示和隐藏
        if (state == 0f) {
            course_current_course_week_select_container.visibility = View.GONE
        } else if (state == 1f) {
            course_current_course_container.visibility = View.GONE
        }
    }


    /**
     * 如果没有直接加载课表ViewPager子页，这个可以用来通知加载
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loadCoursePage(loadCourse: LoadCourse) {
        if (loadCourse.isUserSee) {
            course_current_course_week_select_container.visibility = View.VISIBLE
        }
        //给下方ViewPager添加适配器和绑定tab
        loadViewPager()
    }


    /**
     * 这个方法用于小部件点击打开课表详情页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun showDialogFromWidget(event: WidgetCourseEvent<WidgetCourse.DataBean>) {
        val mCourse = changeLibBeanToCourse(event.mutableList[0])
        mDialogHelper.showDialog(mutableListOf(mCourse))
        EventBus.getDefault().removeStickyEvent(event)
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