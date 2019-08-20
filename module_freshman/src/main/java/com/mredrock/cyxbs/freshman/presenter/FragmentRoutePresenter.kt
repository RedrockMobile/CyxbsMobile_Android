package com.mredrock.cyxbs.freshman.presenter

import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.bean.BusRoute
import com.mredrock.cyxbs.freshman.interfaces.model.Callback
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentRouteModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentRoutePresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentRouteView
import com.mredrock.cyxbs.freshman.model.FragmentRouteModel

/**
 * Create by roger
 * on 2019/8/3
 */
class FragmentRoutePresenter :
        BasePresenter<IFragmentRouteView, IFragmentRouteModel>(),
        IFragmentRoutePresenter{
    override fun start() {
        populateData()
    }

    override fun onAddressLoad(busRoute: BusRoute) {
        view?.setRoute(busRoute.text_2?.message!!, busRoute.text_1!!)
    }

    override fun onDataNotAvailable() {
    }

    override fun populateData() {
        model?.getData(object : Callback {
            override fun onSuccess(route: BusRoute) {
                onAddressLoad(route)
            }

            override fun onFailed() {
                onDataNotAvailable()
            }

        })
    }

    override fun attachModel(): IFragmentRouteModel {
        return FragmentRouteModel()
    }

}