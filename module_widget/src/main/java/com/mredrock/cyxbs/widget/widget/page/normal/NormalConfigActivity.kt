package com.mredrock.cyxbs.widget.widget.page.normal

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.widget.R

class NormalConfigActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_activity_normal_config)

        common_toolbar.init("主题设置")
    }
}
