package com.mredrock.cyxbs.freshman.model

import android.annotation.SuppressLint
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.freshman.base.BaseModel
import com.mredrock.cyxbs.freshman.bean.EnrollmentRequirementsItemBean
import com.mredrock.cyxbs.freshman.bean.EnrollmentRequirementsTitleBean
import com.mredrock.cyxbs.freshman.interfaces.ParseBean
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityEnrollmentRequirementsModel
import com.mredrock.cyxbs.freshman.interfaces.network.EnrollmentRequirementsService
import com.mredrock.cyxbs.freshman.util.MemorandumManager
import com.mredrock.cyxbs.freshman.util.network.ApiGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Create by yuanbing
 * on 2019/8/8
 */
class ActivityEnrollmentRequirementsModel : BaseModel(), IActivityEnrollmentRequirementsModel {
    @SuppressLint("CheckResult")
    override fun requestEnrollmentRequirements(callback: (List<ParseBean>) -> Unit) {
        val service = ApiGenerator.getApiService(EnrollmentRequirementsService::class.java)
        service.requestEnrollmentRequirements()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { rawBean ->
                    rawBean.text.flatMap { rawText ->
                        val data = mutableListOf<ParseBean>()
                        data.add(EnrollmentRequirementsTitleBean(rawText.title))
                        data.addAll(rawText.data.asSequence().map {
                            MemorandumManager.addMust(it.name)
                            EnrollmentRequirementsItemBean(it.name, it.detail.trimEnd('\r', '\n'),
                                    MemorandumManager.status(it.name))
                        })
                        data
                    }
                }
                .observeOn(Schedulers.io())
                .map { must ->
                    MemorandumManager.getAll().let {
                        it.reverse()
                        it.addAll(must)
                        it
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribeBy { callback(it) }
    }
}