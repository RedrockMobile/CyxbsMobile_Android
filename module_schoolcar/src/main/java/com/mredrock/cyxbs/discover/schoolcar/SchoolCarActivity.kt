package com.mredrock.cyxbs.discover.schoolcar

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapsInitializer.updatePrivacyAgree
import com.amap.api.maps.MapsInitializer.updatePrivacyShow
import com.amap.api.maps.model.*
import com.amap.api.maps.utils.overlay.MovingPointOverlay
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.config.DISCOVER_SCHOOL_CAR
import com.mredrock.cyxbs.common.config.END_POINT_REDROCK_PROD
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.discover.schoolcar.Interface.SchoolCarInterface
import com.mredrock.cyxbs.discover.schoolcar.bean.SchoolCarLocation
import com.mredrock.cyxbs.discover.schoolcar.widget.ExploreSchoolCarDialog
import com.mredrock.cyxbs.discover.schoolcar.widget.SchoolCarsSmoothMove
import com.mredrock.cyxbs.schoolcar.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.activity_schoolcar.*
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by glossimar on 2018/9/12
 */
const val IS_MAP_SAVED = "isMapSaved"
@Route(path = DISCOVER_SCHOOL_CAR)
class SchoolCarActivity : BaseActivity(), View.OnClickListener {
    companion object {
        const val TAG: String = "ExploreSchoolCar"
        const val EXPLORE_SCHOOL_CAR_URL: String = "$END_POINT_REDROCK_PROD/app/schoolbus/load.gif"
        const val TIME_OUT: Int = 1
        const val LOST_SERVICES: Int = 2
        const val NO_GPS: Int = 3

        const val WHOLE_SCHOOL: Int = 0
        const val ME: Int = 1
        const val SCHOOL_CAR: Int = 2

        const val ADD_TIMER: Long = 3
        const val ADD_TIMER_AND_SHOW_MAP: Long = 55
        const val NOT_ADD_TIMER: Long = 0
    }

    var ifLocation = true
    private var showCarIcon = false
    private var savedInstanceState: Bundle? = null
    private lateinit var makerBitmaps: List<Bitmap>
    private var smoothMoveData: SchoolCarsSmoothMove? = null
    private lateinit var locationClient: AMapLocationClient
    private var disposable: Disposable? = null
    private lateinit var dataList: List<SchoolCarLocation.Data>
    lateinit var aMap: AMap
    private lateinit var locationStyle: MyLocationStyle


    //申请权限
    private val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
            )
    private val refusePermissions = mutableListOf<String>()
    private val mRequestCode = 200


    override fun onClick(v: View?) {
        when (v) {
            cv_out -> {
                val update = CameraUpdateFactory.zoomOut()
                aMap.animateCamera(update)
            }
            cv_expand -> {
                val update = CameraUpdateFactory.zoomIn()
                aMap.animateCamera(update)
            }
            cv_positioning -> {
                val update =
                if (aMap.myLocation.longitude != 0.0 || aMap.myLocation.latitude !== 0.0){
                     CameraUpdateFactory.newLatLngZoom(LatLng(aMap.myLocation.latitude, aMap.myLocation.longitude), 17f)
                }else{
                    CameraUpdateFactory.newLatLngZoom(LatLng(29.531876, 106.606789), 17f)
                }
                aMap.animateCamera(update)
            }
            iv_cooperation_logo -> {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse("https://redrock.team/")
                startActivity(intent)
            }
            iv_back -> {
                finishAfterTransition()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        setContentView(R.layout.activity_schoolcar)
        checkActivityPermission()
//        if (checkActivityPermission()) {
        //这里是用户协议申明同意
        updatePrivacyShow(this, true, true)
        updatePrivacyAgree(this,true)
            locationClient = AMapLocationClient(applicationContext)
            initView()
//        }
    }


    /**
     * 获取smoothMoveData、makerBitmap的实例, toolbar，开启加载动画
     * 在initSchoolcarMap()中调用 -> 在初始化了schoolCarMap实例后
     */
    private fun initView() {
        mv_map.onCreate(savedInstanceState)
        aMap = mv_map.map
        cv_out.setOnClickListener(this)
        cv_expand.setOnClickListener(this)
        cv_positioning.setOnClickListener(this)
        iv_cooperation_logo.setOnClickListener(this)
        iv_back.setOnClickListener(this)
        jc_schoolCar.setShadow(false, false, false, false)
        //如果用户同意定位权限，则开启定位和初始化定位用到的类
        if (ifLocation) {
            LogUtils.d(TAG, "schoolCarMap.initLocationType")
            initLocationType()
            initData()
        }
        //初始化地图配置
        initAMap(ifLocation)
        makerBitmaps = getSmoothMakerBitmaps()
        smoothMoveData = SchoolCarsSmoothMove(this, this@SchoolCarActivity,makerBitmaps)
        smoothMoveData!!.setCarMapInterface(object : SchoolCarInterface {
            // 回调是否显示地图，和是否开启一个timer轮询接口
            override fun processLocationInfo(carLocationInfo: RedrockApiWrapper<SchoolCarLocation>, aLong: Long) {
                dataList = carLocationInfo.data.data
                if (dataList[0] == null) {
                    ExploreSchoolCarDialog.show(this@SchoolCarActivity, LOST_SERVICES)
                    if (disposable != null) disposable!!.dispose()
                    return
                }
                if (aLong == ADD_TIMER) {
                    timer("initView")
                }
                if (aLong == ADD_TIMER_AND_SHOW_MAP) {
                    if (disposable != null) disposable!!.dispose()       //取消之前所有的轮询订阅
                    timer("showCarIcon")
                    LogUtils.d(TAG, "processLocationInfo: " + carLocationInfo.status)
                    if (carLocationInfo.status == 200 ||carLocationInfo.status == 10000) {
                        initLocationType()
                    } else {
                        tv_carStatus.text = "校车好像失联了..."
                    }
                }
            }
        })
        //进行一次接口数据请求
        smoothMoveData!!.loadCarLocation(ADD_TIMER)

        //完成后开始轮询并且显示地图
        smoothMoveData!!.loadCarLocation(ADD_TIMER_AND_SHOW_MAP)

    }

    private fun initAMap(ifLocation: Boolean) {
        if (ifLocation) {
            aMap.isMyLocationEnabled = true
            aMap.myLocationStyle = locationStyle
        }
        //加载地图材质包
        aMap.uiSettings.isZoomControlsEnabled = false
        val parent = File(filesDir , "/maoXhMap")


        if(!defaultSharedPreferences.getBoolean(IS_MAP_SAVED,false))MapStyleHelper(this).saveMapStyle{
            initAMap(ifLocation)
        }
        val styleExtra = File(parent, "style_extra.data")
        val style = File(parent, "style.data")
        val customMapStyleOptions = CustomMapStyleOptions()
        customMapStyleOptions.apply {
            isEnable = true
            styleDataPath = style.absolutePath
            styleExtraPath = styleExtra.absolutePath
        }
        aMap.setCustomMapStyle(customMapStyleOptions)
        aMap.uiSettings.isMyLocationButtonEnabled = false
        //地图开始时显示的中心和缩放大小
        val update = CameraUpdateFactory.newLatLngZoom(LatLng(29.531876, 106.606789), 17f)
        aMap.animateCamera(update)
    }

    fun initLocationType() {
        val descriptor: BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.schoolcar_ic_school_car_search_boy)
        locationStyle = MyLocationStyle()
        locationStyle.interval(2000)
        locationStyle.strokeWidth(0f)
        locationStyle.radiusFillColor(Color.alpha(0))
        locationStyle.myLocationIcon(descriptor)
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)
    }


    /**
     * 对地图的定位数据进行初始化，同时判定当前校车是否在学校内
     * 在SchoolCarMap class 中回调initLocationMapButton（）时调用 -> 若用户同意定位权限
     */
    private fun initData() {
        val locationListener = AMapLocationListener {
            if (!showCarIcon) {
                val myDistance = AMapUtils.calculateLineDistance(LatLng(29.531876, 106.606789), LatLng(it.latitude, it.longitude))
                if (myDistance > 1300) {
                    aMap.isMyLocationEnabled = false
                }
            }
        }
        locationClient = AMapLocationClient(application)
        val locationClientOption = AMapLocationClientOption()

        locationClient.setLocationOption(locationClientOption)
        locationClient.setLocationListener(locationListener)
        locationClient.startLocation()
    }

    /**
     * timer 用来轮询接口时调用
     * @param name 标识独立轮询
     */
    private fun timer(name: String) {

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnNext {
                    smoothMoveData!!.loadCarLocation(NOT_ADD_TIMER)
                }.observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<Long> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(t: Long) {

                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {}
                })
    }

    private fun getSmoothMakerBitmaps():List<Bitmap>{
        return buildList {
            add(getSmoothMakerBitmap(BitmapFactory.decodeResource(this@SchoolCarActivity.resources, R.mipmap.schoolcar_ic_school_car)))
            add(getSmoothMakerBitmap(BitmapFactory.decodeResource(this@SchoolCarActivity.resources, R.mipmap.schoolcar_ic_school_car)))
            add(getSmoothMakerBitmap(BitmapFactory.decodeResource(this@SchoolCarActivity.resources, R.mipmap.schoolcar_ic_school_car)))
            add(getSmoothMakerBitmap(BitmapFactory.decodeResource(this@SchoolCarActivity.resources, R.mipmap.schoolcar_ic_school_car)))
        }
    }


    private fun getSmoothMakerBitmap(tempPointBitmap: Bitmap):Bitmap {
        var pointBitmap = tempPointBitmap
        var width = pointBitmap.width
        var height = pointBitmap.height
        var scaleWidth = dp2px(22f) / width
        var scaleHeight = dp2px(22f) / height
        var matrix = Matrix()
        matrix.postScale(scaleWidth.toFloat(), scaleHeight.toFloat())
        pointBitmap = Bitmap.createBitmap(pointBitmap, 0, 0, width, height, matrix, true)
        var backBitmap = BitmapFactory.decodeResource(this@SchoolCarActivity.resources, R.mipmap.schoolcar_ic_school_car_background)
        width = backBitmap.width
        height = backBitmap.height
        scaleWidth = dp2px(66f) / width
        scaleHeight = dp2px(66f) / height
        matrix = Matrix()
        matrix.postScale(scaleWidth.toFloat(), scaleHeight.toFloat())
        backBitmap = Bitmap.createBitmap(backBitmap, 0, 0, width, height, matrix, true)
        val bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(pointBitmap, (backBitmap.width - pointBitmap.width) / 2.toFloat(), (backBitmap.height - pointBitmap.height) / 2.toFloat(), null)
        canvas.save()
        canvas.restore()
        return bitmap
    }

    private fun checkActivityPermission(): Boolean {
        refusePermissions.clear()

        permissions.forEach {
            if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                refusePermissions.add(it)
            }
        }

        if (refusePermissions.size > 0) {//如果有权限未通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, mRequestCode)
        } else {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var hasPermissionsDismiss = false

        if (requestCode == mRequestCode) {
            grantResults.forEach { grant ->
                if (grant == -1) {
                    hasPermissionsDismiss = true
                }
            }
        }

        if (hasPermissionsDismiss) {
            ExploreSchoolCarDialog.show(this, NO_GPS)
            ifLocation = false
        } else {
            locationClient = AMapLocationClient(applicationContext)
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        ExploreSchoolCarDialog.cancelDialog()
        if (checkActivityPermission()) {
            locationClient.onDestroy()
        }
        mv_map.onDestroy()
        if (disposable != null) {
            disposable!!.dispose()
            LogUtils.d(TAG, disposable!!.isDisposed.toString())
        }
        smoothMoveData?.let {
            disposable?.let { if (!it.isDisposed) it.dispose() }
        }
    }

    override fun onPause() {
        super.onPause()

        ExploreSchoolCarDialog.cancelDialog()

        mv_map.onPause()
        if (disposable != null) {
            if (disposable!!.isDisposed) {
                LogUtils.d(TAG, "onRestart: disposed!!!")
            } else {
                LogUtils.d(TAG, "onRestart: not disposed!!!")
            }
        }
        if (smoothMoveData != null) {
            smoothMoveData!!.clearAllList()
        }
    }

    override fun onResume() {
        super.onResume()
        mv_map.onResume()
        if (smoothMoveData != null) {
            smoothMoveData!!.clearAllList()
            smoothMoveData!!.loadCarLocation(ADD_TIMER_AND_SHOW_MAP)
        }
    }

    override fun onStop() {
        super.onStop()
        if (smoothMoveData != null) {
            smoothMoveData!!.clearAllList()
        }
    }

}
