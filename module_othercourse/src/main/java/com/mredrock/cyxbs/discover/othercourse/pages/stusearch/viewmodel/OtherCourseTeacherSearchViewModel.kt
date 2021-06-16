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
import com.mredrock.cyxbs.discover.othercourse.room.TEACHER_TYPE

class OtherCourseTeacherSearchViewModel : OtherCourseSearchViewModel() {
    override fun getPerson(str: String, fromHistory: Boolean?) {
        ApiGenerator.getApiService(Services::class.java)
                .getTeacher(str)
                .mapOrThrowApiException()
                .setSchedulers()
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .doOnErrorWithDefaultErrorHandler {
                    toastEvent.value = R.string.othercourse_hint_no_person
                    true
                }
                .map {
                    it.map { teacher ->
                        Person(TEACHER_TYPE, teacher.teaNum, teacher.name, teacher.gender, teacher.teaRoom, teacher.teaMajor)
                    }
                }
                .safeSubscribeBy {
                    if (fromHistory == true) {
                        mListFromHistory.value = it
                    } else {
                        mList.value = it
                    }
                }.lifeCycle()
    }

    override fun getHistory() {
        getHistoryInternal(TEACHER_TYPE)
    }

    override fun addHistory(history: History, onAddFinished: () -> Unit) {
        history.type = TEACHER_TYPE
        addHistoryInternal(history, onAddFinished)
    }
}