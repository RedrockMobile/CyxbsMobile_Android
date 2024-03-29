package com.mredrock.cyxbs.common.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.mark.ActionLoginStatusSubscriber
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.getDarkModeStatus
import com.mredrock.cyxbs.config.view.JToolbar
import com.mredrock.cyxbs.lib.utils.utils.BindView
import org.greenrobot.eventbus.EventBus


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


    // 只在这里做封装处理
    private var baseBundle: Bundle? = null


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseBundle = savedInstanceState
        // 禁用横屏，现目前不需要横屏，防止发送一些错误，
        // 如果要适配横屏，掌邮会有很多不规范的地方，尤其是 Fragment 那块没办法，学长们遗留下来的代码太多了
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initFlag()
        lifeCycleLog("onCreate")
    }

    // 在setContentView之后进行操作
    override fun setContentView(view: View?) { super.setContentView(view); notificationInit() }
    override fun setContentView(layoutResID: Int) { super.setContentView(layoutResID);notificationInit() }
    private fun notificationInit() {

            val verifyService = ServiceManager(IAccountService::class).getVerifyService()
            if (this is ActionLoginStatusSubscriber) {
                if (verifyService.isLogin()) initOnLoginMode(baseBundle)
                if (verifyService.isTouristMode()) initOnTouristMode(baseBundle)
                if (verifyService.isLogin() || verifyService.isTouristMode()) initPage(verifyService.isLogin(), baseBundle)
            }


    }

    private fun initFlag() {
        if (getDarkModeStatus()) {
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

    val common_toolbar by com.mredrock.cyxbs.config.R.id.toolbar.view<JToolbar>()

    protected fun JToolbar.initWithSplitLine(title: String,
                                             withSplitLine: Boolean = true,
                                             @DrawableRes icon: Int = R.drawable.common_ic_back,
                                             listener: View.OnClickListener? = View.OnClickListener { finish() },
                                             titleOnLeft: Boolean = true) {
        init(this@BaseActivity, title, withSplitLine, icon, titleOnLeft, listener)
    }

    override fun onRestart() {
        super.onRestart()
        lifeCycleLog("onRestart")
    }

    override fun onStart() {
        super.onStart()
        if (this is EventBusLifecycleSubscriber) {
            EventBus.getDefault().register(this)
        }
        lifeCycleLog("onStart")
    }

    override fun onResume() {
        super.onResume()
        lifeCycleLog("onResume")
    }

    override fun onPause() {
        super.onPause()
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
        val verifyService = ServiceManager(IAccountService::class).getVerifyService()
        if (this is ActionLoginStatusSubscriber) {
            if (verifyService.isLogin()) destroyOnLoginMode()
            if (verifyService.isTouristMode()) destroyOnTouristMode()
            if (verifyService.isLogin()||verifyService.isTouristMode()) destroyPage(verifyService.isLogin())
        }
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lifeCycleLog("onSaveInstanceState")
    }

    @CallSuper
    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        lifeCycleLog("onRestoreInstanceState")
    }

    private fun lifeCycleLog(message: String) {
        if (isOpenLifeCycleLog) {
            LogUtils.d(TAG, "${this::class.java.simpleName}\$\$${message}")
        }
    }

    /**
     * 在简单界面，使用这种方式来得到 View，避免使用 DataBinding 大材小用
     * ```
     * 使用方法：
     *    val mTvNum: TextView by R.id.xxx.view()
     * or
     *    val mTvNum by R.id.xxx.view<TextView>()
     * ```
     */
    protected fun <T: View> Int.view() = BindView<T>(this, this@BaseActivity)
    
    
    
    
    
    
    
    
    
}