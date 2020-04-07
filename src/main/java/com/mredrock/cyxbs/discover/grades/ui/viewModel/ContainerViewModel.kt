/**
 * @Author fxy
 * @Date 2019-12-10 20:44
 */

package com.mredrock.cyxbs.discover.grades.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.ApiGeneratorForAnother
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.Exam
import com.mredrock.cyxbs.discover.grades.bean.IdsBean
import com.mredrock.cyxbs.discover.grades.bean.IdsStatus
import com.mredrock.cyxbs.discover.grades.bean.analyze.GPAStatus
import com.mredrock.cyxbs.discover.grades.network.ApiService
import io.reactivex.Observable
import retrofit2.HttpException

class ContainerViewModel : BaseViewModel() {
    companion object {
        const val STATUS_OK = "10000"
        const val ERROR = "10010"
    }

    val examData = MutableLiveData<List<Exam>>()
    private val apiService = ApiGenerator.getApiService(ApiService::class.java)
    private val apiServiceForSign = ApiGeneratorForAnother.getApiService(ApiService::class.java)
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


    private var isAnimating = false

    fun bindIds(idsNum: String, idsPassword: String, bubble: () -> Unit) {
        if (isAnimating) {
            return
        }
        isAnimating = true
        bubble.invoke()
        val startTime = System.currentTimeMillis()
        apiService.bindIds(IdsBean(idsNum, idsPassword))
                .doOnNext {
                    sleepThread(startTime)
                }
                .doOnError {
                    sleepThread(startTime)
                }
                .setSchedulers()
                .safeSubscribeBy(
                        onNext = {
                            BaseApp.context.toast(R.string.grades_bottom_sheet_bind_success)
                            replaceBindFragmentToGPAFragment.postValue(true)
                            isAnimating = false
                        },
                        onError = {
                            //密码错误的话,会导致状态码为400，Retrofit无法回调onNext
                            //详见：https://www.cnblogs.com/fuyaozhishang/p/8607706.html
                            if (it is HttpException) {
                                val body = (it).response()?.errorBody() ?: return@safeSubscribeBy
                                val data = Gson().fromJson(body.string(), IdsStatus::class.java)
                                if (data.errorCode == ERROR) {
                                    replaceBindFragmentToGPAFragment.postValue(false)
                                    BaseApp.context.toast(R.string.grades_bottom_sheet_bind_fail)
                                }
                            } else {
                                replaceBindFragmentToGPAFragment.postValue(false)
                                BaseApp.context.toast("绑定ids失败")
                            }
                            isAnimating = false
                        }
                ).lifeCycle()

    }

    private fun sleepThread(startTime: Long) {
        val curTime = System.currentTimeMillis()
        val waitTime = 2000
        if (curTime - startTime < waitTime) {
            Thread.sleep(waitTime - curTime + startTime)
        }
    }

    val replaceBindFragmentToGPAFragment: MutableLiveData<Boolean> = MutableLiveData()

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
                            //未绑定的话,会导致状态码为400，Retrofit无法回调onNext
                            //详见：https://www.cnblogs.com/fuyaozhishang/p/8607706.html
                            if (it is HttpException) {
                                val errorBody = it.response()?.errorBody()?.string() ?: ""
                                val gpaStatus = Gson().fromJson(errorBody, GPAStatus::class.java)
                                _analyzeData.postValue(gpaStatus)
                            } else {
                                //此时说明是一些其他的错误
                                val s = "{\"errcode\":\"10010\",\"errmessage\":\"errCode:114514 errMsg: runtime error: index out of range [-1]\"}"
                                val gpaStatus = Gson().fromJson(s, GPAStatus::class.java)
                                _analyzeData.postValue(gpaStatus)

                                BaseApp.context.toast("加载绩点失败")
                            }

                        }
                ).lifeCycle()
    }

}