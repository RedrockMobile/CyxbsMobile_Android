package com.mredrock.cyxbs.main.ui

import android.content.res.Configuration
import android.os.Build
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
import com.mredrock.cyxbs.common.BaseApp.Companion.isNightMode
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.event.LoadCourse
import com.mredrock.cyxbs.common.event.NotifyBottomSheetToExpandEvent
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.getDarkModeStatus
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


@Route(path = MAIN_MAIN)
class MainActivity : BaseViewModelActivity<MainViewModel>() {

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

    //四个需要组装的fragment(懒加载),为啥要加return@lazy呢，这里lint检查原因会爆黄不加也没关系
    private val courseFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { return@lazy ServiceManager.getService<Fragment>(COURSE_ENTRY) }
    private val qaFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { return@lazy ServiceManager.getService<Fragment>(QA_ENTRY) }
    private val mineFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { return@lazy ServiceManager.getService<Fragment>(MINE_ENTRY) }
    private val discoverFragment: Fragment by lazy(LazyThreadSafetyMode.NONE) { return@lazy ServiceManager.getService<Fragment>(DISCOVER_ENTRY) }

    //已经加载好的fragment
    private val showedFragments = mutableListOf<Fragment>()


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
        //这里因为BottomSheet和[android:fitsSystemWindows="true"]冲突要加上一个状态栏高度
        bottomSheetBehavior.peekHeight = bottomSheetBehavior.peekHeight + getStatusBarHeight()
        viewModel.bottomSheetCallbackBind(bottomSheetBehavior,
                onSlide = { _, fl ->
                    ll_nav_main_container.translationY = nav_main.height * fl
                    if (ll_nav_main_container.visibility == GONE) {
                        ll_nav_main_container.visibility = View.VISIBLE
                    }
                },
                onStateChanged = { _, _ ->
                    //如果是第一次进入展开则加载详细的课表子页
                    if (viewModel.isFirst && bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
                            && viewModel.lastState != BottomSheetBehavior.STATE_EXPANDED && !viewModel.courseShowState) {
                        EventBus.getDefault().post(LoadCourse())
                        viewModel.isFirst = false
                    }
                })
    }


////这个方法可能有用暂不删除
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun setAndroidNativeLightStatusBar(dark: Boolean) {
//        val decor = window.decorView
//        if (dark) {
//            decor.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        } else {
//            decor.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE
//        }
//    }

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
                    R.id.explore -> changeFragment(discoverFragment, 0, menuItem)
                    R.id.qa -> changeFragment(qaFragment, 1, menuItem)
                    R.id.mine -> changeFragment(mineFragment, 2, menuItem)
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
        viewModel.startPage.observe(this, Observer { starPage ->
            viewModel.initStartPage(starPage)
        })
        //下载Splash图
        viewModel.getStartPage()

        other_fragment_container.removeAllViews()
        //取得是否优先显示课表的设置
        viewModel.courseShowState = defaultSharedPreferences.getBoolean(COURSE_SHOW_STATE, false)
        viewModel.lastState = if (viewModel.courseShowState) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        if (viewModel.courseShowState) {
            nav_main.selectedItemId = R.id.explore
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            //这里必须让它GONE，因为这个设置BottomSheet是不会走滑动监听的，否则导致完全显示BottomSheet后依然有下面的tab
            ll_nav_main_container.visibility = GONE
            //加载课表，并在滑动下拉课表容器中添加整个课表
            supportFragmentManager.beginTransaction().replace(R.id.course_bottom_sheet_content, courseFragment).apply {
                commit()
            }
        } else {
            //如果用户没有选择优先显示课表，则加载发现
            nav_main.selectedItemId = R.id.explore
            //加载课表并给课表传递值，让它不要直接加载详细的课表，只用加载现在可见的头部就好
            courseFragment.arguments = Bundle().apply { putString(COURSE_DIRECT_LOAD, FALSE) }
            supportFragmentManager.beginTransaction().replace(R.id.course_bottom_sheet_content, courseFragment).apply {
                commit()
            }
        }
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
