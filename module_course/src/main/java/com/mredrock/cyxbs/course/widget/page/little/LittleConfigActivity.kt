package com.mredrock.cyxbs.course.widget.page.little

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.course.R

class LittleConfigActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_activity_little_config)

        common_toolbar.init("主题设置")
    }
}
