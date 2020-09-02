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
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.account.IUserStateService
import com.mredrock.cyxbs.common.service.main.IMainService
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.pressToZoomOut
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.ScheduleVPAdapter
import com.mredrock.cyxbs.course.databinding.CourseFragmentCourseContainerBinding
import com.mredrock.cyxbs.course.event.*
import com.mredrock.cyxbs.course.lifecycle.VPOnPagerChangeObserver
import com.mredrock.cyxbs.course.ui.ScheduleDetailBottomSheetDialogHelper
import com.mredrock.cyxbs.course.utils.AffairToCalendar
import com.mredrock.cyxbs.course.utils.changeLibBeanToCourse
import com.mredrock.cyxbs.course.viewmodels.CoursesViewModel
import com.mredrock.cyxbs.course.viewmodels.NoCourseInviteViewModel
import com.umeng.analytics.MobclickAgent
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
class CourseContainerEntryFragment : BaseViewModelFragment<CoursesViewModel>(), IUserStateService.StateListener, EventBusLifecycleSubscriber {
    override val openStatistics: Boolean
        get() = false

    private var courseState = CourseState.OrdinaryCourse

    override val viewModelClass: Class<CoursesViewModel> = CoursesViewModel::class.java


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

    // 当前课表是哪个账号的
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

    //用于没课约的ViewModel
    private var mNoCourseInviteViewModel: NoCourseInviteViewModel? = null

    //当前Fragment的根布局的Binding
    private lateinit var mBinding: CourseFragmentCourseContainerBinding

    //每一周的Viewpager所对应的tab的显示的字符串
    private lateinit var mRawWeeks: Array<String>
    private lateinit var mWeeks: Array<String>

    private val mDialogHelper: ScheduleDetailBottomSheetDialogHelper by lazy(LazyThreadSafetyMode.NONE) {
        ScheduleDetailBottomSheetDialogHelper(context!!)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ServiceManager.getService(IAccountService::class.java).getVerifyService().addOnStateChangedListener(this)
        initFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ServiceManager.getService(IAccountService::class.java).getVerifyService().removeStateChangedListener(this)
    }

    /**
     * 对当前Fragment进行一系列初始化
     */
    private fun initFragment() {
        if (!accountService.getVerifyService().isLogin()) {
            course_header_show.invisible()
            rl_tourist_course_title.visible()
            fl.setOnClickListener {}//处理所有未处理的点击事件，防止穿透点击或者滑动
            EventBus.getDefault().postSticky(CurrentDateInformationEvent(getString(R.string.course_tourist_date_txt)))
            return
        }
        rl_tourist_course_title.gone()
        course_header_show.visible()
        //如果没有被添加进Activity，Fragment会抛出not attach a context的错误
        if (!isAdded) return

        fl.setOnClickListener {}//处理所有未处理的点击事件，防止穿透点击或者滑动

        //获取主模块服务并添加bottom状态的监听
        ServiceManager.getService(IMainService::class.java).obtainBottomSheetStateLiveData().observe(viewLifecycleOwner, Observer {
            settingFollowBottomSheet(it)
        })

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
                mBinding.coursesViewModel = viewModel
                context?.let { viewModel.getSchedulesDataFromLocalThenNetwork(accountService.getUserService().getStuNum()) }
            }
            //如果是没课约
            CourseState.NoClassInvitationCourse -> {
                mBinding.coursesViewModel = viewModel
                mNoCourseInviteViewModel =
                        ViewModelProvider(this, NoCourseInviteViewModel.Factory(mStuNumList!!, mNameList!!))
                                .get(NoCourseInviteViewModel::class.java)
                mNoCourseInviteViewModel?.getCourses()
            }
            //如果是查询其他同学的课表
            CourseState.OtherCourse -> {
                mBinding.coursesViewModel = viewModel
                context?.let { viewModel.getSchedulesDataFromLocalThenNetwork(mStuNum!!, true) }
            }
            //如果是老师课表
            CourseState.TeacherCourse -> {
                mBinding.coursesViewModel = viewModel
                viewModel.isTeaCourse = true
                mOthersTeaName?.let {
                    viewModel.mUserName = it
                }
                context?.let { viewModel.getSchedulesDataFromLocalThenNetwork(mOthersTeaNum!!, true) }
            }
        }

        //初始化周数tab的文字和下面课表的Adapter
        resources.getStringArray(R.array.course_course_weeks_strings).let {
            mRawWeeks = it
            mWeeks = mRawWeeks.copyOf()
            mScheduleAdapter = ScheduleVPAdapter(mWeeks, childFragmentManager)
        }

        inflateListener = { inflate ->

            viewModel.nowWeek.observe(viewLifecycleOwner, Observer {
                inflate.vp.currentItem = it ?: 0
            })
            // 给ViewPager添加OnPageChangeListener
            VPOnPagerChangeObserver(inflate.vp,
                    mOnPageSelected = {
                        // 当ViewPager发生了滑动，清理课表上加备忘的View
                        EventBus.getDefault().post(DismissAddAffairViewEvent())
                        // 本周提示文字是否显示
                        viewModel.isShowPresentTips.set(
                                when (it) {
                                    //零判断需要在最前面，应为nowWeekkennel也会为0，
                                    // 但是整学期不需要显示本周
                                    0 -> View.GONE
                                    viewModel.nowWeek.value -> View.VISIBLE
                                    else -> View.GONE
                                }
                        )
                        viewModel.isShowBackPresentWeek.value = if (viewModel.nowWeek.value == it) View.GONE else View.VISIBLE
                        viewModel.mWeekTitle.set(mWeeks[it])
                    })
            //回到本周按钮的点击事件
            course_back_present_week.setOnClickListener {
                viewModel.nowWeek.value?.let {
                    inflate.vp.currentItem = it
                }
            }
            course_back_present_week.pressToZoomOut()
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
        viewModel.courseState = courseState
        course_tv_now_course.setOnClickListener {
            viewModel.nowCourse.get()?.let { course ->
                mDialogHelper.showDialog(MutableList(1) { course })
            }
        }

        viewModel.toastEvent.observe(viewLifecycleOwner, Observer { str -> str?.let { CyxbsToast.makeText(activity, it, Toast.LENGTH_SHORT).show() } })
        viewModel.longToastEvent.observe(viewLifecycleOwner, Observer { str -> str?.let { CyxbsToast.makeText(activity, it, Toast.LENGTH_LONG).show() } })
        viewModel.isShowBackPresentWeek.observe(viewLifecycleOwner, Observer {
            /**
             * 这里为什么要判空呢，因为 viewModel.isShowBackPresentWeek
             * 在切换黑夜模式的时候这个变量的回调会启动，但是这时候[course_current_course_week_select_container]
             * 还没有生成，进一步说在应用内开启黑夜模式因为会重启activity但是[viewModel]没有
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
        MobclickAgent.onEvent(context,CyxbsMob.Event.COURSE_SHOW)
        inflateView.vp.adapter = mScheduleAdapter
        tab_layout.setupWithViewPager(inflateView.vp)
        viewModel.nowWeek.value?.let { nowWeek ->
            inflateView.vp.currentItem = nowWeek
            viewModel.mWeekTitle.set(
                    mWeeks[
                            if (inflateView.vp.currentItem <= 21) inflateView.vp.currentItem else 0
                    ]
            )
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
//        viewModel.setTipsState(state, course_tip)
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

    override fun onStart() {
        super.onStart()
        //构建最近课表（head上面的课程显示），第一次构建并不是这里构建的
        //而是在获取课表数据之后更新的,
        //这里再调用一次是为了能够在课表可以在不用重复获取课表数据时就更新头部课程
        viewModel.buildHeadData()
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
        viewModel.refreshAffairFromInternet()
    }

    /**
     * 这个方法用于添加事务后重新进行网络请求获取数据
     *
     * @param addAffairEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addAffairs(addAffairEvent: AddAffairEvent) {
        EventBus.getDefault().post(RefreshEvent(true))
        viewModel.refreshAffairFromInternet()
    }

    /**
     * 这个方法用于接收设置课表上备忘项目显示模式的更新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showModeChange(e: ShowModeChangeEvent) {
        viewModel.refreshAffairFromInternet()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun modifyAffairs(modifyAffairEvent: ModifyAffairEvent) {
        viewModel.refreshAffairFromInternet()
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

    enum class CourseState {
        OrdinaryCourse, TeacherCourse, OtherCourse, NoClassInvitationCourse
    }

    override fun onStateChanged(state: IUserStateService.UserState) {
        if (state == IUserStateService.UserState.LOGIN) {
            initFragment()
        } else {
            Thread {
                try {
                    ViewModelProvider(this).get(CoursesViewModel::class.java).clearCache()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

}