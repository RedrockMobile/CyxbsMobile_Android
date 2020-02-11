package com.mredrock.cyxbs.main.ui

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.event.*
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.common.utils.update.UpdateEvent
import com.mredrock.cyxbs.common.utils.update.UpdateUtils
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.bean.FinishEvent
import com.mredrock.cyxbs.main.utils.*
import com.mredrock.cyxbs.main.viewmodel.MainViewModel
import com.umeng.message.inapp.InAppMessageManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.dip

@Route(path = MAIN_MAIN)
class MainActivity : BaseViewModelActivity<MainViewModel>() {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    override val viewModelClass = MainViewModel::class.java

    override val isFragmentActivity = true

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private lateinit var navHelpers: BottomNavigationViewHelper
    private lateinit var preCheckedItem: MenuItem
    private var peeCheckedItemPosition = 0
    private val icons = arrayOf(
            R.drawable.main_ic_explore_unselected, R.drawable.main_ic_explore_selected,
            R.drawable.main_ic_qa_unselected, R.drawable.main_ic_qa_selected,
            R.drawable.main_ic_mine_unselected, R.drawable.main_ic_mine_selected
    )

    //四个需要组装的fragment(懒加载)
    private val courseFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { getFragment(COURSE_ENTRY) }
    private val qaFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { getFragment(QA_ENTRY) }
    private val mineFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { getFragment(MINE_ENTRY) }
    private val discoverFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { getFragment(DISCOVER_ENTRY) }

    //已经加载好的fragment
    private val showedFragments = mutableListOf<Fragment>()

    //进入app是否直接显示课表
    var courseShowState: Boolean = false

    private val accountService: IAccountService by lazy (LazyThreadSafetyMode.NONE) {
        ServiceManager.getService(IAccountService::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_main)

        // TODO: 2019/11/19 此处待处理，这里只能适配部分机型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(this, R.color.windowBackground)
        }

        initBottomNavigationView()
        UpdateUtils.checkUpdate(this)
        InAppMessageManager.getInstance(BaseApp.context).showCardMessage(this,
                "课表主页面") {
            //插屏消息关闭之后调用，暂未写功能
        }
        viewModel.startPage.observe { starPage ->
            if (starPage != null) {
                val src = starPage.photo_src

                if (src != null && src.startsWith("http")) {//如果不为空，且url有效
                    //对比缓存的url是否一样
                    if (src != applicationContext.sharedPreferences("splash").getString(SplashActivity.SPLASH_PHOTO_NAME, "#")) {
                        deleteDir(getSplashFile(this@MainActivity))
                        downloadSplash(src, this@MainActivity)
                        applicationContext.sharedPreferences("splash").editor {
                            putString(SplashActivity.SPLASH_PHOTO_NAME, src)
                        }
                    }
                } else { //src非法
                    deleteSplash()
                }
            } else { //不显示图片的时候
                deleteSplash()
            }
        }
        //下载Splash图
        viewModel.getStartPage()

        bottomSheetBehavior = BottomSheetBehavior.from(course_bottom_sheet_content)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            var statePosition = 0f
            var isFirst = true
            var lastState = if (courseShowState) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
            override fun onSlide(p0: View, p1: Float) {
                ll_nav_main_container.translationY = nav_main.height * p1
                statePosition = p1
                EventBus.getDefault().post(BottomSheetStateEvent(p1))
                if (ll_nav_main_container.visibility == GONE) {
                    ll_nav_main_container.visibility = VISIBLE
                }
                if (isFirst && courseShowState && p1<0.5f) {
                    isFirst = false
                    Observable.create<Fragment> {
                        it.onNext(discoverFragment)
                    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
                        other_fragment_container.visibility = GONE
                        changeFragment(discoverFragment, 0, nav_main.menu[0])
                        TransitionManager.beginDelayedTransition(main_content, TransitionSet().apply {
                            addTransition(Slide().apply {
                                duration = 500
                            })
                        })
                        other_fragment_container.visibility = VISIBLE
                    }.isDisposed
                }
            }

            override fun onStateChanged(p0: View, p1: Int) {

                if (isFirst && bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED&&lastState!=BottomSheetBehavior.STATE_EXPANDED) {
                    EventBus.getDefault().post(LoadCourse())
                    if (accountService.getVerifyService().isLogin()){
                        isFirst = false
                    }
                }
                lastState = when(p1){
                    BottomSheetBehavior.STATE_EXPANDED-> BottomSheetBehavior.STATE_EXPANDED
                    BottomSheetBehavior.STATE_COLLAPSED-> {
                        EventBus.getDefault().post(LoadCourse(true))
                        BottomSheetBehavior.STATE_COLLAPSED}
                    else -> lastState
                }
            }
        })
        initFragments()
    }

    private fun checkLoginBeforeAction(msg: String, action: () -> Unit) {
        if (ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
            action.invoke()
        } else {
            EventBus.getDefault().post(AskLoginEvent("请先登陆才能查看${msg}哦~"))
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAndroidNativeLightStatusBar(dark: Boolean) {
        val decor = window.decorView
        if (dark) {
            decor.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            decor.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            moveTaskToBack(true)
        }
    }

    private fun deleteSplash() {
        if (isDownloadSplash(this@MainActivity)) {//如果url为空，则删除之前下载的图片
            deleteDir(getSplashFile(this@MainActivity))
        }
    }

    private fun initBottomNavigationView() {
        navHelpers = BottomNavigationViewHelper(nav_main).apply {
            setTextSize(11f)
            setIconSize(dip(21))
            setItemIconTintList(null)
            nav_main.setOnNavigationItemSelectedListener { menuItem ->
                preCheckedItem.setIcon(icons[peeCheckedItemPosition * 2])
                preCheckedItem = menuItem
                menu?.clear()
                when (menuItem.itemId) {
                    R.id.explore -> {
                        changeFragment(discoverFragment, 0, menuItem)
                    }
                    R.id.qa -> {
                        changeFragment(qaFragment, 1, menuItem)
                    }
                    R.id.mine -> {
                        changeFragment(mineFragment, 2, menuItem)
                    }
                }
                return@setOnNavigationItemSelectedListener true
            }
        }

        nav_main.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        nav_main.menu.getItem(0).setIcon(icons[1])  //一定要放在上面的代码后面
        preCheckedItem = nav_main.menu.getItem(0)
        peeCheckedItemPosition = 0
    }

    /**
     * 进行显示页面的切换
     * @param fragment 用来切换的fragment
     * @param position 切换到的tab的索引 从0开始
     * @param menuItem nav中对应的Item
     */
    private fun changeFragment(fragment: Fragment, position: Int, menuItem: MenuItem) {
        val transition = supportFragmentManager.beginTransaction()
        //遍历隐藏已经加载的fragment
        showedFragments.forEach {
            transition.hide(it)
        }
        //如果这个fragment从来没有加载过，则进行添加
        if (!showedFragments.contains(fragment)) {
            showedFragments.add(fragment)
            transition.add(R.id.other_fragment_container, fragment)
        }
        transition.show(fragment)
        transition.commit()
        //对tab进行变化
        peeCheckedItemPosition = position
        menuItem.setIcon(icons[(position * 2) + 1])
    }

    private fun initFragments() {

        courseShowState = defaultSharedPreferences.getBoolean(COURSE_SHOW_STATE, false)
        if (courseShowState) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            ll_nav_main_container.visibility = GONE
            //在滑动下拉课表容器中添加整个课表
            supportFragmentManager.beginTransaction().replace(R.id.course_bottom_sheet_content, courseFragment).apply {
                commit()
            }
        } else {
            changeFragment(discoverFragment, 0, nav_main.menu[0])
            courseFragment.arguments = Bundle().apply {
                putString(COURSE_DIRECT_LOAD, FALSE)
            }
            supportFragmentManager.beginTransaction().replace(R.id.course_bottom_sheet_content, courseFragment).apply {
                commit()
            }
        }
    }

    private fun getFragment(path: String) = ARouter.getInstance().build(path).navigation() as Fragment

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun installUpdate(event: UpdateEvent) {
        UpdateUtils.installApk(this, updateFile)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(FinishEvent())
    }

    /**
     *
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun acceptCourseSlideInformation(event: CourseSlipsTopEvent) {
        viewModel.isCourseTop = event.isTop
    }

    /**
     * 接收bottomSheet的点击事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun acceptNotifyBottomSheetToExpandEvent(notifyBottomSheetToExpandEvent: NotifyBottomSheetToExpandEvent) {
        if (notifyBottomSheetToExpandEvent.isToExpand) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}
