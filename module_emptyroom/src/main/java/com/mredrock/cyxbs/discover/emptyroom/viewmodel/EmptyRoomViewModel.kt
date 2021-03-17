package com.mredrock.cyxbs.discover.emptyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.emptyroom.bean.EmptyClassRoom
import com.mredrock.cyxbs.discover.emptyroom.bean.EmptyRoom
import com.mredrock.cyxbs.discover.emptyroom.network.ApiService
import com.mredrock.cyxbs.discover.emptyroom.utils.EmptyConverter
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


/**
 * Created by Cynthia on 2018/9/21
 */

class EmptyRoomViewModel : BaseViewModel() {
    var rooms: MutableLiveData<List<EmptyClassRoom>> = MutableLiveData()
    var status: MutableLiveData<Int> = MutableLiveData()

    private var d: Disposable? = null

    private val apiService = ApiGenerator.getApiService(ApiService::class.java)

    companion object {
        const val DEFAULT = 1
        const val LOADING = 2
        const val FINISH = 3
        const val ERROR = 4
    }

    init {
        status.value = DEFAULT
    }

    fun getData(week: Int, weekday: Int, building: Int, section: List<Int>) {
        val tag = section.joinToString(",", " ", " ").replace(" ", "")
        LogUtils.d("RayleighZ", "section = $section, tag = $tag")
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
                .safeSubscribeBy(
                        onNext = {
                            status.value = FINISH
//                            val converter = EmptyConverter()
//                            converter.setEmptyData(it)
                            rooms.value = it
                        },
                        onError = {
                            status.value = ERROR
                        })
        d?.lifeCycle()
    }
}