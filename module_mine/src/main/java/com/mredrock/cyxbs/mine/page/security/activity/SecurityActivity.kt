package com.mredrock.cyxbs.mine.page.security.activity

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
/**
 * Author: RayleighZ
 * Time: 2020-10-29 15:06
 */
class SecurityActivity : BaseActivity() {

    override val isFragmentActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_security)
        setSupportActionBar(findViewById(R.id.toolbar))
    }
}