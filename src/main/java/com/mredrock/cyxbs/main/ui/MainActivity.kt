package com.mredrock.cyxbs.main.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.MenuItem
import com.alibaba.android.arouter.launcher.ARouter
import com.jude.swipbackhelper.SwipeBackHelper
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.ui.adapter.MainVpAdapter
import com.mredrock.cyxbs.main.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.main_activity_main.*
import org.jetbrains.anko.dip

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

    private val fragments = ArrayList<Fragment>()
    private lateinit var adapter: MainVpAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_main)
        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false)

        common_toolbar.init(getString(R.string.common_course), listener = null)
        initBottomNavigationView()
        initFragments()

        fab.setOnClickListener {
            //todo center button
            ARouter.getInstance().build(REDROCK_HOME_ENTRY).navigation()
        }
    }

    private fun initBottomNavigationView() {
        navHelpers = BottomNavigationViewHelper(nav_main).apply {
            enableAnimation(false)
            enableShiftMode(false)
            enableItemShiftMode(false)
            setTextSize(11f)
            setIconSize(dip(21))
            setItemIconTintList(null)
            bindViewPager(view_pager) { position, menuItem ->
                preCheckedItem.setIcon(icons[peeCheckedItemPosition * 2])
                preCheckedItem = menuItem
                peeCheckedItemPosition = position
                menuItem.setIcon(icons[(position * 2) + 1])
                common_toolbar.title = menuItem.title
            }
        }
        nav_main.menu.getItem(0).setIcon(icons[1])  //一定要放在上面的代码后面
        preCheckedItem = nav_main.menu.getItem(0)
        peeCheckedItemPosition = 0
    }

    private fun initFragments() {
        //todo get fragments
        fragments.add(getFragment(COURSE_ENTRY))
        fragments.add(getFragment(QA_ENTRY))
        fragments.add(getFragment(DISCOVER_ENTRY))
        fragments.add(getFragment(MINE_ENTRY))
        adapter = MainVpAdapter(supportFragmentManager, fragments)
        view_pager.adapter = adapter
        view_pager.offscreenPageLimit = 4
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                menu?.clear()
                fragments[position].onPrepareOptionsMenu(menu)
            }
        })
    }

    private fun getFragment(path: String) = ARouter.getInstance().build(path).navigation() as Fragment

    override fun onLoginStateChangeEvent(event: LoginStateChangeEvent) {
        super.onLoginStateChangeEvent(event)
        //todo change fragment course
    }
}
