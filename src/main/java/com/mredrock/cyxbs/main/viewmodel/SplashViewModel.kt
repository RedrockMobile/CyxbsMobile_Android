package com.mredrock.cyxbs.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.QA_ANSWER_LIST
import com.mredrock.cyxbs.common.event.OpenShareQuestionEvent
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiException
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.main.bean.StartPage
import com.mredrock.cyxbs.main.network.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created By jay68 on 2018/8/10.
 */
class SplashViewModel : BaseViewModel() {
    val startPage: LiveData<StartPage?> = MutableLiveData()
    val finishModel = SingleLiveEvent<Boolean>()

    fun finishAfter(time: Long) {
        AndroidSchedulers.mainThread().scheduleDirect({ finishModel.value = true}, time, TimeUnit.MILLISECONDS)
    }

    fun getStartPage() {
        ApiGenerator.getApiService(ApiService::class.java)
                .getStartPage()
                .mapOrThrowApiException()
                .map {
                    it.forEach { startPage ->
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                        try {
                            val now = System.currentTimeMillis() / 1000
                            val date = dateFormat.parse(startPage.start)
                            if (Math.abs(date.time / 1000 - now) < 24 * 60 * 60) {
                                return@map startPage
                            }
                        } catch (e: Throwable) {
                            LogUtils.e("SplashViewModel", "parse time failed", e)
                        }
                    }
                    throw RedrockApiException("no start page found.")
                }
                .setSchedulers()
                .safeSubscribeBy { (startPage as MutableLiveData).value = it }
                .lifeCycle()
    }

    fun getQuestion(qid: String) {
        val u = BaseApp.user!!
        ApiGenerator.getApiService(ApiService::class.java)
                .getQuestion(u.stuNum!!, u.idNum!!, qid)
                .setSchedulers()
                .safeSubscribeBy {
                    EventBus.getDefault().postSticky(OpenShareQuestionEvent(it.string()))
                    finishModel.value = true
                }
                .lifeCycle()
    }
}