package com.mredrock.cyxbs.mine.page.daily

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R


/**
 * Created by zzzia on 2018/8/16.
 * 积分明细
 */
class PointDetailActivity(override val isFragmentActivity: Boolean = false) : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_point_detail)

        common_toolbar.init("积 分 明 细")

        // todo 还没写
    }
}
