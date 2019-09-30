package com.mredrock.cyxbs.discover.schoolcar

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.schoolcar.R

class SchoolCarLearnMoreActivity : BaseActivity() {

    override val isFragmentActivity = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_car_learn_more)

        common_toolbar.init("提示")
    }
}
