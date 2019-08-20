package com.mredrock.cyxbs.freshman.interfaces.model

import com.mredrock.cyxbs.freshman.base.IBaseModel
import com.mredrock.cyxbs.freshman.bean.OnlineActivityText

/**
 * Create by yuanbing
 * on 2019/8/3
 */
interface IFragmentOnlineActivityModel : IBaseModel {
    fun request(callback: (List<OnlineActivityText>) -> Unit)
}