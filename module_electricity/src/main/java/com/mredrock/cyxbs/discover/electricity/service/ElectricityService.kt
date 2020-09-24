package com.mredrock.cyxbs.discover.electricity.service

import android.content.Context
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.DISCOVER_ELECTRICITY_FEED
import com.mredrock.cyxbs.common.config.ELECTRICITY_SERVICE
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor

/**
 * Created by yyfbe, Date on 2020/8/31.
 */
@Route(path = ELECTRICITY_SERVICE, name = ELECTRICITY_SERVICE)
class ElectricityService : com.mredrock.cyxbs.discover.electricity.IElectricityService {
    private var mContext: Context? = null
    override fun getElectricityFeed(): Fragment {
        return ARouter.getInstance().build(DISCOVER_ELECTRICITY_FEED).navigation() as Fragment
    }

    override fun clearSP() {
        mContext?.defaultSharedPreferences?.editor {
            remove("select_building_head_position")
            remove("select_building_foot_position")
            remove("select_room_position")
        }
    }

    override fun init(context: Context?) {
        this.mContext = context
    }
}