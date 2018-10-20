package com.mredrock.cyxbs.widget.page

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.WIDGET_SETTING
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.page.little.LittleConfigActivity
import com.mredrock.cyxbs.widget.page.normal.NormalConfigActivity
import com.mredrock.cyxbs.widget.page.trans.TransConfigActivity
import com.mredrock.cyxbs.widget.widget.little.LittleTransWidget
import com.mredrock.cyxbs.widget.widget.little.LittleWidget
import com.mredrock.cyxbs.widget.widget.normal.NormalWidget
import kotlinx.android.synthetic.main.widget_activity_config.*
import org.jetbrains.anko.toast


/**
 * Created by zia on 2018/10/11.
 * 设置主页面
 */
@Route(path = WIDGET_SETTING)
class ConfigActivity(override val isFragmentActivity: Boolean = false) : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_activity_config)

        common_toolbar.init("控件选择")

        widget_config_littleTransLayout.setOnClickListener {
            startActivity(Intent(this@ConfigActivity, TransConfigActivity::class.java))
        }

        widget_config_littleLayout.setOnClickListener {
            startActivity(Intent(this@ConfigActivity, LittleConfigActivity::class.java))
        }

        widget_config_normalLayout.setOnClickListener {
            startActivity(Intent(this@ConfigActivity, NormalConfigActivity::class.java))
        }

        //本来想网络请求刷新数据的，这样准一点，但是lib的工具类不能改，不配置okHttp又怕有证书问题，所以直接刷新界面算了
//        widget_config_fresh.setOnClickListener {
//            Thread(Runnable {
//                LittleTransWidget().refresh(this@ConfigActivity)
//                LittleWidget().refresh(this@ConfigActivity)
//                NormalWidget().fresh(this@ConfigActivity, 0)
//                runOnUiThread {
//                    toast("刷新成功")
//                }
//            }).start()
//        }
    }
}
