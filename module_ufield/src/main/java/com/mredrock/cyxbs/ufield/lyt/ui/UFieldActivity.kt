package com.mredrock.cyxbs.ufield.lyt.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.ufield.R
/**
 * description ：最初从中心模块跳转到这个activity
 * author : lytMoon
 * email : yytds@foxmail.com
 * date : 2023/8/7 19:49
 * version: 1.0
 */
class UFieldActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ufield_activity_ufield)
    }


}