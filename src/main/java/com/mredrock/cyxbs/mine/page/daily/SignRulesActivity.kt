package com.mredrock.cyxbs.mine.page.daily

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R


/**
 * Created by zzzia on 2018/8/15.
 * 签到规则
 */
class SignRulesActivity(override val isFragmentActivity: Boolean = false) : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_sign_rules)

        common_toolbar.init("签到规则")
    }
}
