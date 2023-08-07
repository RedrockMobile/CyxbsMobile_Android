package com.mredrock.cyxbs.ufield

import android.content.Intent
import android.os.Bundle
import com.mredrock.cyxbs.lib.base.BaseDebugActivity

/**
 * description ：
 * author : lytMoon
 * email : yytds@foxmail.com
 * date : 2023/8/7 15:28
 * version: 1.0
 */
class DebugActivity : BaseDebugActivity() {
    override fun onDebugCreate(savedInstanceState: Bundle?) {
        startActivity(Intent(this, UfieldActivity::class.java))
    }
}