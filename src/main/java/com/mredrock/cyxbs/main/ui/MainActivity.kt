package com.mredrock.cyxbs.main.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.common.utils.update.UpdateEvent
import com.mredrock.cyxbs.common.utils.update.UpdateUtils
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.bean.FinishEvent
import com.mredrock.cyxbs.main.ui.adapter.MainVpAdapter
import com.mredrock.cyxbs.main.utils.*
import com.mredrock.cyxbs.main.viewmodel.MainViewModel
import com.umeng.message.inapp.InAppMessageManager
import kotlinx.android.synthetic.main.main_activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.dip
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import androidx.annotation.RequiresApi
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.event.CourseSlipsTopEvent

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
            R.drawable.main_ic_explore_unselected, R.drawable.main_ic_explore_selected,
            R.drawable.main_ic_qa_unselected, R.drawable.main_ic_qa_selected,
            R.drawable.main_ic_mine_unselected, R.drawable.main_ic_mine_selected
    )


    private val fragments = ArrayList<Fragment>()
    private lateinit var adapter: MainVpAdapter

    private val loadHandler: Handler = Handler()
    private val loadRunnable = Runnable {
        fragments.add(getFragment(QA_ENTRY))
        fragments.add(getFragment(MINE_ENTRY))
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_main)

        //这块先展示效果，后面还会改,因为只能改部分机型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(this, R.color.windowBackground)
        }

        initBottomNavigationView()
        initFragments()
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

        viewModel.initBottomSheetBehavior(this)
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
        moveTaskToBack(true)
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
            bindViewPager(view_pager) { position, menuItem ->
                preCheckedItem.setIcon(icons[peeCheckedItemPosition * 2])
                preCheckedItem = menuItem
                peeCheckedItemPosition = position
                menuItem.setIcon(icons[(position * 2) + 1])
                menu?.clear()
            }
        }
        nav_main.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        nav_main.menu.getItem(0).setIcon(icons[1])  //一定要放在上面的代码后面
        preCheckedItem = nav_main.menu.getItem(0)
        peeCheckedItemPosition = 0
    }

    private fun initFragments() {
        fragments.add(getFragment(DISCOVER_ENTRY))

        //在滑动下拉课表容器中添加整个课表
        supportFragmentManager.beginTransaction().replace(R.id.course_bottom_sheet_content, getFragment(COURSE_ENTRY)).apply {
            commit()
        }

        adapter = MainVpAdapter(supportFragmentManager, fragments)
        view_pager.adapter = adapter
        view_pager.offscreenPageLimit = 3

        window.decorView.postDelayed({
            loadHandler.post(loadRunnable)
        },200)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun acceptCourseSlideInformation(evet: CourseSlipsTopEvent) {
        viewModel.isCourseTop = evet.isTop
    }
}
