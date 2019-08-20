package com.mredrock.cyxbs.freshman.interfaces.presenter

import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.bean.BusRoute
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentRouteModel
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentRouteView

/**
 * Create by roger
 * on 2019/8/3
 */
interface IFragmentRoutePresenter :
        IBasePresenter<IFragmentRouteView, IFragmentRouteModel> {
    fun onAddressLoad(busRoute: BusRoute)
    fun onDataNotAvailable()
    fun populateData()
    fun start()
}