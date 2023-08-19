package com.mredrock.cyxbs.api.electricity

import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * Created by yyfbe, Date on 2020/8/31.
 */
interface IElectricityService : IProvider {
    fun getElectricityFeed():Fragment
}