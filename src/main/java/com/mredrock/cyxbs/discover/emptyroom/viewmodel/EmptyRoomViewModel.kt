package com.mredrock.cyxbs.discover.emptyroom.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.emptyroom.bean.EmptyRoom
import com.mredrock.cyxbs.discover.emptyroom.network.ApiService
import com.mredrock.cyxbs.discover.emptyroom.utils.EmptyConverter
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


/**
 * Created by Cynthia on 2018/9/21
 */

const val DEFAULT = 1
const val LOADING = 2
const val FINISH = 3

class EmptyRoomViewModel : BaseViewModel() {
    var rooms: MutableLiveData<List<EmptyRoom>> = MutableLiveData()
    var status: MutableLiveData<Int> = MutableLiveData()

    private var d: Disposable? = null

    private val apiService = ApiGenerator.getApiService(ApiService::class.java)

    init {
        status.value = DEFAULT
    }

    fun getData(week: Int, weekday: Int, building: Int, section: IntArray) {
        val tag = section.joinToString(",", " ", " ").replace(" ", "")
        if (section.isEmpty()) {
            status.value = DEFAULT
            return
        }
        status.value = LOADING
        d?.dispose()
        d = apiService.getEmptyRooms(week, weekday, building, tag)
                .delay(300, TimeUnit.MILLISECONDS)
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy{
                    status.value = FINISH
                    val converter = EmptyConverter()
                    converter.setEmptyData(it)
                    rooms.value = converter.convert()
                }
        d?.lifeCycle()
    }
}