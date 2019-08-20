package com.mredrock.cyxbs.freshman.model

import android.annotation.SuppressLint
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.freshman.base.BaseModel
import com.mredrock.cyxbs.freshman.bean.CollegeGroupText
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentCollegeGroupModel
import com.mredrock.cyxbs.freshman.interfaces.network.CollegeGroupService
import com.mredrock.cyxbs.freshman.util.network.ApiGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Create by yuanbing
 * on 2019/8/4
 */
class FragmentCollegeGroupModel : BaseModel(), IFragmentCollegeGroupModel {
    @SuppressLint("CheckResult")
    override fun searchCollegeGroup(college: String, callback: (List<CollegeGroupText>) -> Unit) {
        val service = ApiGenerator.getApiService(CollegeGroupService::class.java)
        service.search(college)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ callback(it.text) }, { callback(listOf()) })
    }

    override fun requestCollegeGroup(callback: (List<CollegeGroupText>) -> Unit) {
        val service = ApiGenerator.getApiService(CollegeGroupService::class.java)
        service.requestCollegeGroup()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribeBy { callback(it.text) }
    }
}