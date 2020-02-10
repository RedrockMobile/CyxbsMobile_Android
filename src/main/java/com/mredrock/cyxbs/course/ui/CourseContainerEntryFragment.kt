package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.mredrock.cyxbs.course.utils.AffairToCalendar
import com.mredrock.cyxbs.course.utils.changeLibBeanToCourse
import com.mredrock.cyxbs.course.viewmodels.CoursesViewModel
import com.mredrock.cyxbs.course.viewmodels.NoCourseInviteViewModel
import kotlinx.android.synthetic.main.course_fragment_course_container.*
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

    //如果是在获取他人课表这个值不为空
    private var mOthersStuNum: String? = null


    //当前课表的显示的状态
    var courseState = CourseState.OrdinaryCourse

    // 当前课表是哪个账号的，如果为空则返回当前登陆账号
    var mStuNum = object : ObservableField<String>() {
        val stuNum = ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()
        override fun set(value: String?) {
            if (value == null) {
                super.set(stuNum)
            } else {
                super.set(value)
                courseState = CourseState.OtherCourse
            }
        }

        override fun get(): String? {
            return if (super.get() == null) {
                stuNum
            } else {
                super.get()
            }
        }
    }

    //如果是没课约，这两个值不为空
    var mStuNumList = object : ObservableField<ArrayList<String>>() {
        override fun set(value: ArrayList<String>?) {
            super.set(value)
            if (value != null) {
                courseState = CourseState.NoClassInvitationCourse
            }
        }
    }
    var mNameList = object : ObservableField<ArrayList<String>>() {
        override fun set(value: ArrayList<String>?) {
            super.set(value)
            if (value != null) {
                courseState = CourseState.NoClassInvitationCourse
            }
        }
    }

    //是否直接加载课表子页，默认直接加载，除非传值过来设置为false过来，后面可以懒加载
    lateinit var directLoadCourse:String

    //每一周课表的ViewPager的Adapter
    private lateinit var mScheduleAdapter: ScheduleVPAdapter

    //普通课程和查课表需要的ViewModel
    lateinit var mCoursesViewModel: CoursesViewModel

    //用于没课约的ViewModel
    var mNoCourseInviteViewModel: NoCourseInviteViewModel?= null

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
            mStuNum.set(bundle.getString(OTHERS_STU_NUM))
            mStuNumList.set(bundle.getStringArrayList(STU_NUM_LIST))
            mNameList.set(bundle.getStringArrayList(STU_NAME_LIST))
            directLoadCourse = bundle.getString(COURSE_DIRECT_LOAD)?: TRUE
        }


        //根据当前Entry的状态来获取相应的ViewModel
        when (courseState) {
            CourseState.NoClassInvitationCourse -> {
                mCoursesViewModel = ViewModelProviders.of(activity!!).get(CoursesViewModel::class.java)
                mBinding.coursesViewModel = mCoursesViewModel
                mNoCourseInviteViewModel = ViewModelProviders
                        .of(this, NoCourseInviteViewModel
                                .Factory(mStuNumList.get()!!, mNameList.get()!!))
                        .get(NoCourseInviteViewModel::class.java)
                mNoCourseInviteViewModel?.getCourses()
                hideNowCourseHead()
            }
            CourseState.OtherCourse -> {
                mCoursesViewModel = ViewModelProviders.of(this).get(CoursesViewModel::class.java)
                mBinding.coursesViewModel = mCoursesViewModel
                mCoursesViewModel.mStuNum = mStuNum.get()!!//这里不可能为空
                context?.let { mCoursesViewModel.refreshScheduleData(it) }
                hideNowCourseHead()
            }
            CourseState.OrdinaryCourse -> {
                mCoursesViewModel = ViewModelProviders.of(activity!!).get(CoursesViewModel::class.java)
                mBinding.coursesViewModel = mCoursesViewModel
                mCoursesViewModel.getSchedulesDataFromDataBase(activity!!)
            }
        }

        //初始化周数tab和下面课表的Adapter
        resources.getStringArray(R.array.course_course_weeks_strings).let {
            mRawWeeks = it
            mWeeks = mRawWeeks.copyOf()
            mScheduleAdapter = ScheduleVPAdapter(mWeeks, childFragmentManager)
        }
        if (directLoadCourse== TRUE){
            //给下方ViewPager添加适配器和绑定tab
            mBinding.vp.adapter = mScheduleAdapter
            mBinding.tabLayout.setupWithViewPager(mBinding.vp)
        }



        //获取到ViewModel后进行一些初始化操作
        mCoursesViewModel.nowWeek.observe(this, Observer { nowWeek ->
            // 跳转到当前周
            mBinding.vp.currentItem = nowWeek ?: 0
        })

        // 给ViewPager添加OnPageChangeListener
        lifecycle.addObserver(VPOnPagerChangeObserver(mBinding.vp,
                mOnPageSelected = {
                    // 当ViewPager发生了滑动，清理课表上加备忘的View
                    EventBus.getDefault().post(DismissAddAffairViewEvent())
                    // 当ViewPager发生了滑动，对Head上的周数进行改变
                    mCoursesViewModel.isShowPresentTips.set(
                            when {
                                it == 0 -> {
                                    View.GONE
                                }
                                mCoursesViewModel.nowWeek.value == it -> {
                                    View.VISIBLE
                                }
                                else -> {
                                    View.GONE
                                }
                            }
                    )
                    mCoursesViewModel.isShowBackPresentWeek.set(
                            if (mCoursesViewModel.nowWeek.value == it) {
                                View.GONE
                            } else {
                                View.VISIBLE
                            }
                    )
                    mCoursesViewModel.mWeekTitle.set(mScheduleAdapter.getPageTitle(it).toString())
                }))


        //对头部课表头部信息进行一系列初始化
        initHead()
    }

    /**
     * 隐藏当前课程系列控件
     */
    private fun hideNowCourseHead() {
        course_header_select_content.visibility = View.GONE
        course_current_course_container.visibility = View.GONE
        course_current_course_week_select_container.visibility = View.VISIBLE
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
            TransitionManager.beginDelayedTransition(fl, TransitionSet().apply {
                addTransition(Slide().apply {
                    slideEdge = Gravity.START
                })
            })
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
        mCoursesViewModel.refreshScheduleData(this.context!!)
    }

    /**
     * 这个方法用于添加事务后重新进行网络请求获取数据
     *
     * @param addAffairEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addAffairs(addAffairEvent: AddAffairEvent) {
        EventBus.getDefault().post(RefreshEvent(true))
        mCoursesViewModel.refreshScheduleData(this.context!!)
    }

    /**
     * 这个方法用于接收设置课表上备忘项目显示模式的更新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showModeChange(e: ShowModeChangeEvent) {
        mCoursesViewModel.refreshScheduleData(this.context!!)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun modifyAffairs(modifyAffairEvent: ModifyAffairEvent) {
        mCoursesViewModel.refreshScheduleData(this.context!!)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loadCoursePage(loadCourse: LoadCourse){
        //给下方ViewPager添加适配器和绑定tab
        mBinding.vp.adapter = mScheduleAdapter
        mBinding.tabLayout.setupWithViewPager(mBinding.vp)
    }

    /**
     * main模块里BottomSheet滑动对应的头部变化
     */
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

    enum class CourseState{
        OrdinaryCourse,TeacherCourse,OtherCourse,NoClassInvitationCourse
    }
}