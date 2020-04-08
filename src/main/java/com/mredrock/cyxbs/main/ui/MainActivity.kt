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
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.getStatusBarHeight
import com.mredrock.cyxbs.common.utils.update.UpdateEvent
import com.mredrock.cyxbs.common.utils.update.UpdateUtils
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.utils.BottomNavigationHelper
import com.mredrock.cyxbs.main.utils.getSplashFile
import com.mredrock.cyxbs.main.utils.isDownloadSplash
import com.mredrock.cyxbs.main.viewmodel.MainViewModel
import com.umeng.message.inapp.InAppMessageManager
import kotlinx.android.synthetic.main.main_activity_main.*
import kotlinx.android.synthetic.main.main_view_stub_splash.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.topPadding


@Route(path = MAIN_MAIN)
class MainActivity : BaseViewModelActivity<MainViewModel>() {


    companion object {
        const val BOTTOM_SHEET_STATE = "BOTTOM_SHEET_STATE"
        const val NAV_SELECT = "NAV_SELECT"
        const val PAGE_CLASS_TAG = "PAGE_CLASS_TAG"
        const val SPLASH_PHOTO_NAME = "splash_photo.jpg"
        const val SPLASH_PHOTO_LOCATION = "splash_store_location"
    }

    override val viewModelClass = MainViewModel::class.java

    override val isFragmentActivity = true

    //放弃使用父类的登陆检查，自己做登陆检查操作
    override val loginConfig = LoginConfig(isCheckLogin = false)

    /**
     * 这个变量切记千万不能搬到viewModel,这个变量需要跟activity同生共死
     * 以保障activity异常重启时，这个值会被刷新，activity异常销毁重启viewModel仍在
     */
    private var isLoadCourse = true
    var lastState = BottomSheetBehavior.STATE_COLLAPSED


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private lateinit var navigationHelpers: BottomNavigationHelper

    private var peeCheckedItemPosition = 0


    //四个需要组装的fragment(懒加载)
    private val courseFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { getFragment(COURSE_ENTRY) }
    private val discoverFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { getFragment(DISCOVER_ENTRY) }
    private val qaFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { getFragment(QA_ENTRY) }
    private val mineFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { getFragment(MINE_ENTRY) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.MainActivityTheme)//恢复真正的主题，保证WindowBackground为主题色
        super.onCreate(savedInstanceState)
        @Suppress("UNCHECKED_CAST")//不加报警告，加了又不好看，check类型又说Hash<*,*>更好，但是又不让，太难了
        (savedInstanceState?.getSerializable(PAGE_CLASS_TAG) as? HashMap<String, Class<*>>)?.let {
            viewModel.mainPageLoadedFragmentClassList = it
        }
        setContentView(R.layout.main_activity_main)
        //检查闪屏页是否需要显示，闪屏页现在业务逻辑相对简单，为了减少启动时不必要的开销，暂时放到这里
        //后期，跳转逻辑复杂的话可以考虑将闪屏页独立成Activity
        checkSplash()
        //检查是否登陆，虽然BaseActivity里面也有登陆检查，
        //但是这里因为涉及到闪屏页的部分逻辑，不得不放弃BaseActivity的登陆检查
        checkIsLogin(LoginConfig(isWarnUser = false), this)
        initActivity()//Activity相关初始化
    }

    /**
     * 一些非重量级初始化操作
     */
    private fun initActivity() {
        InAppMessageManager.getInstance(BaseApp.context)
                .showCardMessage(this, "课表主页面") {
                    //友盟插屏消息关闭之后调用，暂未写功能
                }
        viewModel.startPage.observe(this, Observer { starPage ->
            viewModel.initStartPage(starPage)
        })
        UpdateUtils.checkUpdate(this)
        initBottom()
        initBottomSheetBehavior()
        initFragments()
    }


    private fun initBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(course_bottom_sheet_content)
        /**
         * 没有选择[android:fitsSystemWindows="true"]让系统预留一个状态栏高度
         * 而是手动加上一个状态栏高度的内边距，为了解决在BottomSheet正完全展开时
         * activity异常重启后fitsSystemWindows失效的问题
         */
        course_bottom_sheet_content.topPadding = course_bottom_sheet_content.topPadding + getStatusBarHeight()
        bottomSheetBehavior.peekHeight = bottomSheetBehavior.peekHeight + getStatusBarHeight()
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
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            moveTaskToBack(true)
        }
    }

    private fun initBottom() {
        navigationHelpers = BottomNavigationHelper(
                arrayOf(explore, qa, mine),
                arrayOf(
                        R.drawable.main_ic_explore_selected,
                        R.drawable.main_ic_qa_selected,
                        R.drawable.main_ic_mine_selected),
                arrayOf(
                        R.drawable.main_ic_explore_unselected,
                        R.drawable.main_ic_qa_unselected,
                        R.drawable.main_ic_mine_unselected))
        {
            when (it) {
                0 -> changeFragment(discoverFragment)
                1 -> {
                    if (peeCheckedItemPosition == 1) EventBus.getDefault().post(RefreshQaEvent())
                    changeFragment(qaFragment)
                }
                2 -> changeFragment(mineFragment)
            }
            peeCheckedItemPosition = it
        }
    }

    /**
     * 进行显示页面的切换
     * @param fragment 用来切换的fragment
     * @param position 切换到的tab的索引 从0开始
     * @param menuItem nav中对应的Item
     */
    private fun changeFragment(fragment: Fragment) {
        val transition = supportFragmentManager.beginTransaction()
        //遍历隐藏已经加载的fragment
        viewModel.mainPageLoadedFragmentClassList.filter { COURSE_ENTRY != it.key }.forEach {
            val fragment1 = supportFragmentManager.fragments.entryContains(it.key)
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


    private fun checkSplash() {
        //判断是否下载了Splash图，下载了就直接设置
        if (isDownloadSplash(this@MainActivity)) {
            val inflate = main_activity_splash_viewStub.inflate()
            Glide.with(this)
                    .load(getSplashFile(this@MainActivity))
                    .apply(RequestOptions()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE))
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


    private fun initFragments() {
        //取得是否优先显示课表的设置
        viewModel.isCourseDirectShow = defaultSharedPreferences.getBoolean(COURSE_SHOW_STATE, false)
        val isShortcut = intent.action == "com.mredrock.cyxbs.action.COURSE"
        lastState = if (viewModel.isCourseDirectShow) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        if (viewModel.isCourseDirectShow || isShortcut) {
            intent.action = ""
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            ll_nav_main_container.translationY = 10000f
            courseFragment.arguments = Bundle().apply { putString(COURSE_DIRECT_LOAD, TRUE) }
            //加载课表，并在滑动下拉课表容器中添加整个课表
            supportFragmentManager.beginTransaction().apply {
                if (supportFragmentManager.fragments.entryContains(COURSE_ENTRY) == null) {
                    add(R.id.course_bottom_sheet_content, courseFragment)
                }
                show(courseFragment)
                commit()
            }
            navigationHelpers.selectTab(0)
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            //加载课表并给课表传递值，让它不要直接加载详细的课表，只用加载现在可见的头部就好
            courseFragment.arguments = Bundle().apply { putString(COURSE_DIRECT_LOAD, FALSE) }
            supportFragmentManager.beginTransaction().apply {
                if (supportFragmentManager.fragments.entryContains(COURSE_ENTRY) == null) {
                    add(R.id.course_bottom_sheet_content, courseFragment)
                }
                show(courseFragment)
                commit()
            }
            //加载发现
            navigationHelpers.selectTab(0)
        }
    }

    //根据aRouterPath来查询是否已经加载当前Fragment，以此来增强app在Activity异常重启时的稳定性
    private fun List<Fragment>.entryContains(aRouterPath: String): Fragment? {
        val list = filter { it::class.java === viewModel.mainPageLoadedFragmentClassList[aRouterPath] }
        return if (list.isEmpty()) null else list[0]
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BOTTOM_SHEET_STATE, bottomSheetBehavior.state)
        outState.putInt(NAV_SELECT, peeCheckedItemPosition)
        outState.putSerializable(PAGE_CLASS_TAG, viewModel.mainPageLoadedFragmentClassList)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        navigationHelpers.selectTab(savedInstanceState.getInt(NAV_SELECT))
        if (savedInstanceState.getInt(BOTTOM_SHEET_STATE) == BottomSheetBehavior.STATE_EXPANDED && !viewModel.isCourseDirectShow) {
            bottomSheetBehavior.state = savedInstanceState.getInt(BOTTOM_SHEET_STATE)
            EventBus.getDefault().post(LoadCourse(false))
            EventBus.getDefault().post(BottomSheetStateEvent(1f))
            ll_nav_main_container.translationY = 1000f
            isLoadCourse = false
        } else if (savedInstanceState.getInt(BOTTOM_SHEET_STATE) == BottomSheetBehavior.STATE_COLLAPSED && viewModel.isCourseDirectShow) {
            bottomSheetBehavior.state = savedInstanceState.getInt(BOTTOM_SHEET_STATE)
            EventBus.getDefault().post(BottomSheetStateEvent(0f))
        }
    }

    private fun getFragment(data: String) =
            supportFragmentManager.fragments.entryContains(data)
                    ?: (ServiceManager.getService(data) as Fragment).apply {
                        viewModel.mainPageLoadedFragmentClassList[data] = this.javaClass
                    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun installUpdate(event: UpdateEvent) {
        UpdateUtils.installApk(this, updateFile)
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
}
