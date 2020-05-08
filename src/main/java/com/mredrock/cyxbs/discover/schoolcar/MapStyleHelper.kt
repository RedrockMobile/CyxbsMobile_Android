package com.mredrock.cyxbs.discover.schoolcar

import android.content.Context
import android.os.Environment
import com.mredrock.cyxbs.common.BuildConfig
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.discover.schoolcar.network.MapService
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.util.concurrent.TimeUnit

class MapStyleHelper(val context: Context) {
    private val mapDirRoot = File(context.filesDir, "/maoXhMap")
    private lateinit var style:File
    private lateinit var styleExtra:File
    private var oneFileReady = false
    private val mapRetrofit by lazy { Retrofit.Builder()
            .client(OkHttpClient().newBuilder().apply {
                connectTimeout(30.toLong(), TimeUnit.SECONDS)
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.level = HttpLoggingInterceptor.Level.BODY
                    addInterceptor(logging)
                }
            }.build())
            .baseUrl("http://api-234.redrock.team")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build() }
    fun saveMapStyle(callback: () -> Unit){
        checkFile()
        val apiService = ApiGenerator.getApiService(mapRetrofit,MapService::class.java)
        apiService.getMapRes("map_A")
                .setSchedulers()
                .safeSubscribeBy (onError = {

                }){
                    LogUtils.d("MapStyleHelper","start save")
                    saveToDisk(it,style)
                    fileReady(callback)
                }
        apiService.getMapRes("map_B")
                .setSchedulers()
                .safeSubscribeBy {
                    saveToDisk(it,styleExtra)
                    fileReady(callback)
                }
    }
    private fun saveToDisk(responseBody: ResponseBody,file:File){
        try {
            val byte = responseBody.bytes()
            val os = FileOutputStream(file)
            os.write(byte)
        }catch (exception:IOException){
            LogUtils.w(this.javaClass.simpleName,"io exception")
        }

    }
    private fun checkFile(){
        if(!mapDirRoot.exists())mapDirRoot.mkdir()

        styleExtra = File(mapDirRoot,"style_extra.data")
        style = File(mapDirRoot,"style.data")
        if(!styleExtra.exists())styleExtra.createNewFile()
        if(!style.exists())style.createNewFile()
    }
    private fun fileReady(callback:()->Unit){
        if(oneFileReady){
            context.defaultSharedPreferences.editor {
                putBoolean(IS_MAP_SAVED,true)
            }
            callback.invoke()
            return
        }
        synchronized(this){
            if(oneFileReady){
                callback.invoke()
                return
            }else{
                oneFileReady = true
            }
        }
    }

}