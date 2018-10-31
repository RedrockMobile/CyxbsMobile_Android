package com.mredrock.cyxbs.discover.schoolcar.widget

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.utils.overlay.SmoothMoveMarker
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.schoolcar.Interface.SchoolCarInterface
import com.mredrock.cyxbs.schoolcar.R

/**
 * Created by glossimar on 2018/9/12
 */

class SchoolCarMap(context: Context, savedInstanceState: Bundle?, carInterface: SchoolCarInterface) {
    private val TAG = "SchoolCarMap"

    lateinit var aMap: AMap
    var mapView: MapView? = null
    private var context: Context
    private var savedInstanceState: Bundle? = null
    private var carInterface: SchoolCarInterface
    private lateinit var locationStyle: MyLocationStyle
    private lateinit var smoothMarker: SmoothMoveMarker

    init {
        this.context = context
        if (savedInstanceState != null) {
            this.savedInstanceState = savedInstanceState
        }
        this.carInterface = carInterface
    }

    /**
     * 设置用户定位图标，获取位置的每次时间间隔，定位模式
     */
    fun initLocationType(){
        val descriptor: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_school_car_search_girl)
        locationStyle = MyLocationStyle()

        //        } else {
//            BitmapDescriptorFactory.fromResource(R.drawable.ic_school_car_search_boy)
//        }
        locationStyle.interval(2000)
        locationStyle.strokeWidth(0f)
        locationStyle.radiusFillColor(Color.alpha(0))
        locationStyle.myLocationIcon(descriptor)
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)
    }

    /**
     * 加载高德地图中的AMap
     */
    fun initAMap(ifLocation: Boolean){
        aMap = mapView!!.map

        if (aMap == null) {
            aMap = mapView!!.map
            Log.d(TAG, "initAMap: .........")
            if (mapView == null) {
            }
        }
        if (ifLocation) {
            aMap.isMyLocationEnabled = true
            aMap.myLocationStyle = locationStyle
        }

        aMap.uiSettings.isMyLocationButtonEnabled = false
        //地图开始时显示的中心和缩放大小
        val update = CameraUpdateFactory.newLatLngZoom(LatLng(29.531876, 106.606789), 17f)
        aMap.animateCamera(update)
        smoothMarker = SmoothMoveMarker(aMap)
    }

    /**
     * 显示高德地图在校车动画4s加载完成后
     */
    fun showMap(status: String?, layout: RelativeLayout, loadImage: ImageView){
        //接口数据返回200 时说明获取校车数据成功，并开始加入地图控件
        if (status == "200") {
            loadImage.visibility = View.INVISIBLE
            if (mapView == null) {
                mapView = MapView(context)
                layout.addView(mapView)
                mapView!!.onCreate(savedInstanceState)
            }

            //在地图上加入红岩和合作机构的图标，并设置点击监听
            val relativeLayout = RelativeLayout(context)
            val layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            layoutParams.leftMargin = 40
            layoutParams.topMargin = 0
            layoutParams.rightMargin = 25
            layout.addView(relativeLayout, layoutParams)

            val imageView = ImageView(context)
            imageView.setImageBitmap(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_school_car_search_orgnization_logo))
            imageView.setOnClickListener { v ->
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse("http://eini.cqupt.edu.cn/")
                context.startActivity(intent)
            }
            val lpLogo = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lpLogo.topMargin = 45
            relativeLayout.addView(imageView, lpLogo)

            initLocationType()
            aMap = mapView!!.map

            if (aMap == null) {
                aMap = mapView!!.map
                Log.d(TAG, "initAMap: .........")
                if (mapView == null) {
                }
            }

            //在地图上加入观察模式的切换"全校"<->"我"button
            carInterface.initLocationMapButton(aMap, locationStyle)
        } else {
            Toast.makeText(context, "校车暂时不在线哟～", Toast.LENGTH_SHORT).show()
        }
    }

    fun distroyMap(locationClient: AMapLocationClient?) {
        if (mapView != null) {
            mapView!!.onDestroy()
            locationClient?.onDestroy()
        }
    }

    fun pauseMap() {
        if (mapView != null) {
            mapView!!.onPause()
        }
    }

    fun resumeMap() {
        if (mapView != null) {
            mapView!!.onResume()
        }
    }
}