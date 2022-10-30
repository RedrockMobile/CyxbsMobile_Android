package com.mredrock.cyxbs.main.ui.defaultpage

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.config.route.DEFAULT_PAGE
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.main.R

/**
 * @author : why
 * @time   : 2022/10/30 21:02
 * @bless  : God bless my code
 */
@Route(path = DEFAULT_PAGE)
class DefaultPageActivity: BaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_default_page)
        "点击退出".toast()
        val cl = findViewById<ConstraintLayout>(R.id.main_cl_default_page)
        cl.setOnClickListener { finish() }
    }
}