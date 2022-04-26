package com.redrock.module_notification

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.NOTIFICATION_SETTING
import com.mredrock.cyxbs.common.ui.BaseActivity

/**
 * Author by OkAndGreat
 * Date on 2022/4/25 22:46.
 *
 */
//考虑到通知设置页以后可能会有其它渠道进入，故配置一个路由
@Route(path = NOTIFICATION_SETTING)
class SettingActivity :BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
    }
}