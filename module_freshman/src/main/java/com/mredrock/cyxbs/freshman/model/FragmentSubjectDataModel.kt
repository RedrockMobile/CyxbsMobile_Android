package com.mredrock.cyxbs.freshman.model

import android.annotation.SuppressLint
import com.mredrock.cyxbs.freshman.base.BaseModel
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentSubjectDataModel
import com.mredrock.cyxbs.freshman.interfaces.network.SubjectDataService
import com.mredrock.cyxbs.freshman.util.network.ApiGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FragmentSubjectDataModel : BaseModel(), IFragmentSubjectDataModel {
    @SuppressLint("CheckResult")
    override fun requestSubjectData(college: String, callback: IFragmentSubjectDataModel.Callback) {
        val service = ApiGenerator.getApiService(SubjectDataService::class.java)
        service.requestSubjectData()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { subjectDataBean ->
                    subjectDataBean.text.first { it.name == college }.message
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ callback.requestSubjectDataSuccess(it) },
                        { callback.requestSubjectDataFailed() })
    }
}