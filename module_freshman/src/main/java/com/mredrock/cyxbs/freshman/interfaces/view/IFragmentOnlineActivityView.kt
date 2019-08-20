package com.mredrock.cyxbs.freshman.interfaces.view

import com.mredrock.cyxbs.freshman.base.IBaseView
import com.mredrock.cyxbs.freshman.bean.OnlineActivityText

/**
 * Create by yuanbing
 * on 2019/8/3
 */
interface IFragmentOnlineActivityView : IBaseView {
    fun showOnlineActivities(activities: List<OnlineActivityText>)
}