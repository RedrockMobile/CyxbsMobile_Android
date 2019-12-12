/**
 * @Author fxy
 * @Date 2019-12-10 20:44
 */

package com.mredrock.cyxbs.discover.grades.ui.viewModel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.Exam
import com.mredrock.cyxbs.discover.grades.bean.Grade
import com.mredrock.cyxbs.discover.grades.network.ApiService
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class ContainerViewModel : BaseViewModel() {
    val examData = MutableLiveData<List<Exam>>()
    val gradesData = MutableLiveData<List<Grade>>()
    private val apiService = ApiGenerator.getApiService(ApiService::class.java)
    private lateinit var examDisposable: Disposable
    private lateinit var gradeDisposable: Disposable
    fun loadData(stuNum: String){
        val exam = apiService.getExam(stuNum)
        val reExam = apiService.getReExam(stuNum)
        examDisposable = Observable
                .merge(exam,reExam)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    toastEvent.value = R.string.grades_no_exam_history
                    false
                }
                .safeSubscribeBy {
                    examData.value = it.data
                }
    }

    fun loadGrades(stuNum: String,idNum: String){
        gradeDisposable = apiService
                .getGrades(stuNum, idNum)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    toastEvent.value = R.string.grades_no_grades_history
                    false
                }
                .safeSubscribeBy {
                    gradesData.value = it
                }
    }

    override fun onCleared() {
        super.onCleared()
        examDisposable.dispose()
        gradeDisposable.dispose()
    }
}