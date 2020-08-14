package com.mredrock.cyxbs.discover.othercourse.pages.stusearch.viewmodel

import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.network.Person
import com.mredrock.cyxbs.discover.othercourse.network.Services
import com.mredrock.cyxbs.discover.othercourse.room.History
import com.mredrock.cyxbs.discover.othercourse.room.STUDENT_TYPE

class OtherCourseStudentSearchViewModel : OtherCourseSearchViewModel() {

    override fun getPerson(str: String) {
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
                .map {
                    it.map { student ->
                        Person(STUDENT_TYPE, student.stunum, student.name, student.gender, student.major, student.depart)
                    }
                }
                .safeSubscribeBy {
                    mList.value = it
                }.lifeCycle()
    }

    override fun getHistory() {
        getHistoryInternal(STUDENT_TYPE)
    }

    override fun addHistory(history: History) {
        history.type = STUDENT_TYPE
        addHistoryInternal(history)
    }
}