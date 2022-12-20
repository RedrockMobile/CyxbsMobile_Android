package com.mredrock.cyxbs.discover.schoolcar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.discover.schoolcar.bean.MapLines
import com.mredrock.cyxbs.discover.schoolcar.database.MapInfoDataBase
import com.mredrock.cyxbs.discover.schoolcar.network.ApiService
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 *@Author:SnowOwlet
 *@Date:2022/5/11 21:28
 *
 */
class SchoolDetailViewModel : ViewModel() {

  private val apiService = ApiGenerator.getApiService(ApiService::class.java)
  private val _mapInfo = MutableLiveData<MapLines>()
  val mapInfo: LiveData<MapLines>
    get() = _mapInfo

  fun initMapInfo() {
    MapInfoDataBase.INSTANCE.mapInfoDao().queryMapLines()
      .toObservable()
      .setSchedulers(observeOn = Schedulers.io())
      .unsafeSubscribeBy(
        onNext = { mapLines ->
          _mapInfo.postValue(mapLines)
        }
      )
    //拿取车站等信息版本号
    apiService.schoolSiteVersion()
      .setSchedulers(observeOn = Schedulers.io())
      .unsafeSubscribeBy(
        onNext = { version ->
          MapInfoDataBase.INSTANCE.mapInfoDao().queryMapLines()
            .subscribeOn(Schedulers.io())
            .subscribe(
              {
                if (it.version != version.data.version) {
                  getMapLinesByNet()
                }
              },{
                getMapLinesByNet()
              })
        }
      )
  }

  private fun getMapLinesByNet(){
    apiService.schoolSite()
      .setSchedulers(observeOn = Schedulers.io())
      .unsafeSubscribeBy(
        onNext = { mapLines ->
          mapLines.data.let { res -> _mapInfo.postValue(res) }
          MapInfoDataBase.INSTANCE.mapInfoDao().insertMapLines(mapLines.data)
        },
        onError = {

        }
      )
  }

}