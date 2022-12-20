package com.mredrock.cyxbs.mine.page.setting

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.postDelayed
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.appbar.AppBarLayout
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.login.ILoginService
import com.mredrock.cyxbs.common.component.CommonDialogFragment
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.service.impl
import com.mredrock.cyxbs.common.component.JToolbar
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.activity.SecurityActivity
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.ui.WarningDialog
import com.mredrock.cyxbs.mine.util.widget.SwitchPlus
import io.reactivex.rxjava3.disposables.Disposable

class SettingActivity : BaseActivity() {
    private val mToolbar by R.id.mine_setting_toolbar.view<AppBarLayout>()
    private val mSwitch by R.id.mine_setting_switch.view<SwitchPlus>()
    private val mFmSecurity by R.id.mine_setting_fm_security.view<FrameLayout>()
    private val mFmShieldPerson by R.id.mine_setting_fm_shield_person.view<FrameLayout>()
    private val mBtnExit by R.id.mine_setting_btn_exit.view<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_setting)
        val toolbar:JToolbar = mToolbar.findViewById(R.id.toolbar)
        //初始化toolbar

        toolbar.apply {
            setTitleLocationAtLeft(false)
            setBackgroundColor(
                ContextCompat.getColor(
                    this@SettingActivity,
                    com.mredrock.cyxbs.common.R.color.common_mine_setting_common_back_color
                )
            )
            initWithSplitLine(
                "设置",
                withSplitLine = true,
                titleOnLeft = false
            )
        }
        //启动App优先显示课表
        mSwitch.setOnCheckedChangeListener { _, isChecked ->
            defaultSharedPreferences.editor {
                if (isChecked) {
                    putBoolean(COURSE_SHOW_STATE, true)
                } else {
                    putBoolean(COURSE_SHOW_STATE, false)
                }
            }
        }
        mSwitch.isChecked =
            defaultSharedPreferences.getBoolean(COURSE_SHOW_STATE, false)

        //账号安全
        mFmSecurity.setOnSingleClickListener { doIfLogin { startActivity<SecurityActivity>() } }
        //屏蔽此人
        mFmShieldPerson.setOnSingleClickListener {
            doIfLogin {
                ARouter.getInstance().build(QA_MY_IGNORE).navigation()
            }
        }
        //退出登录
        mBtnExit.setOnSingleClickListener {
            doIfLogin {
                com.mredrock.cyxbs.lib.utils.extensions.toast("退出登录会先检查请求是否正常，请稍后~")
                onExitClick()
            }
        }
    }

    /**
     * 退出时的网络请求，用于在 activity onDestroy() 时及时取消
     */
    private var mExitDisposable: Disposable? = null

    private fun onExitClick() {
        if (mExitDisposable != null) {
            return
        }
        mExitDisposable = apiService.pingMagipoke()
            .setSchedulers()
            .unsafeSubscribeBy(
                onNext = {
                    //判定magipoke系列接口正常，允许正常退出登陆
                    doExit()
                },
                onError = {
                    //判定magipoke系列接口异常，极有可能会导致退出之后无法重新登陆，弹一个dialog提示一下
                    WarningDialog.showDialog(
                        this,
                        "温馨提示",
                        "因服务器或当前手机网络原因，检测到掌邮核心服务暂不可用，退出登录之后有可能会导致无法正常登录，是否确认退出登录？",
                        onNegativeClick = {
                            //内部已经将dialog消除，这里啥都不用处理
                        },
                        onPositiveClick = {
                            jumpToLoginActivity()
                        }
                    )
                }
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        mExitDisposable?.dispose() // 取消网络请求
    }

    private fun doExit() {
        val tag = "exit"
        if (this.supportFragmentManager.findFragmentByTag(tag) == null) {
            CommonDialogFragment().apply {
                initView(
                    containerRes = R.layout.mine_layout_dialog_logout,
                    onPositiveClick = {
                        jumpToLoginActivity()
                    },
                    positiveString = "退出",
                    onNegativeClick = { dismiss() }
                )
            }.show(this.supportFragmentManager, tag)
        }
    }
    
    private fun jumpToLoginActivity() {
        cleanAppWidgetCache()
        //清除user信息，必须要在LoginStateChangeEvent之前
        ServiceManager.getService(IAccountService::class.java).getVerifyService()
            .logout(this@SettingActivity)
        window.decorView.postDelayed(20) {
            // 延迟打开，保证前面的 logout 有时间清空数据
            ILoginService::class.impl
                .startLoginActivityReboot()
            finish()
        }
    }

    private fun cleanAppWidgetCache() {
        defaultSharedPreferences.editor {
            putString(WIDGET_COURSE, "")
            putBoolean(SP_WIDGET_NEED_FRESH, true)
        }
    }
}