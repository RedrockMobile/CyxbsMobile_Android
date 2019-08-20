package com.mredrock.cyxbs.freshman.model

import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.freshman.base.BaseModel
import com.mredrock.cyxbs.freshman.interfaces.model.Callback
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentRouteModel
import com.mredrock.cyxbs.freshman.interfaces.network.CampusService
import com.mredrock.cyxbs.freshman.util.network.ApiGenerator

/**
 * Create by roger
 * on 2019/8/3
 */
class FragmentRouteModel : BaseModel(),  IFragmentRouteModel{
    override fun getData(call: Callback) {

        ApiGenerator.getApiService(CampusService::class.java)
                .getRoutes()
                .setSchedulers()
                .safeSubscribeBy (
                        onError = {
                        },
                        onComplete = {
                        },
                        onNext = {
                            call.onSuccess(it)}
                )

    }
}