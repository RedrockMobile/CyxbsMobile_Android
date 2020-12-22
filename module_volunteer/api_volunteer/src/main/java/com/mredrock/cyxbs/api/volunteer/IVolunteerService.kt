package com.mredrock.cyxbs.api.volunteer

import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * Created by yyfbe, Date on 2020/8/31.
 */
interface IVolunteerService : IProvider {
    fun getVolunteerFeed(): Fragment
    fun clearSP()

}