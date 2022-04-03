package com.mredrock.cyxbs.discover.schoolcar

import android.content.Context
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.discover.schoolcar.network.MapService
import okhttp3.ResponseBody
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MapStyleHelper(val context: Context) {
    private val mapDirRoot = File(context.filesDir, "/maoXhMap")
    private lateinit var style: File
    private lateinit var styleExtra: File
    private var oneFileReady = false


    fun saveMapStyle(callback: () -> Unit) {
        checkFile()
        ApiGenerator.registerNetSettings(SCHOOL_CAR_MAP_RES, retrofitConfig = { builder: Retrofit.Builder ->
            builder.baseUrl(getBaseUrl())
        },tokenNeeded = true)
        val apiService = ApiGenerator.getApiService(0, MapService::class.java)
        apiService.getMapRes("map_")
                .setSchedulers()
                .safeSubscribeBy(onError = {

                }) {
                    LogUtils.d("MapStyleHelper", "start save")
                    saveToDisk(it, style)
                    fileReady(callback)
                }
        apiService.getMapRes("map_")
                .setSchedulers()
                .safeSubscribeBy {
                    saveToDisk(it, styleExtra)
                    fileReady(callback)
                }
    }

    private fun saveToDisk(responseBody: ResponseBody, file: File) {
        try {
            val byte = responseBody.bytes()
            val os = FileOutputStream(file)
            os.write(byte)
        } catch (exception: IOException) {
            LogUtils.w(this.javaClass.simpleName, "io exception")
        }

    }

    private fun checkFile() {
        if (!mapDirRoot.exists()) mapDirRoot.mkdir()

        styleExtra = File(mapDirRoot, "style_extra.data")
        style = File(mapDirRoot, "style.data")
        if (!styleExtra.exists()) styleExtra.createNewFile()
        if (!style.exists()) style.createNewFile()
    }

    private fun fileReady(callback: () -> Unit) {
        if (oneFileReady) {
            context.defaultSharedPreferences.editor {
                putBoolean(IS_MAP_SAVED, true)
            }
            callback.invoke()
            return
        }
        synchronized(this) {
            if (oneFileReady) {
                callback.invoke()
                return
            } else {
                oneFileReady = true
            }
        }
    }

}