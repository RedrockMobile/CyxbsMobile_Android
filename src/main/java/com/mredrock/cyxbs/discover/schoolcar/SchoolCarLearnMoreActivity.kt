package com.mredrock.cyxbs.discover.schoolcar

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.schoolcar.R
import kotlinx.android.synthetic.main.activity_school_car_learn_more.*

class SchoolCarLearnMoreActivity : BaseActivity() {

    override val isFragmentActivity = true

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_car_learn_more)

        initToolBar()
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initToolBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.statusBarColor = Color.TRANSPARENT
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        if (learn_more_school_car_toolbar != null) {
            learn_more_school_car_toolbar.title = ""
            setSupportActionBar(learn_more_school_car_toolbar)
        }
    }

    private fun initView(){
        learn_more_toolbar_back.setOnClickListener {
            finish()
        }
    }
}
