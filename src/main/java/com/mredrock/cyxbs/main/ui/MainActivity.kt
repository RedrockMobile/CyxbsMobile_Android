package com.mredrock.cyxbs.main.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.event.BottomSheetStateEvent
import com.mredrock.cyxbs.common.event.LoadCourse
import com.mredrock.cyxbs.common.event.NotifyBottomSheetToExpandEvent
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.getStatusBarHeight
import com.mredrock.cyxbs.common.utils.update.UpdateEvent
import com.mredrock.cyxbs.common.utils.update.UpdateUtils
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.utils.BottomNavigationViewHelper
import com.mredrock.cyxbs.main.viewmodel.MainViewModel
import com.umeng.message.inapp.InAppMessageManager
import kotlinx.android.synthetic.main.main_activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.dip
import org.jetbrains.anko.topPadding


@Route(path = MAIN_MAIN)
class MainActivity : BaseViewModelActivity<MainViewModel>() {

    override val viewModelClass = MainViewModel::class.java

    override val isFragmentActivity = true

    /**
     * 这个变量切记千万不能搬到viewModel,这个变量需要跟activity同生共死
     * 以保障activity异常重启时，这个值会被刷新，activity异常销毁viewModel仍在
     */
    private var isLoadCourse = true
    var lastState = BottomSheetBehavior.STATE_COLLAPSED


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private lateinit var navHelpers: BottomNavigationViewHelper
    private lateinit var preCheckedItem: MenuItem
    private var peeCheckedItemPosition = 0
    val menuIdList = listOf(R.id.explore, R.id.qa, R.id.mine)
    private val icons = arrayOf(
            R.drawable.main_ic_explore_unselected, R.drawable.main_ic_explore_selected,
            R.drawable.main_ic_qa_unselected, R.drawable.main_ic_qa_selected,
            R.drawable.main_ic_mine_unselected, R.drawable.main_ic_mine_selected
    )

    //四个需要组装的fragment(懒加载),为啥要加return@lazy呢，这里lint检查原因会爆黄不加也没关系
    private val courseFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { return@lazy ServiceManager.getService<Fragment>(COURSE_ENTRY) }
    private val qaFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { return@lazy ServiceManager.getService<Fragment>(QA_ENTRY) }
    private val mineFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { return@lazy ServiceManager.getService<Fragment>(MINE_ENTRY) }
    private val discoverFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { return@lazy ServiceManager.getService<Fragment>(DISCOVER_ENTRY) }

    //已经加载好的fragment
    private val showedFragments = mutableListOf<Fragment>()


    override var TAG: String = "MainHHHHH"
    override var isOpenLifeCycleLog: Boolean
        get() = true
        set(value) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_main)
        initBottomNavigationView()
        UpdateUtils.checkUpdate(this)
        InAppMessageManager.getInstance(BaseApp.context).showCardMessage(this,
                "课表主页面") {
            //友盟插屏消息关闭之后调用，暂未写功能
        }
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
                ll_nav_main_container.translationY = nav_main.height * slideOffset
                if (ll_nav_main_container.visibility == GONE) {
                    ll_nav_main_container.visibility = View.VISIBLE
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //如果是第一次进入展开则加载详细的课表子页
                if (isLoadCourse && bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
                        && lastState != BottomSheetBehavior.STATE_EXPANDED && !viewModel.courseShowState) {
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
                    menuIdList[0] -> changeFragment(discoverFragment, 0, menuItem)
                    menuIdList[1] -> changeFragment(qaFragment, 1, menuItem)
                    menuIdList[2] -> changeFragment(mineFragment, 2, menuItem)
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
        //防止activity由于异常重启导致旧的Fragment显示在新的fragment的下面
        val transition = supportFragmentManager.beginTransaction()
        supportFragmentManager.fragments.forEach { transition.detach(it) }
        transition.commit()

        viewModel.startPage.observe(this, Observer { starPage ->
            viewModel.initStartPage(starPage)
        })
        //下载Splash图
        viewModel.getStartPage()

        other_fragment_container.removeAllViews()
        //取得是否优先显示课表的设置
        viewModel.courseShowState = defaultSharedPreferences.getBoolean(COURSE_SHOW_STATE, false)
        lastState = if (viewModel.courseShowState) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        if (viewModel.courseShowState) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            //这里必须让它GONE，因为这个设置BottomSheet是不会走滑动监听的，否则导致完全显示BottomSheet后依然有下面的tab
            ll_nav_main_container.visibility = GONE
            //加载课表，并在滑动下拉课表容器中添加整个课表
            supportFragmentManager.beginTransaction().replace(R.id.course_bottom_sheet_content, courseFragment).apply {
                commit()
            }
        } else {
            //加载课表并给课表传递值，让它不要直接加载详细的课表，只用加载现在可见的头部就好
            courseFragment.arguments = Bundle().apply { putString(COURSE_DIRECT_LOAD, FALSE) }
            supportFragmentManager.beginTransaction().replace(R.id.course_bottom_sheet_content, courseFragment).apply {
                commit()
            }
        }
        //加载发现
        nav_main.selectedItemId = R.id.explore
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
