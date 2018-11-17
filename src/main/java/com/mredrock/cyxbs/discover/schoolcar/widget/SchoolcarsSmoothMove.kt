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
import com.mredrock.cyxbs.discover.schoolcar.SchoolCarActivity.Companion.TIME_OUT
import com.mredrock.cyxbs.discover.schoolcar.network.ApiService
import okio.ByteString
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Created by glossimar on 2018/9/12
 */

class SchoolcarsSmoothMove(schoolCarMap: SchoolCarMap?, activity: Activity){
    private var carAngle: Double = 0.toDouble()

    private val activity : Activity
    private val schoolCarMap: SchoolCarMap

    private lateinit var carInterface: SchoolCarInterface

    private var checkTimeList : Boolean = true              //是否需要检查当前时间是否为校车运营时间
    private lateinit var timeList: MutableList<String>      //储存接口每次返回的校车最后运行时间的List

    //3辆校车的经纬度的List
    private var smoothMoveList1: MutableList<LatLng>
    private var smoothMoveList2: MutableList<LatLng>
    private var smoothMoveList3: MutableList<LatLng>

    init {
        this.schoolCarMap = schoolCarMap!!
        this.activity = activity
        timeList = mutableListOf()
        smoothMoveList1 = mutableListOf()
        smoothMoveList2 = mutableListOf()
        smoothMoveList3 = mutableListOf()
    }

    /**
     * 进行接口数据请求
     */
    fun loadCarLocation(ifAddTimer : Long){
        val apiService = ApiGenerator.getApiService(ApiService::class.java)

        apiService.schoolcar("Redrock", md5Hex(System.currentTimeMillis().toString().substring(0, 10) + "." + "Redrock"),
                System.currentTimeMillis().toString().substring(0, 10),
                md5Hex((System.currentTimeMillis() - 1).toString().substring(0, 10)))
                .setSchedulers()
                .safeSubscribeBy {
                    carInterface.processLocationInfo(it, ifAddTimer!!)

                    val location = it.data
                    smoothMoveList1.add(LatLng(location[0].lat, location[0].lon))
                    smoothMoveList2.add(LatLng(location[1].lat, location[1].lon))
                    smoothMoveList3.add(LatLng(location[2].lat, location[2].lon))

                    //当timeList的size大于3时将会对运营时间（11-2 && 5-10）和从接口数据返回的时间来双重检查是否用户可以进入地图
                    // 或者显示当前TIME_OUT的dialog
                    if (checkTimeList){
                        if (timeList.size < 3) {
                            timeList.add(it.time)
                        } else {
                            checkTimeList = false
                            activity.runOnUiThread {
                                checkBeforeEnter(activity)
                            }
                        }
                    }
                }
    }

    /**
     * 在地图上绘出校车轨迹
     */
    private fun drawTraceLine(aMap: AMap?, smoothMoveList: MutableList<LatLng>){
        val polyline = aMap!!.addPolyline(PolylineOptions()
                                      .addAll(smoothMoveList.subList(0, smoothMoveList.size - 1))
                                                            .width(8f)
                                                            .color(Color.argb(255, 93, 152, 255)))

    }

    /**
     * 更改校车车头角度（校车拐弯）
     */
    private fun changeCarOrientation(marker: SmoothMoveMarker, latlng1: LatLng, latlng2: LatLng, errorRange: Double){
        if (!(latlng1.latitude == latlng2.latitude && latlng1.longitude == latlng2.longitude)) {
            val nextOrientation = getNextOrientation(latlng1, latlng2)
            val currentAngle = carAngle
            if (Math.abs(nextOrientation - currentAngle) > errorRange) {
                carAngle = nextOrientation
                val computAngle = computRotateAngle(currentAngle, nextOrientation).toFloat()
                if (marker.marker != null) {
                    val makerLocal = marker.marker
                    makerLocal.rotateAngle = marker.marker.rotateAngle + computAngle
                }
            }
        }
    }

    /**
     * 校车平滑移动时调用
     */
    fun smoothMove(smoothMoveMarkers: MutableList<SmoothMoveMarker>, bitmapChanged: Bitmap){
        if (smoothMoveList1.size > 0 || smoothMoveList2.size > 0) {
            val smoothMarker = SmoothMoveMarker(schoolCarMap.aMap)
            smoothMoveMarkers.add(smoothMarker)
            val carAmount = smoothMoveMarkers.size - 1
            smoothMoveMarkers[carAmount].setDescriptor(BitmapDescriptorFactory.fromBitmap(bitmapChanged))
            if (smoothMoveList2.size > 3 || smoothMoveList1.size > 3) {
                changeCarOrientation(smoothMoveMarkers[carAmount], getSmoothMoveList(carAmount)[getSmoothMoveList(carAmount).size - 3], getSmoothMoveList(carAmount)[getSmoothMoveList(carAmount).size - 2], 2.0)
                smoothMoveMarkers[carAmount].setPoints(getSmoothMoveList(carAmount).subList(getSmoothMoveList(carAmount).size - 3, getSmoothMoveList(carAmount).size - 1))
            } else {
//                changeCarOrientation(smoothMoveMarkers[carAmount], getSmoothMoveList(carAmount)[getSmoothMoveList(carAmount).size - 1], getSmoothMoveList(carAmount)[getSmoothMoveList(carAmount).size - 1], 2.0)
                smoothMoveMarkers[carAmount].setPoints(getSmoothMoveList(carAmount).subList(getSmoothMoveList(carAmount).size - 1, getSmoothMoveList(carAmount).size - 1))
            }
            smoothMoveMarkers[carAmount].setTotalDuration(2)
            drawTraceLine(schoolCarMap.aMap, getSmoothMoveList(carAmount))
            smoothMoveMarkers[carAmount].startSmoothMove()
        }

    }

    /**
     * 获取校车车头转向角度算法
     */
    private fun getNextOrientation(latlng1: LatLng, latlng2: LatLng): Double{
        var angle: Double
        val a: Double
        val b: Double
        val c: Double
        val a1: Double = latlng2.latitude - latlng1.latitude
        val b1: Double = latlng2.longitude - latlng1.longitude

        val latitudeDif = Math.abs(a1)
        val longitudeDif = Math.abs(b1)
        a = latitudeDif
        b = longitudeDif
        c = Math.sqrt(Math.pow(a, 2.0) + Math.pow(b, 2.0))
        if (a == 0.0) {
            angle = if (latlng2.longitude - latlng1.longitude < 0) 180.0 else 0.0
        } else if (b == 0.0) {
            angle = if (latlng2.latitude - latlng1.latitude < 0) 270.0 else 90.0
        } else {
            angle = Math.toDegrees(Math.acos(b / c))
            if (a1 < 0 && b1 > 0)  angle = 360 - angle
            else if (a1 > 0 && b1 > 0)
            else if (a1 > 0 && b1 < 0) angle = 180 - angle
            else if (a1 < 0 && b1 < 0) angle += 180
        }
        return angle
    }

    /**
     * 计算RotateAngle
     */
    private fun computRotateAngle(currentAngle: Double, nextAngle: Double): Double {
        return nextAngle - currentAngle

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
     * 时间和接口中校车最后运行时间的双重检查，用户当前是否应该直接进入地图还是显示Dialog->TIME_OUT
     */
    private fun checkBeforeEnter(activity: Activity): Boolean {
        //如果timeList里最后一次校车运行时间和第一次获取到的校车运行时间不一样则说明此时校车正在运行，一样则校车此时未运行
       if (checkTimeBeforeEnter() || (timeList.size <= 1 || timeList[timeList.size - 1] == timeList[0])) {
            timeList = mutableListOf("")
           ExploreSchoolCarDialog.show(activity, TIME_OUT)
            return false    // 校车未运行
        }
        timeList.clear()
        return true         // 校车正在运行
    }

    /**
     * 检查当前时间是否时校车运营时间（ 11AM-2PM , 5PM-10PM)
     */
    private fun checkTimeBeforeEnter(): Boolean{
        val calendar: Calendar = Calendar.getInstance()
        val hour: Int = calendar.get(Calendar.HOUR)
        val AM_PM: Int = calendar.get(Calendar.AM_PM)

        if ((AM_PM == Calendar.AM && hour >= 11) || AM_PM == Calendar.PM && hour <= 11) {
            return false    //时间为校车正在运行时间，返回false
        }
        return true         //时间为校车未运行时间，返回true
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


