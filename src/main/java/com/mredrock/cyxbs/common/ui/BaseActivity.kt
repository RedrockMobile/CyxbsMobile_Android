package com.mredrock.cyxbs.common.ui

//import com.jude.swipbackhelper.SwipeBackHelper
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import android.view.WindowManager
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.event.LoginEvent
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.utils.LogUtils
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.common_toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity

/**
 * Created By jay68 on 2018/8/9.
 */
abstract class BaseActivity : AppCompatActivity() {
    companion object {
        @JvmField
        val TAG: String = BaseActivity::class.java.simpleName
    }

    /**
     * service for umeng
     * set true if this activity consist of fragments.
     */
    abstract val isFragmentActivity: Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // todo 8.0 系统Bug 窗口透明导致限制竖屏闪退
//        SwipeBackHelper.onCreate(this)
//        SwipeBackHelper.getCurrentPage(this).setSwipeRelateEnable(true)

        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
//            window.navigationBarColor = Color.TRANSPARENT
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= 19) {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )
        }

        LogUtils.v(TAG, javaClass.name)
    }

    inline fun <reified T : Activity> startActivity(finish: Boolean = false, vararg params: Pair<String, Any?>) {
        if (finish) finish()
        startActivity<T>(*params)
    }

    val common_toolbar get() = toolbar
    var menu: Menu? = null
        private set

    protected fun Toolbar.init(title: String,
                               @DrawableRes icon: Int = R.drawable.common_ic_back,
                               listener: View.OnClickListener? = View.OnClickListener { finish() }) {
        this.title = title
        setSupportActionBar(this)
        if (listener == null) {
            navigationIcon = null
        } else {
            setNavigationIcon(icon)
            setNavigationOnClickListener(listener)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onLoginEvent(event: LoginEvent) {
        ARouter.getInstance().build("/main/login").navigation()
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    open fun onAskLoginEvent(event: AskLoginEvent) {
        val handler = Handler(mainLooper)
        handler.post {
            MaterialDialog.Builder(this)
                    .title("是否登录?")
                    .content(event.msg)
                    .positiveText("马上去登录")
                    .negativeText("我再看看")
                    .callback(object : MaterialDialog.ButtonCallback() {
                        override fun onPositive(dialog: MaterialDialog?) {
                            super.onPositive(dialog)
                            onLoginEvent(LoginEvent())
                        }

                        override fun onNegative(dialog: MaterialDialog?) {
                            super.onNegative(dialog)
                            dialog!!.dismiss()
                        }
                    }).show()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val r = super.onPrepareOptionsMenu(menu)
        this.menu = menu
        return r
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onLoginStateChangeEvent(event: LoginStateChangeEvent) {
        LogUtils.d("LoginStateChangeEvent", "in" + localClassName + "login state: " + event.newState + "")
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
        if (!isFragmentActivity) {
            MobclickAgent.onPageStart(javaClass.name)
            LogUtils.d("UMStat", javaClass.name + " started")
        }
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
        if (!isFragmentActivity) {
            MobclickAgent.onPageEnd(javaClass.name)
            LogUtils.d("UMStat", javaClass.name + " paused")
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

}
