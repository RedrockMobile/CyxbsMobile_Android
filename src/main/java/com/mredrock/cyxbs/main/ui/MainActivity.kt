package com.mredrock.cyxbs.main.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.event.GoToDiscoverEvent
import com.mredrock.cyxbs.common.event.MainVPChangeEvent
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.update.UpdateEvent
import com.mredrock.cyxbs.common.utils.update.UpdateUtils
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.ui.adapter.MainVpAdapter
import com.mredrock.cyxbs.main.utils.BottomNavigationViewHelper
import com.umeng.message.inapp.InAppMessageManager
import kotlinx.android.synthetic.main.main_activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.dip

@Route(path = MAIN_MAIN)
class MainActivity : BaseActivity() {
    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    override val isFragmentActivity = true

    private lateinit var navHelpers: BottomNavigationViewHelper
    private lateinit var preCheckedItem: MenuItem
    private var peeCheckedItemPosition = 0
    private val icons = arrayOf(
            R.drawable.main_ic_course_unselected, R.drawable.main_ic_course_selected,
            R.drawable.main_ic_qa_unselected, R.drawable.main_ic_qa_selected,
            R.drawable.main_ic_explore_unselected, R.drawable.main_ic_explore_selected,
            R.drawable.main_ic_mine_unselected, R.drawable.main_ic_mine_selected
    )

    private lateinit var appbar: AppBarLayout

    private val fragments = ArrayList<Fragment>()
    private lateinit var adapter: MainVpAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_main)

        appbar = findViewById(R.id.app_bar_layout)

        common_toolbar.init(getString(R.string.common_course), listener = null)
        initBottomNavigationView()
        initFragments()

        fab.setOnClickListener {
            ARouter.getInstance().build(FRESHMAN_ENTRY).navigation()
        }

        UpdateUtils.checkUpdate(this)


        InAppMessageManager.getInstance(BaseApp.context).showCardMessage(this,
                "课表主页面") {
            //插屏消息关闭之后调用
        }
    }

    private fun initBottomNavigationView() {
        navHelpers = BottomNavigationViewHelper(nav_main).apply {
            //enableAnimation(false)
            //enableShiftMode(false)
            //enableItemShiftMode(false)
            setTextSize(11f)
            setIconSize(dip(21))
            setItemIconTintList(null)
            bindViewPager(view_pager) { position, menuItem ->
                preCheckedItem.setIcon(icons[peeCheckedItemPosition * 2])
                preCheckedItem = menuItem
                peeCheckedItemPosition = position
                menuItem.setIcon(icons[(position * 2) + 1])
                common_toolbar.title = menuItem.title

                menu?.clear()
                fragments[position].onPrepareOptionsMenu(menu)
                EventBus.getDefault().post(MainVPChangeEvent(position))
                appbar.setExpanded(true)
            }
        }
        nav_main.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        nav_main.menu.getItem(0).setIcon(icons[1])  //一定要放在上面的代码后面
        preCheckedItem = nav_main.menu.getItem(0)
        peeCheckedItemPosition = 0
    }

    private fun initFragments() {
        fragments.add(getFragment(COURSE_ENTRY))
        fragments.add(getFragment(QA_ENTRY))
        fragments.add(getFragment(DISCOVER_ENTRY))
        fragments.add(getFragment(MINE_ENTRY))
        adapter = MainVpAdapter(supportFragmentManager, fragments)
        view_pager.adapter = adapter
        view_pager.offscreenPageLimit = 4
    }

    private fun getFragment(path: String) = ARouter.getInstance().build(path).navigation() as Fragment


    // 为完成迎新专题中直接跳转"发现页"的需求添加的event
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun goToDiscover(event: GoToDiscoverEvent) {
        view_pager.currentItem = 2
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun installUpdate(event: UpdateEvent) {
        UpdateUtils.installApk(this, updateFile)
    }
}
