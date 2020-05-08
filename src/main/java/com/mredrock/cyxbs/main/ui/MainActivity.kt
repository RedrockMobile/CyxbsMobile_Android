@file:Suppress("UNCHECKED_CAST")

package com.mredrock.cyxbs.main.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.View.GONE
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.LoginConfig
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.event.BottomSheetStateEvent
import com.mredrock.cyxbs.common.event.LoadCourse
import com.mredrock.cyxbs.common.event.NotifyBottomSheetToExpandEvent
import com.mredrock.cyxbs.common.event.RefreshQaEvent
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.update.AppUpdateStatus
import com.mredrock.cyxbs.common.service.update.IAppUpdateService
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.getStatusBarHeight
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.utils.*
import com.mredrock.cyxbs.main.viewmodel.MainViewModel
import com.umeng.message.inapp.InAppMessageManager
import kotlinx.android.synthetic.main.main_activity_main.*
import kotlinx.android.synthetic.main.main_view_stub_splash.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.topPadding

@Route(path = MAIN_MAIN)
class MainActivity : BaseViewModelActivity<MainViewModel>(), EventBusLifecycleSubscriber {

    override val viewModelClass = MainViewModel::class.java

    override val isFragmentActivity = true

    override val loginConfig = LoginConfig(isWarnUser = false)

    /**
     * 这个变量切记千万不能搬到viewModel,这个变量需要跟activity同生共死
     * 以保障activity异常重启时，这个值会被刷新，activity异常销毁重启viewModel仍在
     */
    private var isLoadCourse = true
    var lastState = BottomSheetBehavior.STATE_COLLAPSED

    //这个变量主要是做标志使用，防止Activity异常重启造成的Fragment多次加载
    var mainPageLoadedFragmentClassList = HashMap<String, String>()


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private lateinit var bottomHelper: BottomNavigationHelper


    //四个需要组装的fragment(懒加载)
    private val courseFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { getFragment(COURSE_ENTRY, this) }
    private val discoverFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { getFragment(DISCOVER_ENTRY, this) }
    private val qaFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { getFragment(QA_ENTRY, this) }
    private val mineFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { getFragment(MINE_ENTRY, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.MainActivityTheme)//恢复真正的主题，保证WindowBackground为主题色
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_main)
        //检查闪屏页是否需要显示，闪屏页现在业务逻辑相对简单，为了减少启动时不必要的开销，暂时放到这里
        //后期，跳转逻辑复杂的话可以考虑将闪屏页独立成Activity
        checkSplash()
        //检查是否登录，虽然BaseActivity里面也有登录检查，
        //但是这里因为涉及到闪屏页的部分逻辑，不得不放弃BaseActivity的登录检查
        initActivity(savedInstanceState)//Activity相关初始化
    }

    /**
     * 一些非重量级初始化操作
     */
    private fun initActivity(bundle: Bundle?) {
        InAppMessageManager.getInstance(BaseApp.context).showCardMessage(this, "课表主页面") {} //友盟插屏消息关闭之后调用，暂未写功能
        viewModel.startPage.observe(this, Observer { starPage -> viewModel.initStartPage(starPage) })
        initUpdate()//初始化app更新服务
        initBottom()//初始化底部导航栏
        initBottomSheetBehavior()//初始化上拉容器BottomSheet课表
        initFragments(bundle)//对四个主要的fragment进行配置
    }

    private fun initUpdate() {
        ServiceManager.getService(IAppUpdateService::class.java).apply {
            getUpdateStatus().observe {
                when (it) {
                    AppUpdateStatus.UNCHECK -> checkUpdate()
                    AppUpdateStatus.DATED -> noticeUpdate(this@MainActivity)
                    AppUpdateStatus.TO_BE_INSTALLED -> installUpdate(this@MainActivity)
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
        course_bottom_sheet_content.topPadding = course_bottom_sheet_content.topPadding + getStatusBarHeight()
        bottomSheetBehavior.peekHeight = bottomSheetBehavior.peekHeight + course_bottom_sheet_content.topPadding
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                EventBus.getDefault().post(BottomSheetStateEvent(slideOffset))
                ll_nav_main_container.translationY = ll_nav_main_container.height * slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //如果是第一次进入展开则加载详细的课表子页
                if (isLoadCourse && bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
                        && lastState != BottomSheetBehavior.STATE_EXPANDED && !viewModel.isCourseDirectShow) {
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
        //底部导航栏的控制初始化
        bottomHelper = BottomNavigationHelper(arrayOf(explore, qa, mine), arrayOf(
                R.drawable.main_ic_explore_selected,
                R.drawable.main_ic_qa_selected,
                R.drawable.main_ic_mine_selected), arrayOf(
                R.drawable.main_ic_explore_unselected,
                R.drawable.main_ic_qa_unselected,
                R.drawable.main_ic_mine_unselected))
        {
            when (it) {
                0 -> changeFragment(discoverFragment)
                1 -> {
                    //点击Tab刷新邮问
                    if (bottomHelper.peeCheckedItemPosition == 1) EventBus.getDefault().post(RefreshQaEvent())
                    changeFragment(qaFragment)
                }
                2 -> changeFragment(mineFragment)
            }
        }
    }


    private fun checkSplash() {
        //判断是否下载了Splash图，下载了就直接设置
        if (isDownloadSplash(this@MainActivity)) {
            val inflate = main_activity_splash_viewStub.inflate()
            Glide.with(this)
                    .load(getSplashFile(this@MainActivity))
                    .apply(RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(inflate.splash_view)
            object : CountDownTimer(3000, 1000) {
                override fun onFinish() {
                    inflate.visibility = GONE
                }
                override fun onTick(millisUntilFinished: Long) {
                    runOnUiThread {
                        val str = "跳过 ${millisUntilFinished / 1000}"
                        inflate.main_activity_splash_skip.text = str
                    }
                }
            }.start()
            inflate.main_activity_splash_skip.setOnClickListener {
                inflate.visibility = GONE
            }
        }
        //检查网络有没有闪屏页，有的话下载，下次显示
        viewModel.getStartPage()
    }


    private fun initFragments(bundle: Bundle?) {
        //取得是否优先显示课表的设置
        viewModel.isCourseDirectShow = defaultSharedPreferences.getBoolean(COURSE_SHOW_STATE, false)
        //如果为异常启动，则可以取得选中状态
        bundle?.getInt(BOTTOM_SHEET_STATE)?.let { viewModel.isCourseDirectShow = it == BottomSheetBehavior.STATE_EXPANDED }
        (bundle?.getSerializable(PAGE_CLASS_TAG) as? HashMap<String, String>)?.let { mainPageLoadedFragmentClassList = it }
        val isShortcut = intent.action == FAST
        lastState = if (viewModel.isCourseDirectShow) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        if (viewModel.isCourseDirectShow || isShortcut) {
            intent.action = ""
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            ll_nav_main_container.translationY = 10000f
            courseFragment.arguments = Bundle().apply { putString(COURSE_DIRECT_LOAD, TRUE) }
            //加载课表，并在滑动下拉课表容器中添加整个课表
            supportFragmentManager.beginTransaction().apply {
                if (supportFragmentManager.fragments.entryContains(COURSE_ENTRY, this@MainActivity) == null) {
                    add(R.id.course_bottom_sheet_content, courseFragment)
                }
                show(courseFragment)
                commit()
            }
            bottomHelper.selectTab(0)
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            //加载课表并给课表传递值，让它不要直接加载详细的课表，只用加载现在可见的头部就好
            courseFragment.arguments = Bundle().apply { putString(COURSE_DIRECT_LOAD, FALSE) }
            supportFragmentManager.beginTransaction().apply {
                if (supportFragmentManager.fragments.entryContains(COURSE_ENTRY, this@MainActivity) == null) {
                    add(R.id.course_bottom_sheet_content, courseFragment)
                }
                show(courseFragment)
                commit()
            }
            //加载发现
            bottomHelper.selectTab(0)
        }
    }


    /**
     * 进行显示页面的切换
     * @param fragment 用来切换的fragment
     */
    private fun changeFragment(fragment: Fragment) {
        val transition = supportFragmentManager.beginTransaction()
        //遍历隐藏已经加载的fragment
        mainPageLoadedFragmentClassList.filter { COURSE_ENTRY != it.key }.forEach {
            val fragment1 = supportFragmentManager.fragments.entryContains(it.key, this)
            if (fragment1 != null) {
                transition.hide(fragment1)
            }
        }
        //如果这个fragment从来没有加载过，则进行添加
        if (!supportFragmentManager.fragments.contains(fragment)) {
            transition.add(R.id.other_fragment_container, fragment)
        }
        transition.show(fragment)
        transition.commit()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BOTTOM_SHEET_STATE, bottomSheetBehavior.state)
        outState.putInt(NAV_SELECT, bottomHelper.peeCheckedItemPosition)
        outState.putSerializable(PAGE_CLASS_TAG, mainPageLoadedFragmentClassList)
    }

    /**
     * 恢复一些在初始化之后才能够进行的操作，比如控件状态，或者点击状态
     * @param savedInstanceState 不为空的savedInstanceState，用于获取恢复数据
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bottomHelper.selectTab(savedInstanceState.getInt(NAV_SELECT))
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
        const val PAGE_CLASS_TAG = "PAGE_CLASS_TAG"
        const val SPLASH_PHOTO_NAME = "splash_photo.jpg"
        const val SPLASH_PHOTO_LOCATION = "splash_store_location"
        const val FAST = "com.mredrock.cyxbs.action.COURSE"
    }
}
