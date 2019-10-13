package com.mredrock.cyxbs.main.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.common.utils.update.UpdateEvent
import com.mredrock.cyxbs.common.utils.update.UpdateUtils
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.ui.adapter.MainVpAdapter
import com.mredrock.cyxbs.main.utils.*
import com.mredrock.cyxbs.main.viewmodel.MainViewModel
import com.umeng.message.inapp.InAppMessageManager
import kotlinx.android.synthetic.main.main_activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.dip
import org.jetbrains.anko.toast
import java.lang.IndexOutOfBoundsException

@Route(path = MAIN_MAIN)
class MainActivity : BaseViewModelActivity<MainViewModel>() {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    override val viewModelClass = MainViewModel::class.java

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

    private val loadHandler: Handler = Handler()
    private val loadRunnable = Runnable {
        fragments.add(getFragment(QA_ENTRY))
        fragments.add(getFragment(DISCOVER_ENTRY))
        fragments.add(getFragment(MINE_ENTRY))
        adapter.notifyDataSetChanged()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_main)
        appbar = findViewById(R.id.app_bar_layout)

        common_toolbar.init(getString(R.string.common_course), listener = null)
        initBottomNavigationView()
        initFragments()

        UpdateUtils.checkUpdate(this)

        fab.setOnClickListener {
            ARouter.getInstance().build(REDROCK_HOME_ENTRY).navigation()
        }

        InAppMessageManager.getInstance(BaseApp.context).showCardMessage(this,
                "课表主页面") {
            //插屏消息关闭之后调用
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
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        moveTaskToBack(true)
    }

    private fun deleteSplash() {
        if (isDownloadSplash(this@MainActivity)) {//如果url为空，则删除之前下载的图片
            deleteDir(getSplashFile(this@MainActivity))
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

                try {//防止用户点击过快，IdleHandler还未触发，未懒加载完成fragment
                    fragments[position].onPrepareOptionsMenu(menu)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }

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
        //只添加课表的fragment，一次性加入四个fragment又不lazy load的话打开改Activity的时间很长。
        fragments.add(getFragment(COURSE_ENTRY))
        adapter = MainVpAdapter(supportFragmentManager, fragments)
        view_pager.adapter = adapter
        view_pager.offscreenPageLimit = 4

        window.decorView.post {
            loadHandler.post(loadRunnable)
        }
    }

    private fun getFragment(path: String) = ARouter.getInstance().build(path).navigation() as Fragment

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun installUpdate(event: UpdateEvent) {
        UpdateUtils.installApk(this, updateFile)
    }
}
