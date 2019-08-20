package com.mredrock.cyxbs.freshman.model

import android.annotation.SuppressLint
import com.mredrock.cyxbs.freshman.base.BaseModel
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentSexRatioModel
import com.mredrock.cyxbs.freshman.interfaces.network.SexRatioService
import com.mredrock.cyxbs.freshman.util.network.ApiGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FragmentSexRatioModel : BaseModel(), IFragmentSexRatioModel {
    @SuppressLint("CheckResult")
    override fun requestSexRatio(college: String, callback: IFragmentSexRatioModel.Callback) {
        val service = ApiGenerator.getApiService(SexRatioService::class.java)
        service.requestSexRatio()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { sexRatioBean ->
                    sexRatioBean.text.first { it.name == college }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ callback.requestSexRatioSuccess(it) },
                        { callback.requestSexRatioFaild() })
    }
}