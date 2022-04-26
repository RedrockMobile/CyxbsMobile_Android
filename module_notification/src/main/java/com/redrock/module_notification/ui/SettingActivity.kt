package com.redrock.module_notification.ui

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.NOTIFICATION_SETTING
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.redrock.module_notification.R
import com.redrock.module_notification.util.Constant.IS_SWITCH1_SELECT
import com.redrock.module_notification.util.Constant.IS_SWITCH2_SELECT
import com.redrock.module_notification.util.NotificationSp
import kotlinx.android.synthetic.main.activity_setting.*
import kotlin.properties.Delegates

/**
 * Author by OkAndGreat
 * Date on 2022/4/25 22:46.
 *
 */
//考虑到通知设置页以后可能会有其它渠道进入，故配置一个路由
@Route(path = NOTIFICATION_SETTING)
class SettingActivity :BaseActivity() {
    private var switch1Checked by Delegates.notNull<Boolean>()
    private var switch2Checked by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initSwitch()
        initViewClickListener()
    }

    private fun initSwitch(){
        switch1Checked = NotificationSp.getBoolean(IS_SWITCH1_SELECT,false)
        switch2Checked = NotificationSp.getBoolean(IS_SWITCH2_SELECT,false)
        notification_setting_switch_1.isChecked = switch1Checked
        notification_setting_switch_2.isChecked = switch2Checked
    }

    private fun initViewClickListener(){
        notification_setting_switch_1.setOnCheckedChangeListener{_, isChecked ->
            NotificationSp.editor {
                if (isChecked) {
                    putBoolean(IS_SWITCH1_SELECT, true)
                } else {
                    putBoolean(IS_SWITCH1_SELECT, false)
                }
            }
        }

        notification_setting_switch_2.setOnCheckedChangeListener{_, isChecked ->
            NotificationSp.editor {
                if (isChecked) {
                    putBoolean(IS_SWITCH2_SELECT, true)
                } else {
                    putBoolean(IS_SWITCH2_SELECT, false)
                }
            }
        }

        notification_rl_setting_back.setOnClickListener {
            finish()
        }
    }
}