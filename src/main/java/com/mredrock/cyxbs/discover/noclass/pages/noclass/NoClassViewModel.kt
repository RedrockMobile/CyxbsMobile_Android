package com.mredrock.cyxbs.discover.noclass.pages.noclass

import android.arch.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.noclass.network.Services
import com.mredrock.cyxbs.discover.noclass.network.Student

/**
 * Created by zxzhu
 *   2018/9/9.
 *   enjoy it !!
 */
class NoClassViewModel: BaseViewModel() {
    var mStuList = MutableLiveData<List<Student>>()

    fun getStudent(str: String) {
        ApiGenerator.getApiService(Services::class.java)
                .getStudent(str)
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    mStuList.value = it
                    LogUtils.d("zzxTest", "change")
                }.lifeCycle()
    }
}