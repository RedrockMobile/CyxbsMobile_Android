package com.mredrock.cyxbs.discover.schoolcar.widget

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.util.ArrayMap
import com.amap.api.maps.model.*
import com.amap.api.maps.utils.overlay.MovingPointOverlay
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.discover.schoolcar.Interface.SchoolCarInterface
import com.mredrock.cyxbs.discover.schoolcar.SchoolCarActivity
import com.mredrock.cyxbs.discover.schoolcar.bean.SchoolCar
import com.mredrock.cyxbs.discover.schoolcar.network.ApiService
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.activity_schoolcar.*
import okio.ByteString
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.math.abs

/**
 * Created by glossimar on 2018/9/12
 */

class SchoolCarsSmoothMove(private val schoolCarActivity: SchoolCarActivity?, private val activity: Activity,private val bitmapChanges: List<Bitmap>){
    private lateinit var carInterface: SchoolCarInterface

    //key:id value:type
    private val carMap : ArrayMap<Int,SchoolCar> = ArrayMap()
    private val apiService = ApiGenerator.getApiService(ApiService::class.java)
    private var lastPostTime = System.currentTimeMillis()
    var disposable: Disposable? = null
    /**
     * 进行接口数据请求
     */
    fun loadCarLocation(ifAddTimer: Long) {
        disposable = apiService.schoolcar("Redrock",
                md5Hex(System.currentTimeMillis().toString().substring(0, 10) + "." + "Redrock"),
                System.currentTimeMillis().toString().substring(0, 10),
                md5Hex((System.currentTimeMillis() - 1).toString().substring(0, 10)))
                .setSchedulers()
                .safeSubscribeBy(onError = {
                    activity.tv_carStatus.text = "校车好像失联了..."
                },
                onNext =  {
                    activity.tv_carStatus.text = "校车运行中"
                    lastPostTime = System.currentTimeMillis()
                        //如果所有的数据都没有更新
                        it.data.data.forEach {  data ->
                            if(!carMap.contains(data.id)){
                                val latLng = LatLng(data.lat,data.lon)
                                val marker = buildMarker(data.id,data.type,latLng)
                                marker?.let { marker ->
                                    val smoothMarker = MovingPointOverlay(schoolCarActivity?.aMap,marker)
                                    carMap[data.id] = SchoolCar(data.id,data.type,data.upDate,data.upDate,smoothMarker,marker,mutableListOf(latLng,smoothMarker.position),getPolyline(data.type,latLng))
                                }
                            }else{
                                //这里因为轮询是1s一次，所以说判断一下更新的时间，
                                if(carMap[data.id]?.upDate != data.upDate){
                                    carMap[data.id]?.upDate?.let { upDate ->
                                        //将两次更新的时间差作为移动的时间
                                        var mistime = upDate - data.upDate
                                        carMap[data.id]?.upDate = data.upDate
                                        carMap[data.id]?.latLngList?.add(LatLng(data.lat,data.lon))
                                        mistime = abs(mistime)
                                        mistime = if(mistime < 3) 3 else if( mistime > 15) 15 else mistime
                                        carMap[data.id]?.let { car -> smoothMove(car,abs(mistime.toInt())) }
                                    }
                                }
                            }
                        }
                        carInterface.processLocationInfo(it, ifAddTimer)
                        activity.runOnUiThread {
                            if (it.data.data.isEmpty()) {
                                activity.tv_carStatus.text = "校车正在休息"
                            }
                        }
                }
            )
    }

    /**
     * 在地图上绘出校车轨迹
     */
    private fun drawTraceLine(car: SchoolCar) {
        car.polyline?.points = car.latLngList
    }

    private fun smoothMove(car:SchoolCar, mistime:Int = 3) {
        if (car.latLngList.size > 3) {
            //停止上一次的移动，然后将上一次的位置加上之后再移动
            car.smoothMarker.stopMove()
            val list = car.latLngList.subList(car.latLngList.size - 2, car.latLngList.size)
            list[0] = car.smoothMarker.position
            val smoothMarker = MovingPointOverlay(schoolCarActivity?.aMap,car.marker)
            smoothMarker.setPoints(list)
            smoothMarker.setTotalDuration(mistime)
            smoothMarker.startSmoothMove()
            drawTraceLine(car)
            car.smoothMarker = smoothMarker
        }
        //如果线路过长就删除一些
        if (car.latLngList.size > 8){
            car.latLngList.removeFirstOrNull()
            car.latLngList.removeFirstOrNull()
        }
    }

    fun setCarMapInterface(carMapInterface: SchoolCarInterface) {
        this.carInterface = carMapInterface
    }

    /**
     * 检查当前时间是否时校车运营时间
     */
    private fun checkTimeBeforeEnter(type:Int): Boolean {
        val calendar: Calendar = Calendar.getInstance()
        val hour: Int = calendar.get(Calendar.HOUR)
        val minute: Int = calendar.get(Calendar.MINUTE)
        val AM_PM: Int = calendar.get(Calendar.AM_PM)
        return when(type){
            0,1 -> {
                if (AM_PM == Calendar.AM && hour == 7) {
                    minute >= 30
                } else if (AM_PM == Calendar.AM && hour > 7) {
                    true
                } else if(AM_PM == Calendar.PM && hour < 10){
                    true
                } else if(AM_PM == Calendar.PM && hour == 10){
                    minute <= 30
                } else {
                    false
                }
            }
            2 -> {
                if (AM_PM == Calendar.AM && hour == 9) {
                    minute >= 30
                } else if (AM_PM == Calendar.AM && hour > 9) {
                    true
                } else if(AM_PM == Calendar.PM && hour == 1){
                    minute >= 30
                } else AM_PM == Calendar.PM && hour < 6 && hour > 1
            }
            3 -> {
                AM_PM == Calendar.AM && hour >= 8 && hour <= 10
            }
            else -> false
        }
        return false
    }

    private fun buildMarker(id:Int,type:Int,latLng:LatLng): Marker? {
        val markerOption = MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmapChanges[type]))
        markerOption
            .title("${id}号车")
            .snippet("${type+1}号线路")
            .anchor(0.5f,0.5f)
            .position(latLng)
        return schoolCarActivity?.aMap?.addMarker(markerOption)
    }

    fun clearAllList() {
        carMap.iterator().forEach {
            it.value.marker.remove()
            it.value.polyline?.remove()
        }
        carMap.clear()

    }

    private fun md5Hex(s: String): String {
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            val md5bytes = messageDigest.digest(s.toByteArray(charset("UTF-8")))
            return ByteString.of(*md5bytes).hex()
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError(e)
        } catch (e: UnsupportedEncodingException) {
            throw AssertionError(e)
        }
    }
    private fun getPolyline(type: Int, latLng: LatLng): Polyline? {
        return when(type){
            0->{
                schoolCarActivity?.aMap?.addPolyline(PolylineOptions()
                    .setDottedLine(true)
                    .width(8f)
                    .color(Color.argb(255, 93, 152, 255))
                    .add(latLng))
            }
            1->{
                schoolCarActivity?.aMap?.addPolyline(PolylineOptions()
                    .setDottedLine(true)
                    .width(8f)
                    .color(Color.argb(255, 93, 152, 255))
                    .add(latLng))
            }
            2->{
                schoolCarActivity?.aMap?.addPolyline(PolylineOptions()
                    .setDottedLine(true)
                    .width(8f)
                    .color(Color.argb(255, 93, 152, 255))
                    .add(latLng))
            }
            3->{
                schoolCarActivity?.aMap?.addPolyline(PolylineOptions()
                    .setDottedLine(true)
                    .width(8f)
                    .color(Color.argb(255, 93, 152, 255))
                    .add(latLng))
            }
            else ->{
                schoolCarActivity?.aMap?.addPolyline(PolylineOptions()
                    .setDottedLine(true)
                    .width(8f)
                    .color(Color.argb(255, 93, 152, 255))
                    .add(latLng))
            }
        }
    }


}


