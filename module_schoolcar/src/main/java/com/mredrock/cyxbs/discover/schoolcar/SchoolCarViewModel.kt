package com.mredrock.cyxbs.discover.schoolcar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.discover.schoolcar.bean.MapLines
import com.mredrock.cyxbs.discover.schoolcar.database.MapInfoDataBase
import com.mredrock.cyxbs.discover.schoolcar.network.ApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 *@Author:SnowOwlet
 *@Date:2022/5/6 20:06
 *
 */
class SchoolCarViewModel : ViewModel() {

  private val apiService by lazy { ApiGenerator.getApiService(ApiService::class.java) }

  //网络请求map信息
  private val _mapInfo = MutableLiveData<MapLines>()
  val mapInfo: LiveData<MapLines>
    get() = _mapInfo

  //bsb状态
  private val _bsbState = MutableLiveData<Int>()
  val bsbState: LiveData<Int>
    get() = _bsbState

  //当前线路信息
  private val _line = MutableLiveData(-1)
  val line: LiveData<Int>
    get() = _line

  //当前car选择的线路信息
  private val _carLine = MutableLiveData(-1)
  val carLine: LiveData<Int>
    get() = _carLine

  //当前map选择的线路信息
  private val _mapLine = MutableLiveData<ArrayList<Int>>(ArrayList())
  val mapLine: LiveData<ArrayList<Int>>
    get() = _mapLine

  //选择的站点
  private val _chooseSite = MutableLiveData<Int>()
  val chooseSite: LiveData<Int>
    get() = _chooseSite

  //是否显示最近站点
  private val _showRecently = MutableLiveData<Int>(0)
  val showRecently: LiveData<Int>
    get() = _showRecently

  fun chooseCar(line: Int) {
    _carLine.value = line
  }

  fun chooseSite(id: Int) {
    val arrayList = ArrayList<Int>()
    val chooseLine = _line.value ?: -1
    if (chooseLine != -1) {
      arrayList.add(chooseLine)
    }
    mapInfo.value?.lines?.forEach { line ->
      line.stations.forEach { station ->
        if (station.id == id) {
          if (chooseLine != line.id)
          arrayList.add(line.id)
          _chooseSite.value = station.id
        }
      }
    }
    _mapLine.value = arrayList
  }

  fun changeLine(type: Int = -1) {
    if (_line.value != type)
      _line.value = type
  }

  fun showRecently(){
    _showRecently.value= _showRecently.value?.plus(1)
  }

  fun recentlySite(lat:Double,lng:Double){
    var min = Float.MAX_VALUE
    var siteId = -1
    mapInfo.value?.lines?.forEach { line ->
      line.stations.forEach { station ->
        val recent = AMapUtils.calculateLineDistance(LatLng(lat,lng), LatLng(station.lat,station.lng))
        if (recent < min){
          min = recent
          siteId = station.id
        }
      }
    }
    if (siteId != -1){
      chooseSite(siteId)
    }
  }

  fun bsbHide(isDraggable:Boolean = false) {
    _bsbState.value = if (isDraggable) 1 else 2
  }

  fun bsbShow() {
    _bsbState.value = 0
  }

  fun initMapInfo() {
    MapInfoDataBase.INSTANCE.mapInfoDao().queryMapLines()
      .toObservable()
      .setSchedulers(observeOn = Schedulers.io())
      .safeSubscribeBy(
        onNext = { mapLines ->
          _mapInfo.postValue(mapLines)
        }
      )

    apiService.schoolSiteVersion()
      .setSchedulers(observeOn = Schedulers.io())
      .safeSubscribeBy(
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

  fun getMapLinesByNet(){
    apiService.schoolSite()
      .setSchedulers(observeOn = Schedulers.io())
      .safeSubscribeBy(
        onNext = { mapLines ->
          mapLines.data.let { res -> _mapInfo.postValue(res) }
          MapInfoDataBase.INSTANCE.mapInfoDao().insertMapLines(mapLines.data)
        },
        onError = {

        }
      )
  }
}