package com.mredrock.cyxbs.main.viewmodel

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiException
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.main.bean.StartPage
import com.mredrock.cyxbs.main.network.ApiService
import com.mredrock.cyxbs.main.ui.MainActivity
import com.mredrock.cyxbs.main.utils.deleteDir
import com.mredrock.cyxbs.main.utils.downloadSplash
import com.mredrock.cyxbs.main.utils.getSplashFile
import com.mredrock.cyxbs.main.utils.isDownloadSplash
import com.tencent.bugly.Bugly.applicationContext
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : BaseViewModel() {

    val startPage: LiveData<StartPage?> = MutableLiveData()

    val splashVisibility = ObservableField(View.GONE)

    //进入app是否直接显示课表
    var isCourseDirectShow = false

    fun getStartPage() {
        ApiGenerator.getCommonApiService(ApiService::class.java)
                .getStartPage()
                .mapOrThrowApiException()
                .map {
                    it.forEach { startPage ->
                        //这儿的逻辑是只闪屏页显示一天，如果超了一天此处就不会下载，通过后端返回空的url，删除本地存储的闪屏页
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                        try {
                            val now = System.currentTimeMillis() / 1000
                            val date = dateFormat.parse(startPage.start)
                            val gap = now - date.time / 1000
                            if (gap < 24 * 60 * 60 && gap > 0) {
                                return@map startPage
                            }
                        } catch (e: Throwable) {
                            LogUtils.e("SplashViewModel", "parse time failed", e)
                        }
                    }
                    throw RedrockApiException("no start page found.")
                }
                .setSchedulers()
                .safeSubscribeBy(onError = {
                    (startPage as MutableLiveData).value = null
                }, onNext = {
                    (startPage as MutableLiveData).value = it
                })
                .lifeCycle()
    }


    fun initStartPage(starPage: StartPage?) {
        if (starPage != null) {
            val src = starPage.photo_src
            if (src != null && src.startsWith("http")) {//如果不为空，且url有效
                //对比缓存的url是否一样
                if (src != applicationContext.sharedPreferences("splash").getString(MainActivity.SPLASH_PHOTO_NAME, "#")) {
                    deleteDir(getSplashFile(context))
                    downloadSplash(src, context)
                    applicationContext.sharedPreferences("splash").editor {
                        putString(MainActivity.SPLASH_PHOTO_NAME, src)
                    }
                }
            } else { //src非法
                deleteSplash()
            }
        } else { //不显示图片的时候
            deleteSplash()
        }
    }

    private fun deleteSplash() {
        if (isDownloadSplash(context)) {//如果url为空，则删除之前下载的图片
            deleteDir(getSplashFile(context))
        }
    }


}