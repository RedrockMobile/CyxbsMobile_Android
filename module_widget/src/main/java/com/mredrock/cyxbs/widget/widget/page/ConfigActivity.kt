package com.mredrock.cyxbs.widget.widget.page

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.config.route.WIDGET_SETTING
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.widget.page.normal.NormalConfigActivity
import kotlinx.android.synthetic.main.widget_activity_config.*


/**
 * Created by zia on 2018/10/11.
 * 设置主页面
 */
@Route(path = WIDGET_SETTING)
class ConfigActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_activity_config)
        widget_config_normalLayout.setOnClickListener {
            startActivity(Intent(this@ConfigActivity, NormalConfigActivity::class.java))
        }
    }

}
