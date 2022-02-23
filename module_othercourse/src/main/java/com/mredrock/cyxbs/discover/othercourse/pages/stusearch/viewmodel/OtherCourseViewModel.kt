package com.mredrock.cyxbs.discover.othercourse.pages.stusearch.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.network.Services
import com.mredrock.cyxbs.discover.othercourse.network.Student

/**
 * Created by zxzhu
 *   2018/10/19.
 *   enjoy it !!
 */
class OtherCourseViewModel : BaseViewModel() {
    var mStuList = MutableLiveData<List<Student>>()

    fun getStudent(str: String) {
        ApiGenerator.getApiService(Services::class.java)
                .getStudent(str)
                .mapOrThrowApiException()
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .doOnErrorWithDefaultErrorHandler {
                    toastEvent.value = R.string.othercourse_hint_no_person
                    true
                }
                .safeSubscribeBy {
                    mStuList.value = it
                }.lifeCycle()
    }
}