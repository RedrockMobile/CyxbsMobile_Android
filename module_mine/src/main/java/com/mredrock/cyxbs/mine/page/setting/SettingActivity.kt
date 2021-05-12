package com.mredrock.cyxbs.mine.page.setting

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.component.CommonDialogFragment
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.main.MAIN_LOGIN
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.activity.SecurityActivity
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.ui.WarningDialog
import kotlinx.android.synthetic.main.mine_activity_setting.*
import kotlinx.android.synthetic.main.mine_activity_tablayout_my_product.view.*

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_setting)

        //初始化toolbar

        mine_setting_toolbar.toolbar.apply {
            setTitleLocationAtLeft(false)
            setBackgroundColor(ContextCompat.getColor(this@SettingActivity, R.color.common_mine_setting_common_back_color))
            initWithSplitLine(
                    "设置",
                    withSplitLine = true,
                    titleOnLeft = false
            )
        }
        //启动App优先显示课表
        mine_setting_switch.setOnCheckedChangeListener { _, isChecked ->
            defaultSharedPreferences.editor {
                if (isChecked) {
                    putBoolean(COURSE_SHOW_STATE, true)
                } else {
                    putBoolean(COURSE_SHOW_STATE, false)
                }
            }
        }
        mine_setting_switch.isChecked = context?.defaultSharedPreferences?.getBoolean(COURSE_SHOW_STATE, false)
                ?: false

        //自定义桌面小组件
        mine_setting_fm_edit_widget.setOnClickListener {
            ARouter.getInstance().build(WIDGET_SETTING).navigation()
        }

        //账号安全
        mine_setting_fm_security.setOnClickListener { doIfLogin { startActivity<SecurityActivity>() } }
        //屏蔽此人
        mine_setting_fm_shield_person.setOnClickListener { doIfLogin { ARouter.getInstance().build(QA_MY_IGNORE).navigation() } }
        //退出登录
        mine_setting_btn_exit.setOnClickListener { doIfLogin { onExitClick() } }
    }

    private fun onExitClick() {
        apiService.pingMagipoke()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { true }
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
                                        cleanAppWidgetCache()
                                        //清除user信息，必须要在LoginStateChangeEvent之前
                                        ServiceManager.getService(IAccountService::class.java).getVerifyService().logout(BaseApp.context)
                                        //清空activity栈
                                        val flag = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        ARouter.getInstance().build(MAIN_LOGIN).withFlags(flag).withBoolean(IS_EXIT_LOGIN, true).navigation()
                                    }
                            )
                        }
                )
    }

    private fun doExit(){
        val tag = "exit"
        if (this.supportFragmentManager.findFragmentByTag(tag) == null) {
            CommonDialogFragment().apply {
                initView(
                        containerRes = R.layout.mine_layout_dialog_logout,
                        onPositiveClick = {
                            cleanAppWidgetCache()
                            //清除user信息，必须要在LoginStateChangeEvent之前
                            ServiceManager.getService(IAccountService::class.java).getVerifyService().logout(BaseApp.context)
                            //清空activity栈
                            val flag = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            ARouter.getInstance().build(MAIN_LOGIN).withFlags(flag).withBoolean(IS_EXIT_LOGIN, true).navigation()
                        },
                        positiveString = "退出",
                        onNegativeClick = { dismiss() }
                )
            }.show(this.supportFragmentManager, tag)
        }
    }

    private fun cleanAppWidgetCache() {
        context?.defaultSharedPreferences?.editor {
            putString(WIDGET_COURSE, "")
            putBoolean(SP_WIDGET_NEED_FRESH, true)
        }
    }
}