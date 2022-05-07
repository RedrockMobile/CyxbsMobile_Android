@file:Suppress("UNCHECKED_CAST")

package com.mredrock.cyxbs.main.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.main.IMainService
import com.mredrock.cyxbs.api.main.MAIN_MAIN
import com.mredrock.cyxbs.api.update.AppUpdateStatus
import com.mredrock.cyxbs.api.update.IAppUpdateService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.LoginConfig
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.event.LoadCourse
import com.mredrock.cyxbs.common.event.NotifyBottomSheetToExpandEvent
import com.mredrock.cyxbs.common.event.RefreshQaEvent
import com.mredrock.cyxbs.common.mark.ActionLoginStatusSubscriber
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.debug
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.adapter.MainAdapter
import com.mredrock.cyxbs.main.components.DebugDataDialog
import com.mredrock.cyxbs.main.service.NotifySignWorker
import com.mredrock.cyxbs.main.utils.BottomNavigationHelper
import com.mredrock.cyxbs.main.utils.Const.IS_SWITCH2_SELECT
import com.mredrock.cyxbs.main.utils.Const.NOTIFY_TAG
import com.mredrock.cyxbs.main.utils.NotificationSp
import com.mredrock.cyxbs.main.viewmodel.MainViewModel
import com.umeng.message.PushAgent
import kotlinx.android.synthetic.main.main_activity_main.*
import kotlinx.android.synthetic.main.main_bottom_nav.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*
import kotlin.properties.Delegates

@Route(path = MAIN_MAIN)
class MainActivity : BaseViewModelActivity<MainViewModel>(),
    EventBusLifecycleSubscriber, ActionLoginStatusSubscriber {

    //todo记得改为false
    private var isSign = true


    override val loginConfig = LoginConfig(
        isWarnUser = false,
        isCheckLogin = true
    )

    private lateinit var mainService: IMainService

    /**
     * 这个变量切记千万不能搬到viewModel,这个变量需要跟activity同生共死
     * 以保障activity异常重启时，这个值会被刷新，activity异常销毁重启viewModel仍在
     */
    private var isLoadCourse = true
    var lastState = BottomSheetBehavior.STATE_COLLAPSED

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private var bottomHelper: BottomNavigationHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //友盟 应用活跃统计 理应在用户同意以后调用
        PushAgent.getInstance(BaseApp.appContext).onAppStart()

        setTheme(R.style.MainActivityTheme)//恢复真正的主题，保证WindowBackground为主题色
        super.onCreate(savedInstanceState)
        // 暂时不要在mainActivity里面使用dataBinding，会有一个量级较大的闪退
        setContentView(R.layout.main_activity_main)
        initSignObserver()
    }

    private fun initSignObserver() {
        viewModel.checkInStatus.observe {
            it?.let {
                isSign = it
                Log.d("NotifySignWorker", "今日是否已经签到 : $it ")
            }
        }
    }

    /**
     * 大前提：用户允许签到提醒
     * 如果已经签到&&时间小于18 shouldNotify false next day true
     * 已经签到&&时间大于等于18  shouldNotify false next day true
     * 没有签到&&时间小于18  shouldNotify false next day false
     * 没有签到&&时间大于等于18 shouldNotify true next day true
     */
    override fun onStart() {
        viewModel.getCheckInStatus()
        super.onStart()
        //用户不允许提醒 直接返回
        if (!NotificationSp.getBoolean(IS_SWITCH2_SELECT, false)) return
        val workManager = WorkManager.getInstance(applicationContext)
        val hour = Calendar.HOUR_OF_DAY
        var data: Data by Delegates.notNull()
        var dailySignWorkRequest: OneTimeWorkRequest by Delegates.notNull()
        val dailySignWorkRequestBuilder =
            OneTimeWorkRequestBuilder<NotifySignWorker>().addTag(NOTIFY_TAG)

        if (isSign) {
            data = Data.Builder()
                .putBoolean("isNextDay", true)
                .putBoolean("shouldNotify", false)
                .build()
        } else {
            data = if (hour < 18) {
                Data.Builder()
                    .putBoolean("isNextDay", false)
                    .putBoolean("shouldNotify", false)
                    .build()
            } else {
                Data.Builder()
                    .putBoolean("isNextDay", true)
                    .putBoolean("shouldNotify", true)
                    .build()
            }
        }

        dailySignWorkRequest = dailySignWorkRequestBuilder
            .setInputData(data)
            .build()
        workManager.enqueue(dailySignWorkRequest)
    }

    override fun initPage(isLoginElseTourist: Boolean, savedInstanceState: Bundle?) {
        /**
         * 关于这个判断，用于从文件或者其他非launcher打开->按home->点击launcher热启动导致多了一个
         * flag，导致新建this
         * 由于取消了SplashActivity，如果MainActivity的lunchMode为singleTas，热启动会导致所有出栈
         * 改为了singleTop
         * @see android.content.Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
         */
        if ((intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish()
            return
        }
        checkSplash()
        // 暂时注释友盟
//        InAppMessageManager.getInstance(com.mredrock.cyxbs.appContext)
//            .showCardMessage(this, "课表主页面") {} //友盟插屏消息关闭之后调用，暂未写功能
        mainService = ServiceManager.getService(IMainService::class.java)//初始化主模块服务
        viewModel.startPage.observe(
            this,
            Observer { starPage -> viewModel.initStartPage(starPage) })
        initUpdate()//初始化app更新服务
        initBottom()//初始化底部导航栏
        initBottomSheetBehavior()//初始化上拉容器BottomSheet课表
        initFragments(isLoginElseTourist, savedInstanceState)//对四个主要的fragment进行配置
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (referrer != null && referrer?.toString() == "android-app://com.mredrock.cyxbs") {
                //如果来自登陆界面，则进行是否设置密保的判断
                viewModel.checkBindingEmail(
                    ServiceManager.getService(IAccountService::class.java).getUserService()
                        .getStuNum()
                ) {
                    val bindingEmailDialog = Dialog(this, R.style.transparent_dialog)
                    bindingEmailDialog.setContentView(R.layout.main_dialog_bind_email)
                    val confirm =
                        bindingEmailDialog.findViewById<AppCompatButton>(R.id.main_bt_bind_email_confirm)
                    val cancel =
                        bindingEmailDialog.findViewById<AppCompatButton>(R.id.main_bt_bind_email_cancel)
                    confirm.setOnClickListener {
                        //跳转到修改密码界面
                        ARouter.getInstance().build(MINE_BIND_EMAIL).navigation()
                        bindingEmailDialog.dismiss()
                    }
                    cancel.setOnClickListener {
                        bindingEmailDialog.dismiss()
                    }
                    bindingEmailDialog.show()
                }
            }
        }
    }


    private fun initUpdate() {
        ServiceManager.getService(IAppUpdateService::class.java).apply {
            getUpdateStatus().observe {
                when (it) {
                    AppUpdateStatus.UNCHECK -> checkUpdate()
                    AppUpdateStatus.DATED -> noticeUpdate(this@MainActivity)
                    AppUpdateStatus.TO_BE_INSTALLED -> installUpdate(this@MainActivity)
                    else -> Unit
                }
            }
        }
    }

    private fun initBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(course_bottom_sheet_content)
        /**
         * 没有选择[android:fitsSystemWindows="true"]让系统预留一个状态栏高度
         * 而是手动加上一个状态栏高度的内边距，
         * 为了解决CoordinatorLayout定制的fitsSystemWindows在BottomSheet正完全展开时activity
         * 异常重启后fitsSystemWindows失效的问题
         */

        course_bottom_sheet_content.topPadding =
            course_bottom_sheet_content.topPadding + getStatusBarHeight()
        bottomSheetBehavior.peekHeight =
            bottomSheetBehavior.peekHeight + course_bottom_sheet_content.topPadding
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                mainService.obtainBottomSheetStateLiveData().value = slideOffset
                if (main_view_pager.currentItem != 1 && slideOffset >= 0)
                    ll_nav_main_container.translationY = ll_nav_main_container.height * slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (!ServiceManager.getService(IAccountService::class.java).getVerifyService()
                        .isLogin() && newState == BottomSheetBehavior.STATE_DRAGGING
                ) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    return
                }
                //如果是第一次进入展开则加载详细的课表子页
                if (isLoadCourse && bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
                    && lastState != BottomSheetBehavior.STATE_EXPANDED && !viewModel.isCourseDirectShow
                ) {
                    EventBus.getDefault().post(LoadCourse())
                    isLoadCourse = false
                }
                //对状态Bottom的状态进行记录，这里只记录了打开和关闭
                lastState = when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> BottomSheetBehavior.STATE_EXPANDED
                    BottomSheetBehavior.STATE_COLLAPSED -> BottomSheetBehavior.STATE_COLLAPSED
                    else -> lastState
                }
            }
        })
    }

    override fun onBackPressed() {
        //课表判断展开返回判断
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            moveTaskToBack(true)
        }
    }

    private fun initBottom() {
        // ViewPager2默认模式OFFSCREEN_PAGE_LIMIT_DEFAULT为懒加载模式
        main_view_pager.adapter = MainAdapter(this)
        main_view_pager.isUserInputEnabled = false
        ll_nav_main_container.onTouch { _, _ -> }//防止穿透点击或者滑动，子View无法处理默认消耗
        //底部导航栏的控制初始化
        bottomHelper = BottomNavigationHelper(arrayOf(explore, qa, mine)) {
            // 暂时注释友盟
//            MobclickAgent.onEvent(
//                this, CyxbsMob.Event.BOTTOM_TAB_CLICK, mutableMapOf(
//                    Pair(CyxbsMob.Key.TAB_INDEX, it.toString())
//                )
//            )
            if (it == 1 && bottomHelper?.peeCheckedItemPosition == 1)
                EventBus.getDefault().post(RefreshQaEvent())

            when (it) {
                0 -> {
                    bottomSheetBehavior.isHideable = false
                    //如果用户选择了点击App优先展示课表，会存在课表自动下滑的情况，需要加以判断
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        //如果自邮问而来，则清除z轴高度
                        ll_nav_main_container.elevation = 0f
                    }
                }
                1 -> {
                    //切换到邮问时，bottomSheet变为隐藏，并添加z轴高度
                    bottomSheetBehavior.isHideable = true
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    ll_nav_main_container.elevation = dp2px(4f).toFloat()
                    //点击Tab刷新邮问
                    if (bottomHelper?.peeCheckedItemPosition == 1) EventBus.getDefault()
                        .post(RefreshQaEvent())
                }
                2 -> {
                    bottomSheetBehavior.isHideable = false
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    ll_nav_main_container.elevation = 0f
                }
            }
            main_view_pager.setCurrentItem(it, false)
        }

        // 长按我的展现测试数据
        debug {
            mine.setOnLongClickListener {
                DebugDataDialog(this).show()
                true
            }
        }
    }


    /**
     * 检查闪屏页状态
     */
    private fun checkSplash() {
        viewModel.splashVisibility.observe(this, Observer {
            main_activity_splash_viewStub.visibility = it
        })
        //判断是否下载了Splash图，下载了就直接显示
        if (isDownloadSplash(this@MainActivity)) {
            if (!ServiceManager.getService(IAccountService::class.java).getVerifyService()
                    .isLogin() && !ServiceManager.getService(IAccountService::class.java)
                    .getVerifyService().isTouristMode()
            ) {
                //表示，已经下载了，但是用户主动退出登录
                //这里判断的依据是防止onCreate()加载了闪屏页fragment，onStart()跳转到登录Activity，MainActivity被销毁，fragment没有activity
                return
            }
            main_activity_splash_viewStub.onTouch { _, _ -> }//防止穿透点击
            viewModel.splashVisibility.value = View.VISIBLE//显示闪屏页容器
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_activity_splash_viewStub, SplashFragment()).commit()
        }
        //检查网络有没有闪屏页，有的话下载，下次显示
        viewModel.getStartPage()
    }


    private fun initFragments(isLoginElseTourist: Boolean, bundle: Bundle?) {
        //取得是否优先显示课表的设置
        viewModel.isCourseDirectShow = defaultSharedPreferences.getBoolean(COURSE_SHOW_STATE, false)
        //如果为异常启动，则可以取得选中状态
        bundle?.getInt(BOTTOM_SHEET_STATE)
            ?.let { viewModel.isCourseDirectShow = it == BottomSheetBehavior.STATE_EXPANDED }
        lastState =
            if (viewModel.isCourseDirectShow) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        val isShortcut = intent.action == FAST
        if ((viewModel.isCourseDirectShow || isShortcut) && isLoginElseTourist) {
            intent.action = ""
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            ll_nav_main_container.translationY = 10000f
            val courseFragment = supportFragmentManager.findFragmentByTag(COURSE_ENTRY)
                ?: ServiceManager.getService(COURSE_ENTRY)
            courseFragment.arguments = Bundle().apply { putString(COURSE_DIRECT_LOAD, TRUE) }
            //加载课表，并在滑动下拉课表容器中添加整个课表
            supportFragmentManager.commit {
                replace(R.id.course_bottom_sheet_content, courseFragment, COURSE_ENTRY)
                show(courseFragment)
            }
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            //加载课表并给课表传递值，让它不要直接加载详细的课表，只用加载现在可见的头部就好
            val courseFragment = supportFragmentManager.findFragmentByTag(COURSE_ENTRY)
                ?: ServiceManager.getService(COURSE_ENTRY)
            courseFragment.arguments = Bundle().apply { putString(COURSE_DIRECT_LOAD, FALSE) }
            supportFragmentManager.commit {
                replace(R.id.course_bottom_sheet_content, courseFragment, COURSE_ENTRY)
                show(courseFragment)
            }
        }
        //加载发现
        bottomHelper?.selectTab(0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BOTTOM_SHEET_STATE, bottomSheetBehavior.state)
        bottomHelper?.peeCheckedItemPosition?.let { outState.putInt(NAV_SELECT, it) }
    }

    /**
     * 恢复一些在初始化之后才能够进行的操作，比如控件状态，或者点击状态
     * @param savedInstanceState 不为空的savedInstanceState，用于获取恢复数据
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bottomHelper?.selectTab(savedInstanceState.getInt(NAV_SELECT))
    }

    /**
     * 接收bottomSheet头部的点击事件，用来点击打开BottomSheet
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun acceptNotifyBottomSheetToExpandEvent(notifyBottomSheetToExpandEvent: NotifyBottomSheetToExpandEvent) {
        if (notifyBottomSheetToExpandEvent.isToExpand) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }


    companion object {
        const val BOTTOM_SHEET_STATE = "BOTTOM_SHEET_STATE"
        const val NAV_SELECT = "NAV_SELECT"
        const val SPLASH_PHOTO_NAME = "splash_photo.jpg"
        const val SPLASH_PHOTO_LOCATION = "splash_store_location"
        const val FAST = "com.mredrock.cyxbs.action.COURSE"
    }

    fun isDownloadSplash(context: Context): Boolean {
        return getSplashFile(context).exists()
    }

    fun getSplashFile(context: Context): File {
        val appDir = getDir(context)//下载目录
        val fileName = SPLASH_PHOTO_NAME
        return File("$appDir/$fileName")
    }

    fun getDir(context: Context): File {
        val pictureFolder = context.externalCacheDir
        val appDir = File("$pictureFolder/$SPLASH_PHOTO_LOCATION")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return appDir
    }
}
