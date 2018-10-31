package com.mredrock.cyxbs.news.viewmodel

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.news.bean.NewsAttachment
import com.mredrock.cyxbs.news.bean.NewsDetails
import com.mredrock.cyxbs.news.network.ApiService
import com.mredrock.cyxbs.news.network.download.DownloadManager
import com.mredrock.cyxbs.news.network.download.RedDownloadListener
import com.tbruyelle.rxpermissions2.RxPermissions
import java.io.File

/**
 * Author: Hosigus
 * Date: 2018/9/25 13:24
 * Description: 控制数据交互，包括下载
 */
class NewsItemViewModel : BaseViewModel() {
    val news: LiveData<NewsDetails> = MutableLiveData()

    fun getNews(id: Int) {
        ApiGenerator.getApiService(ApiService::class.java)
                .getNewsDetails(id)
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    (news as MutableLiveData).value = it
                }.dispose()
/* 模拟数据       (news as MutableLiveData).value = Gson().fromJson("{\n" +
                "           \"title\": \"2017级学生转专业录取公示\",\n" +
                "           \"pubTime\": \"2018年9月21日\",\n" +
                "           \"teaName\": \"杨艳\",\n" +
                "           \"readCount\": 472,\n" +
                "           \"content\": \" (见附件) \\n\",\n" +
                "           \"urlData\": [\n" +
                "               {\n" +
                "                   \"url\": \"fileDownLoadAttach.php?id=6567\",\n" +
                "                   \"urlname\": \"转专业录取公示(753次下载)\"\n" +
                "               }\n" +
                "           ]\n" +
                "       }", NewsDetails::class.java)*/
    }

    fun download(rxPermissions: RxPermissions, list: List<NewsAttachment>, listener: NewsDownloadListener) {
        checkPermission(rxPermissions) { isGranted ->
            if (isGranted) {
                listener.onDownloadStart()
                list.forEachIndexed { pos, it ->
                    DownloadManager.download(object : RedDownloadListener {
                        override fun onDownloadStart() {}
                        override fun onProgress(currentBytes: Long, contentLength: Long) {
                            listener.onProgress(pos, currentBytes, contentLength)
                        }

                        override fun onSuccess(file: File) {
                            listener.onDownloadEnd(pos, file)
                        }

                        override fun onFail(e: Throwable) {
                            listener.onDownloadEnd(pos, e = e)
                        }
                    }, it.url, it.name)
                }
            } else {
                listener.onDownloadEnd(-1, e = Exception("permission deny"))
            }
        }
    }

    private fun checkPermission(rxPermissions: RxPermissions, result: (Boolean) -> Unit) {
        rxPermissions.request(WRITE_EXTERNAL_STORAGE).subscribe(result).lifeCycle()
    }

    interface NewsDownloadListener {
        fun onDownloadStart()
        fun onProgress(id: Int, currentBytes: Long, contentLength: Long)
        fun onDownloadEnd(id: Int, file: File? = null, e: Throwable? = null)
    }
}