package com.redrock.module_notification

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.NOTIFICATION_HOME
import com.mredrock.cyxbs.common.ui.BaseActivity


@Route(path = NOTIFICATION_HOME)
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}