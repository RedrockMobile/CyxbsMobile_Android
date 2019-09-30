package com.mredrock.cyxbs.main.ui

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.event.GoToDiscoverEvent
import com.mredrock.cyxbs.common.event.MainVPChangeEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.common.utils.update.UpdateEvent
import com.mredrock.cyxbs.common.utils.update.UpdateUtils
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.ui.adapter.MainVpAdapter
import com.mredrock.cyxbs.main.utils.*
import com.mredrock.cyxbs.main.viewmodel.MainViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import com.umeng.message.inapp.InAppMessageManager
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.main_activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.dip

@Route(path = MAIN_MAIN)
class MainActivity : BaseViewModelActivity<MainViewModel>() {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
        const val HAS_PERMISSION = "hasPermission"
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
    private lateinit var disposable: Disposable
    private var hasPermission: Boolean = false

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

        hasPermission = applicationContext.sharedPreferences("splash").getBoolean(HAS_PERMISSION, false)

        //下载Splash图
        viewModel.getStartPage()
        disposable = RxPermissions(this)
                .request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    hasPermission = it
                    viewModel.getStartPage()
                    applicationContext.sharedPreferences("splash").editor {
                        putBoolean(HAS_PERMISSION, true)
                    }
                }

        viewModel.startPage.observe(this, Observer { starPage ->
            if (starPage != null && hasPermission) {
                val src = starPage.photo_src

                if (src != null && src.startsWith("http")) {//如果不为空，且url有效
                    //对比缓存的url是否一样
                    if (src != applicationContext.sharedPreferences("splash").getString(SplashActivity.SPLASH_PHOTO_NAME, "#")) {
                        deleteDir(getSplashFile())
                        downloadSplash(src)
                        applicationContext.sharedPreferences("splash").editor {
                            putString(SplashActivity.SPLASH_PHOTO_NAME,src)
                        }
                    }

                } else {
                    if (isDownloadSplash()) {//如果url为空，则删除之前下载的图片
                        deleteDir(getSplashFile())
                    }
                }
            }
        })
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
        adapter = MainVpAdapter(supportFragmentManager, fragments)
        view_pager.adapter = adapter
        view_pager.offscreenPageLimit = 4

        Looper.myQueue().addIdleHandler {
            fragments.add(getFragment(QA_ENTRY))
            fragments.add(getFragment(DISCOVER_ENTRY))
            fragments.add(getFragment(MINE_ENTRY))
            adapter.notifyDataSetChanged()
            false
        }
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

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
