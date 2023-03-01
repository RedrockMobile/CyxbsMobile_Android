package com.mredrock.cyxbs.mine.page.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.postDelayed
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.login.ILoginService
import com.mredrock.cyxbs.common.config.COURSE_SHOW_STATE
import com.mredrock.cyxbs.common.config.SP_WIDGET_NEED_FRESH
import com.mredrock.cyxbs.common.config.WIDGET_COURSE
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.impl
import com.mredrock.cyxbs.config.sp.defaultSp
import com.mredrock.cyxbs.config.view.JToolbar
import com.mredrock.cyxbs.lib.base.dailog.ChooseDialog
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.setSchedulers
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.activity.SecurityActivity
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.ui.WarningDialog
import com.mredrock.cyxbs.mine.util.widget.SwitchPlus
import io.reactivex.rxjava3.disposables.Disposable

class SettingActivity : BaseActivity() {
    private val mSwitch by R.id.mine_setting_switch.view<SwitchPlus>()
    private val mFmSecurity by R.id.mine_setting_fm_security.view<FrameLayout>()
    private val mFmClear by R.id.mine_setting_fm_clear.view<FrameLayout>()
    private val mBtnExit by R.id.mine_setting_btn_exit.view<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_setting)
        val toolbar = findViewById<JToolbar>(R.id.toolbar)
        //初始化toolbar

        toolbar.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    this@SettingActivity,
                    com.mredrock.cyxbs.common.R.color.common_mine_setting_common_back_color
                )
            )
            init(
                this@SettingActivity,
                "设置",
                withSplitLine = true,
                titleOnLeft = false
            )
        }
        //启动App优先显示课表
        mSwitch.setOnCheckedChangeListener { _, isChecked ->
            defaultSp.edit {
                if (isChecked) {
                    putBoolean(COURSE_SHOW_STATE, true)
                } else {
                    putBoolean(COURSE_SHOW_STATE, false)
                }
            }
        }
        mSwitch.isChecked = defaultSp.getBoolean(COURSE_SHOW_STATE, false)

        //账号安全
        mFmSecurity.setOnSingleClickListener {
            doIfLogin {
                startActivity(Intent(this, SecurityActivity::class.java))
            }
        }
        
        // 清理软件数据
        mFmClear.setOnSingleClickListener {
            var boolean = false
            ChooseDialog.Builder(
                this,
                ChooseDialog.Data(
                    content = "清理软件数据将重新登录并还原所有本地设置，请慎重选择！",
                    positiveButtonText = "确定",
                    negativeButtonText = "取消",
                    height = 160,
                )
            ).setPositiveClick {
                if (!boolean) {
                    toast("请再次点击进行确定")
                    boolean = true
                } else {
                    try {
                        // 用命令清理软件数据
                        Runtime.getRuntime().exec("pm clear $packageName")
                    } catch (e: Exception) {
                        toastLong("清理失败，请进入设置页面手动点击”清理数据“")
                        // 会打开手机应用设置中的掌邮页面，让他自动点击清理软件数据
                        val uri = Uri.parse("package:$packageName")
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
                        startActivity(intent)
                    }
                }
            }.setNegativeClick {
                dismiss()
            }.show()
        }
        
        //退出登录
        mBtnExit.setOnSingleClickListener {
            doIfLogin {
                toast("退出登录会先检查请求是否正常，请稍后~")
                onExitClick()
            }
        }
    }

    /**
     * 退出时的网络请求，用于在 activity onDestroy() 时及时取消
     */
    private var mExitDisposable: Disposable? = null

    private fun onExitClick() {
        val disposable = mExitDisposable
        if (disposable != null) {
            if (!disposable.isDisposed) {
                // 防止重复点击
                return
            }
        }
        mExitDisposable = apiService.pingMagipoke()
            .setSchedulers()
            .safeSubscribeBy(
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

    private fun doExit() {
        ChooseDialog.Builder(
            this,
            ChooseDialog.Data(
                content = "是否退出登录？",
                positiveButtonText = "确定",
                negativeButtonText = "取消",
                height = 160
            )
        ).setPositiveClick {
            jumpToLoginActivity()
        }.setNegativeClick {
            dismiss()
        }.show()
    }
    
    private fun jumpToLoginActivity() {
        cleanAppWidgetCache()
        //清除user信息，必须要在LoginStateChangeEvent之前
        ServiceManager(IAccountService::class).getVerifyService()
            .logout(this@SettingActivity)
        window.decorView.postDelayed(20) {
            // 延迟打开，保证前面的 logout 有时间清空数据
            ILoginService::class.impl
                .startLoginActivityReboot()
            finish()
        }
    }

    private fun cleanAppWidgetCache() {
        defaultSp.edit {
            putString(WIDGET_COURSE, "")
            putBoolean(SP_WIDGET_NEED_FRESH, true)
        }
    }
}