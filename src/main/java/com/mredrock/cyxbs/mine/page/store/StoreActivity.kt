package com.mredrock.cyxbs.mine.page.store

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R


/**
 * Created by zzzia on 2018/8/14.
 * 商城
 */
class StoreActivity(override val isFragmentActivity: Boolean = false) : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_store)

        common_toolbar.init("积分商城")
    }
}
