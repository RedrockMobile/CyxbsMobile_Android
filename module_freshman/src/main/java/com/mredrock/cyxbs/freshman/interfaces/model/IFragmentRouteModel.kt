package com.mredrock.cyxbs.freshman.interfaces.model

import com.mredrock.cyxbs.freshman.base.IBaseModel
import com.mredrock.cyxbs.freshman.bean.BusRoute

/**
 * Create by roger
 * on 2019/8/3
 */
interface IFragmentRouteModel : IBaseModel {
    fun getData(call: Callback)
}
interface Callback {
    fun onSuccess(route: BusRoute)
    fun onFailed()
}
