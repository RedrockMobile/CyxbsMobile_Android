package com.mredrock.cyxbs.widget.page.little

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.widget.R

class LittleConfigActivity(override val isFragmentActivity: Boolean = false) : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_activity_little_config)

        common_toolbar.init("主题设置")
    }
}
