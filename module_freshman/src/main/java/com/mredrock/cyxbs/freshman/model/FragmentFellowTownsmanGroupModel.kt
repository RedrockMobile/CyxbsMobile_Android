package com.mredrock.cyxbs.freshman.model

import android.annotation.SuppressLint
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.freshman.base.BaseModel
import com.mredrock.cyxbs.freshman.bean.FellowTownsmanGroupText
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentFellowTownsmanGroupModel
import com.mredrock.cyxbs.freshman.interfaces.network.FellowTownsmanGroupService
import com.mredrock.cyxbs.freshman.util.network.ApiGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Create by yuanbing
 * on 2019/8/4
 */
class FragmentFellowTownsmanGroupModel : BaseModel(), IFragmentFellowTownsmanGroupModel {
    @SuppressLint("CheckResult")
    override fun searchFellowTownsmanGroup(province: String, callback: (List<FellowTownsmanGroupText>) -> Unit) {
        val service = ApiGenerator.getApiService(FellowTownsmanGroupService::class.java)
        service.search(province)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ callback(it.text) }, { callback(listOf()) })
    }

    @SuppressLint("CheckResult")
    override fun requestFellowTownsmanGroup(callback: (List<FellowTownsmanGroupText>) -> Unit) {
        val service = ApiGenerator.getApiService(FellowTownsmanGroupService::class.java)
        service.requestFellowTownsmanGroupService()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribeBy { callback(it.text) }
    }
}