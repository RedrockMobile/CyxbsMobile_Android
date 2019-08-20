package com.mredrock.cyxbs.freshman.model

import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.freshman.base.BaseModel
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentDataDisclosureModel
import com.mredrock.cyxbs.freshman.interfaces.network.SubjectDataService
import com.mredrock.cyxbs.freshman.util.network.ApiGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FragmentDataDisclosureModel : BaseModel(), IFragmentDataDisclosureModel {
    override fun requestCollegeData(callback: (List<String>) -> Unit) {
        val service = ApiGenerator.getApiService(SubjectDataService::class.java)
        service.requestSubjectData()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { collegeDataBean -> collegeDataBean.text.asSequence().map { it.name } }
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribeBy { callback(it.toList()) }
    }
}