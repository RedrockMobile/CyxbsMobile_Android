package com.mredrock.cyxbs.common.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.bean.LoginConfig
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.component.JToolbar
import com.mredrock.cyxbs.common.config.ACTIVITY_CLASS
import com.mredrock.cyxbs.common.config.MAIN_LOGIN
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.getDarkModeStatus
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.common_toolbar.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity

/**
 * Created By jay68 on 2018/8/9.
 */
abstract class BaseActivity : AppCompatActivity() {

    /**
     * 这里可以开启生命周期的Log，你可以重写这个值并给值为true，
     * 也可以直接赋值为true（赋值的话请在init{}里面赋值或者在onCreate的super.onCreate(savedInstanceState)调用之前赋值）
     */
    protected open var isOpenLifeCycleLog = false

    //当然，你要定义自己的TAG方便在Log里面找也可以重写这个
    protected open var TAG: String = this::class.java.simpleName

    /**
     * service for umeng
     * set true if this activity consist of fragments.
     */
    abstract val isFragmentActivity: Boolean

    protected open val loginConfig = LoginConfig()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 禁用横屏，现目前不需要横屏，防止发送一些错误
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        initFlag()
        lifeCycleLog("onCreate")
    }

    private fun initFlag() {

        when {
            Build.VERSION.SDK_INT >= 23 -> {
                if (this.getDarkModeStatus()) {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                } else {
                    window.decorView.systemUiVisibility =
                            //亮色模式状态栏
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                                    //设置decorView的布局设置为全屏
                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                    //维持布局稳定，不会因为statusBar和虚拟按键的消失而移动view位置
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }
            }
            Build.VERSION.SDK_INT >= 21 -> {
                //设置decorView的布局设置为全屏，并维持布局稳定
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                window.statusBarColor = Color.TRANSPARENT
            }
        }
    }

    inline fun <reified T : Activity> startActivity(finish: Boolean = false, vararg params: Pair<String, Any?>) {
        if (finish) finish()
        startActivity<T>(*params)
    }

    val common_toolbar get() = toolbar

    var menu: Menu? = null
        private set

    @Deprecated(message = "废弃，请使用initWithSplitLine()", replaceWith = ReplaceWith("JToolbar.initWithSplitLine()", "com.mredrock.cyxbs.common.ui"))
    protected fun JToolbar.init(title: String,
                                @DrawableRes icon: Int = R.drawable.common_ic_back,
                                listener: View.OnClickListener? = View.OnClickListener { finish() }) {
        withSplitLine(true)
        initInternal(title, icon, listener)
    }

    private fun JToolbar.initInternal(title: String,
                                      @DrawableRes icon: Int = R.drawable.common_ic_back,
                                      listener: View.OnClickListener? = View.OnClickListener { finish() },
                                      titleOnLeft: Boolean = true) {
        this.title = title
        setSupportActionBar(this)
        setTitleLocationAtLeft(titleOnLeft)
        if (listener == null) {
            navigationIcon = null
        } else {
            setNavigationIcon(icon)
            setNavigationOnClickListener(listener)
        }
    }

    protected fun JToolbar.initWithSplitLine(title: String,
                                             withSplitLine: Boolean = true,
                                             @DrawableRes icon: Int = R.drawable.common_ic_back,
                                             listener: View.OnClickListener? = View.OnClickListener { finish() },
                                             titleOnLeft: Boolean = true) {
        setTitleLocationAtLeft(false)
        withSplitLine(withSplitLine)
        initInternal(title, icon, listener, titleOnLeft)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val r = super.onPrepareOptionsMenu(menu)
        this.menu = menu
        return r
    }

    override fun onRestart() {
        super.onRestart()
        lifeCycleLog("onRestart")
    }

    override fun onStart() {
        super.onStart()
        if (this is EventBusLifecycleSubscriber) EventBus.getDefault().register(this)
        lifeCycleLog("onStart")
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
        if (!isFragmentActivity) {
            MobclickAgent.onPageStart(javaClass.name)
            LogUtils.d("UMStat", javaClass.name + " started")
        }
        lifeCycleLog("onResume")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
        if (!isFragmentActivity) {
            MobclickAgent.onPageEnd(javaClass.name)
            LogUtils.d("UMStat", javaClass.name + " paused")
        }
        lifeCycleLog("onPause")
    }

    override fun onStop() {
        super.onStop()
        if (this is EventBusLifecycleSubscriber && EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
        lifeCycleLog("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        lifeCycleLog("onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lifeCycleLog("onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        lifeCycleLog("onRestoreInstanceState")
    }

    fun lifeCycleLog(message: String) {
        if (isOpenLifeCycleLog) {
            LogUtils.d(TAG, "${this::class.java.simpleName}\$\$${message}")
        }
    }

    /**
     * 检查是否登录，因为单模块调试，需主动调用
     */
    protected fun checkIsLogin(loginConfig: LoginConfig, activity: Activity) {
        if (!ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin() && loginConfig.isCheckLogin) {
            //取消转场动画
            val postcard = ARouter.getInstance().build(MAIN_LOGIN).withTransition(0, 0)
            //如果设置了重新启动activity，则传Class过去，并关闭当前Activity
            if (loginConfig.isFinish) {
                postcard.withSerializable(ACTIVITY_CLASS, this::class.java)
                activity.finish()
            }
            //如果需要提示用户未登录
            if (loginConfig.isWarnUser) {
                CyxbsToast.makeText(activity, loginConfig.warnMessage, Toast.LENGTH_SHORT).show()
            }
            //正式开始路由
            postcard.navigation(activity)
        }
    }

    //检查refreshToken是否过期，用户是否太久未使用
    // 以我的思路，只会出现在MainActivity
    // 但是还是放在BaseActivity，以备其他情况
    protected fun checkUserInfoExpired(loginConfig: LoginConfig, activity: Activity) {
        //refreshToken过期，那么请退出重新登录
        val userState = ServiceManager.getService(IAccountService::class.java).getVerifyService()
        if (userState.isLogin() && userState.isRefreshTokenExpired()) {
            //退出登录
            CyxbsToast.makeText(activity, "身份信息已过期，请重新登录", Toast.LENGTH_LONG).show()
            userState.logout(this)
            //取消转场动画
            val postcard = ARouter.getInstance().build(MAIN_LOGIN).withTransition(0, 0)
            //如果设置了重新启动activity，则传Class过去，并关闭当前Activity
            if (loginConfig.isFinish) {
                postcard.withSerializable(ACTIVITY_CLASS, this::class.java)
                activity.finish()
            }
            //正式开始路由
            postcard.navigation(activity)
        }
    }
}
