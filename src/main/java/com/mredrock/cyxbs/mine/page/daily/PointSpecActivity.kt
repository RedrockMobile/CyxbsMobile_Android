package com.mredrock.cyxbs.mine.page.daily

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R


/**
 * Created by zzzia on 2018/8/15.
 * 积分说明
 */
class PointSpecActivity(override val isFragmentActivity: Boolean = false) : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_point_spec)

        common_toolbar.init("积分说明")


    }
}
