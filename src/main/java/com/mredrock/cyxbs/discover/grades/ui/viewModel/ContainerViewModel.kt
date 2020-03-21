/**
 * @Author fxy
 * @Date 2019-12-10 20:44
 */

package com.mredrock.cyxbs.discover.grades.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.ApiGeneratorForSign
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.Exam
import com.mredrock.cyxbs.discover.grades.bean.Grade
import com.mredrock.cyxbs.discover.grades.bean.IdsBean
import com.mredrock.cyxbs.discover.grades.bean.analyze.GPAStatus
import com.mredrock.cyxbs.discover.grades.network.ApiService
import io.reactivex.Observable

class ContainerViewModel : BaseViewModel() {
    companion object {
        const val STATUS_OK = "10000"
        const val ERROR = "10010"
    }

    val examData = MutableLiveData<List<Exam>>()
    val gradesData = MutableLiveData<List<Grade>>()
    private val apiService = ApiGenerator.getApiService(ApiService::class.java)
    private val apiServiceForSign = ApiGeneratorForSign.getApiService(ApiService::class.java)
    fun loadData(stuNum: String) {
        val exam = apiService.getExam(stuNum)
        val reExam = apiService.getReExam(stuNum)
        Observable.merge(exam, reExam)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    toastEvent.value = R.string.grades_no_exam_history
                    false
                }
                .safeSubscribeBy {
                    examData.value = it.data
                }.lifeCycle()
    }

    fun loadGrades(stuNum: String, idNum: String) {
        apiService.getGrades(stuNum, idNum)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    toastEvent.value = R.string.grades_no_grades_history
                    false
                }
                .safeSubscribeBy {
                    gradesData.value = it
                }.lifeCycle()
    }

    fun bindIds(idsNum: String, idsPassword: String) {
        apiService.bindIds(IdsBean(idsNum, idsPassword))
                .setSchedulers()
                .safeSubscribeBy {
                    if (it.status == STATUS_OK) {
                        BaseApp.context.toast(R.string.grades_bottom_sheet_bind_success)
                        replaceBindFragmentToGPAFragment.postValue(true)
                    } else if (it.errorCode == ERROR) {
                        BaseApp.context.toast(R.string.grades_bottom_sheet_bind_fail)
                    }
                }.lifeCycle()

    }

    val replaceBindFragmentToGPAFragment: SingleLiveEvent<Boolean> = SingleLiveEvent()

    private val _analyzeData = MutableLiveData<GPAStatus>()
    val analyzeData: LiveData<GPAStatus>
        get() = _analyzeData

    fun getAnalyzeData() {
        apiServiceForSign.getAnalyzeData()
                .setSchedulers()
                .safeSubscribeBy(
                        onNext = {
                            _analyzeData.postValue(it)
                        },
                        onError = {
                            BaseApp.context.toast("加载绩点失败")
                        }
                ).lifeCycle()
    }

}