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
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.Exam
import com.mredrock.cyxbs.discover.grades.bean.IdsBean
import com.mredrock.cyxbs.discover.grades.bean.IdsStatus
import com.mredrock.cyxbs.discover.grades.bean.Status
import com.mredrock.cyxbs.discover.grades.bean.analyze.GPAStatus
import com.mredrock.cyxbs.discover.grades.network.ApiService
import io.reactivex.Observable
import retrofit2.HttpException

class ContainerViewModel : BaseViewModel() {
    companion object {
        const val STATUS_OK = "10000"
        const val ERROR = "10010"
    }

    //监听从bindactivity回来containerativity方便用户观看成绩则将bottom展开，如果是在本来在containeractivity则折叠
    val bottomStateListener = MutableLiveData<Boolean?>()

    //当前采取的考试展示策略
    val nowStatus = MutableLiveData<Status>()

    fun isContainerActivity() {
        bottomStateListener.postValue(false)
    }

    val examData = MutableLiveData<List<Exam>>()
    private val apiService = ApiGenerator.getApiService(ApiService::class.java)
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
                    apiService.getAnalyzeData()
                        .setSchedulers()
                        .safeSubscribeBy(
                            onNext = {
                                replaceBindFragmentToGPAFragment.value = true
                                bottomStateListener.postValue(true)
                                BaseApp.context.toast(R.string.grades_bottom_sheet_bind_success)
                                isAnimating = false
                            },
                            onError = {
                                replaceBindFragmentToGPAFragment.value = false
                                BaseApp.context.toast(R.string.grades_bottom_sheet_bind_wrong)
                                isAnimating = false
                            }
                        ).lifeCycle()
                },
                onError = {
                    //密码错误的话,会导致状态码为400，Retrofit无法回调onNext
                    //详见：https://www.cnblogs.com/fuyaozhishang/p/8607706.html
                    if (it is HttpException && it.code() == 400) {
                        val body = (it).response()?.errorBody() ?: return@safeSubscribeBy
                        val data = Gson().fromJson(body.string(), IdsStatus::class.java)
                        if (data.errorCode == ERROR) {
                            replaceBindFragmentToGPAFragment.value = false
                            BaseApp.context.toast(R.string.grades_bottom_sheet_bind_fail)
                        }
                    } else {
                        replaceBindFragmentToGPAFragment.value = false
                        BaseApp.context.toast("绑定ids失败")
                    }
                    isAnimating = false
                }
            ).lifeCycle()

    }

    /**
     * 解绑ids
     */
    fun unbindIds(onSuccess: () -> Unit) {
        apiService.unbindIds()
            .doOnNext {
            }
            .doOnError {
            }
            .setSchedulers()
            .safeSubscribeBy(
                onNext = {
                    bottomStateListener.postValue(true)
                    BaseApp.context.toast(R.string.grades_bottom_sheet_unbind_success)
                    onSuccess.invoke()
                    isBinding.value = false
                },
                onError = {
                    BaseApp.context.toast(R.string.grades_bottom_sheet_unbind_fail)
                }
            ).lifeCycle()
    }

    private fun sleepThread(startTime: Long) {
        val curTime = System.currentTimeMillis()
        val waitTime = 1500L
        if (curTime - startTime < waitTime) {
            Thread.sleep(waitTime - curTime + startTime)
        }
    }

    val replaceBindFragmentToGPAFragment: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * 当前是否处于绑定状态
     * 通过该LiveData改变按钮的点击事件
     */
    val isBinding = MutableLiveData<Boolean>()

    private val _analyzeData = MutableLiveData<GPAStatus>()
    val analyzeData: LiveData<GPAStatus>
        get() = _analyzeData

    fun getAnalyzeData() {
        apiService.getAnalyzeData()
            .setSchedulers()
            .safeSubscribeBy(
                onNext = {
                    _analyzeData.value = it
                    isBinding.value = true
                },
                onError = {
                    //未绑定的话,会导致状态码为400，Retrofit无法回调onNext
                    //详见：https://www.cnblogs.com/fuyaozhishang/p/8607706.html
                    if (it is HttpException) {
                        val errorBody = it.response()?.errorBody()?.string() ?: ""
                        try{
                            // 防止后端返回的status不符合json格式报错
                            val gpaStatus = Gson().fromJson(errorBody, GPAStatus::class.java)
                            _analyzeData.postValue(gpaStatus)
                            isBinding.value = false
                        }catch (e: Exception){
                            BaseApp.context.toast("加载绩点失败")
                        }
                    } else {
                        //此时说明是一些其他的错误
                        val s =
                            "{\"errcode\":\"10010\",\"errmessage\":\"errCode:114514 errMsg: runtime error: index out of range [-1]\"}"
                        val gpaStatus = Gson().fromJson(s, GPAStatus::class.java)
                        _analyzeData.postValue(gpaStatus)
                        isBinding.value = false
                        BaseApp.context.toast("加载绩点失败")
                    }
                }
            ).lifeCycle()
    }

    //获取当前采取的成绩展示方案
    fun getStatus() {
        apiService.getNowStatus()
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy {
                it?.data?.let { status ->
                    nowStatus.postValue(status)
                }
            }
    }

}