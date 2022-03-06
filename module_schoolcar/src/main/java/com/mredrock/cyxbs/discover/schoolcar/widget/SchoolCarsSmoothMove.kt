package com.mredrock.cyxbs.discover.schoolcar.widget

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import com.amap.api.maps.AMap
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.PolylineOptions
import com.amap.api.maps.utils.overlay.SmoothMoveMarker
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.discover.schoolcar.Interface.SchoolCarInterface
import com.mredrock.cyxbs.discover.schoolcar.SchoolCarActivity
import com.mredrock.cyxbs.discover.schoolcar.network.ApiService
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.activity_schoolcar.*
import okio.ByteString
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Created by glossimar on 2018/9/12
 */

class SchoolCarsSmoothMove(private val schoolCarActivity: SchoolCarActivity?, private val activity: Activity){
    private lateinit var carInterface: SchoolCarInterface

    //3辆校车的经纬度的List
    private var smoothMoveList1: MutableList<LatLng> = mutableListOf()
    private var smoothMoveList2: MutableList<LatLng> = mutableListOf()
    private var smoothMoveList3: MutableList<LatLng> = mutableListOf()
    var disposable: Disposable? = null
    /**
     * 进行接口数据请求
     */
    fun loadCarLocation(ifAddTimer: Long) {
        val apiService = ApiGenerator.getApiService(ApiService::class.java)

        disposable = apiService.schoolcar("Redrock", md5Hex(System.currentTimeMillis().toString().substring(0, 10) + "." + "Redrock"),
                System.currentTimeMillis().toString().substring(0, 10),
                md5Hex((System.currentTimeMillis() - 1).toString().substring(0, 10)))
                .setSchedulers()
                .safeSubscribeBy(onError = {
                    activity.tv_carStatus.text = "校车好像失联了..."
                },
                onNext =  {
                    carInterface.processLocationInfo(it, ifAddTimer)
                    val location = it.data
                    //现在最多三辆校车
                    when (location.size) {
                        0 -> {
                            activity.tv_carStatus.text = "校车好像失联了..."
                        }
                        1 -> {
                            smoothMoveList1.add(LatLng(location[0].lat, location[0].lon))
                        }
                        2 -> {
                            smoothMoveList1.add(LatLng(location[0].lat, location[0].lon))
                            smoothMoveList2.add(LatLng(location[1].lat, location[1].lon))
                        }

                        3 -> {
                            smoothMoveList1.add(LatLng(location[0].lat, location[0].lon))
                            smoothMoveList2.add(LatLng(location[1].lat, location[1].lon))
                            smoothMoveList3.add(LatLng(location[2].lat, location[2].lon))
                        }
                    }
                    activity.runOnUiThread {
                        if (!checkTimeBeforeEnter()) {
                            activity.tv_carStatus.text = "校车正在休息"
                        }
                    }

                }
                )

    }

    /**
     * 在地图上绘出校车轨迹
     */
    private fun drawTraceLine(aMap: AMap?, smoothMoveList: MutableList<LatLng>) {
        aMap!!.addPolyline(PolylineOptions()
                .addAll(smoothMoveList.subList(0, smoothMoveList.size - 1))
                .width(8f)
                .color(Color.argb(255, 93, 152, 255)))

    }



    /**
     * 校车平滑移动时调用
     */
    fun smoothMove(smoothMoveMarkers: MutableList<SmoothMoveMarker>, bitmapChanged: Bitmap) {
        if (smoothMoveList1.size > 0 || smoothMoveList2.size > 0) {
            val smoothMarker = SmoothMoveMarker(schoolCarActivity?.aMap)
            smoothMoveMarkers.add(smoothMarker)
            val carAmount = smoothMoveMarkers.size - 1
            smoothMoveMarkers[carAmount].setDescriptor(BitmapDescriptorFactory.fromBitmap(bitmapChanged))
            if (smoothMoveList2.size > 3 || smoothMoveList1.size > 3) {
                smoothMoveMarkers[carAmount].setPoints(getSmoothMoveList(carAmount).subList(getSmoothMoveList(carAmount).size - 3, getSmoothMoveList(carAmount).size - 1))
            } else {
                smoothMoveMarkers[carAmount].setPoints(getSmoothMoveList(carAmount).subList(getSmoothMoveList(carAmount).size - 1, getSmoothMoveList(carAmount).size - 1))
            }
            smoothMoveMarkers[carAmount].setTotalDuration(2)
            drawTraceLine(schoolCarActivity?.aMap, getSmoothMoveList(carAmount))
            smoothMoveMarkers[carAmount].startSmoothMove()
        }

    }


    fun setCarMapInterface(carMapInterface: SchoolCarInterface) {
        this.carInterface = carMapInterface
    }

    /**
     * 获取储存当前校车经纬度的List
     */
    private fun getSmoothMoveList(carID: Int): MutableList<LatLng> {
        var smoothMoveList: MutableList<LatLng> = mutableListOf()
        when (carID) {
            0 -> {
                smoothMoveList = smoothMoveList1
                return smoothMoveList
            }
            1 -> {
                smoothMoveList = smoothMoveList2
                return smoothMoveList
            }

            2 -> {
                smoothMoveList = smoothMoveList3
                return smoothMoveList
            }
        }
        return smoothMoveList
    }

    /**
     * 检查当前时间是否时校车运营时间（ 11:30AM-2PM , 5PM-9PM)
     */
    private fun checkTimeBeforeEnter(): Boolean {
        val calendar: Calendar = Calendar.getInstance()
        val hour: Int = calendar.get(Calendar.HOUR)
        val minute: Int = calendar.get(Calendar.MINUTE)
        val AM_PM: Int = calendar.get(Calendar.AM_PM)

        if (AM_PM == Calendar.AM && hour == 11) {
            if (minute >= 30) {
                return true
            }
        } else if (AM_PM == Calendar.AM && hour > 11 && hour < 2) {
            return true
        } else if (AM_PM == Calendar.PM && hour >= 5 && hour < 9) {
            return true
        }
        return false
    }

    fun clearAllList() {
        smoothMoveList3.clear()
        smoothMoveList2.clear()
        smoothMoveList1.clear()
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

}


