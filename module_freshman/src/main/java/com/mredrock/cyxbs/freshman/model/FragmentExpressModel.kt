package com.mredrock.cyxbs.freshman.model

import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.freshman.base.BaseModel
import com.mredrock.cyxbs.freshman.bean.ExpressText
import com.mredrock.cyxbs.freshman.config.API_BASE_IMG_URL
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentExpressModel
import com.mredrock.cyxbs.freshman.interfaces.network.ExpressService
import com.mredrock.cyxbs.freshman.util.network.ApiGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FragmentExpressModel : BaseModel(), IFragmentExpressModel {
    override fun requestExpress(callback: (List<ExpressText>) -> Unit) {
        val service = ApiGenerator.getApiService(ExpressService::class.java)
        service.requestExpress()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { expressBean ->
                    expressBean.text.map { text ->
                        text.message.map { message ->
                            message.photo = "$API_BASE_IMG_URL${message.photo}"
                            message.detail = message.detail.trimEnd('\r', '\n')
                            message
                        }
                        text
                    }
                    expressBean.text
                }
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribeBy { callback(it) }
    }
}