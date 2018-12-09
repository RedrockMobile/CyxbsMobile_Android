package com.mredrock.cyxbs.qa.pages.report

import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.checkError
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.network.ApiService

/**
 * Created By jay68 on 2018/12/8.
 */
class ReportViewModel : BaseViewModel() {
    val backPreActivityEvent = SingleLiveEvent<Boolean>()

    fun report(qid: String, content: String, type: String) {
        progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT

        val user = BaseApp.user!!
        ApiGenerator.getApiService(ApiService::class.java)
                .report(user.stuNum ?: "", user.idNum ?: "", qid, content, type)
                .setSchedulers()
                .checkError()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnError { toastEvent.value = R.string.qa_service_error_hint }
                .safeSubscribeBy {
                    toastEvent.value = R.string.qa_hint_report_success
                    backPreActivityEvent.value = true
                }
    }
}