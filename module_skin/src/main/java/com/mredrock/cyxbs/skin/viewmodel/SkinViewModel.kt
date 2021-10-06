package com.mredrock.cyxbs.skin.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.skin.SkinManager
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.skin.bean.SkinInfo
import com.mredrock.cyxbs.skin.component.DownLoadManager
import com.mredrock.cyxbs.skin.model.SkinDataCache
import com.mredrock.cyxbs.skin.model.TestData
import com.mredrock.cyxbs.skin.network.SkinApiService

/**
 * Created by LinTong on 2021/9/18
 * Description:换肤的ViewModel
 */
class SkinViewModel : BaseViewModel() {
    private lateinit var skinApiService: SkinApiService

    //网络请求得到的数据，如何使用：在要使用的activity或者fragment观察即可
    val skinInfo = MutableLiveData<ArrayList<SkinInfo.Data>>()

    //网络请求失败，使用本地缓存
    val loadFail = MutableLiveData<Boolean>()
    fun init() {

        /**
         * 初始化网络请求
         */
        skinApiService = ApiGenerator.getApiService(SkinApiService::class.java)
        /**
         * 获得地图基本信息，地点坐标等
         */

//        skinApiService.getSkinInfo()//网络请求替换为：apiService.getMapInfo()
        skinApiService.getSkinInfo()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    //使用缓存数据
                    val skinInfoStore = SkinDataCache.getSkinInfo()
                    if (skinInfoStore != null) {
                        skinInfo.postValue((skinInfoStore as ArrayList<SkinInfo.Data>?)!!)
                    }
                    loadFail.postValue(true)
                    true
                }
                .safeSubscribeBy(
                        onNext = {
                            skinInfo.value = (it.data as ArrayList<SkinInfo.Data>?)!!
                            SkinDataCache.saveSkinInfo(it.data)
                        },
                        onError = { it.printStackTrace() }
                )

                .lifeCycle()
        skinInfo
    }

    fun downloadSkin(context: Context, url: String, id: Int) {
        val fileName = DownLoadManager.allSkinName[id] ?: ""
        DownLoadManager.download(context, url, fileName, object : DownLoadManager.OnDownloadListener {
            override fun onDownloadSuccess() {

            }

            override fun onDownloading(progress: Int) {

            }

            override fun onDownloadFailed() {

            }

        })
    }
}