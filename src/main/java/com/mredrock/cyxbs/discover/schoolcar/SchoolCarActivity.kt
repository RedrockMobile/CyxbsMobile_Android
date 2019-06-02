package com.mredrock.cyxbs.discover.schoolcar

//import com.jude.swipbackhelper.SwipeBackHelper
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.utils.overlay.SmoothMoveMarker
import com.mredrock.cyxbs.common.config.DISCOVER_SCHOOL_CAR
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.schoolcar.Interface.SchoolCarInterface
import com.mredrock.cyxbs.discover.schoolcar.bean.SchoolCarLocation
import com.mredrock.cyxbs.discover.schoolcar.widget.ExploreSchoolCarDialog
import com.mredrock.cyxbs.discover.schoolcar.widget.SchoolCarMap
import com.mredrock.cyxbs.discover.schoolcar.widget.SchoolcarsSmoothMove
import com.mredrock.cyxbs.schoolcar.R
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_schoolcar.*
import org.jetbrains.anko.dip
import java.util.concurrent.TimeUnit

/**
 * Created by glossimar on 2018/9/12
 */

@Route(path = DISCOVER_SCHOOL_CAR)
class SchoolCarActivity : BaseActivity() {
    companion object {
        const val TAG: String = "ExploreSchoolCar"
        const val TIME_OUT: Int = 1
        const val LOST_SERVICES: Int = 2
        const val NO_GPS: Int = 3

        const val WHOLE_SCHOOL: Int = 0
        const val ME: Int = 1
        const val SCHOOL_CAR: Int = 2

        const val ADD_TIMER: Long = 3
        const val ADD_TIMER_AND_SHOW_MAP: Long = 55
        const val NOT_ADD_Timer: Long = 0
    }

    var ifLocation = true
    private var showCarIcon = false

    private var locationStatus: Int = 0
    private var savedInstanceState: Bundle? = null
    private lateinit var makerBitmap: Bitmap
    private lateinit var holeSchoolButton: ImageView
    private var schoolCarMap: SchoolCarMap? = null
    private var smoothMoveData: SchoolcarsSmoothMove? = null
    private lateinit var smoothMoveMarkers: MutableList<SmoothMoveMarker>
    private lateinit var locationClient: AMapLocationClient
    private var disposable: Disposable? = null
    private lateinit var dataList: List<SchoolCarLocation.Data>

    override val isFragmentActivity = false

    //申请权限
    private val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val refusePermissions = mutableListOf<String>()
    private val mRequestCode = 200

    private val menuListener = MenuItem.OnMenuItemClickListener {
        when (it?.itemId) {
            R.id.school_car_more -> {
                startActivity<SchoolCarLearnMoreActivity>()
            }
        }
        return@OnMenuItemClickListener false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        setContentView(R.layout.activity_schoolcar)

        if (checkActivityPermission()) {
            locationClient = AMapLocationClient(applicationContext)
            initSchoolCarMap()
        }
    }


    /**
     * 获取schoolCarMap的实例并且重写接口initLocationMapButton（高德地图只能使用动态加载添加UI）
     *  onCreate()中调用
     */
    private fun initSchoolCarMap() {
        schoolCarMap = SchoolCarMap(this, savedInstanceState, object : SchoolCarInterface {
            override fun processLocationInfo(carLocationInfo: SchoolCarLocation, aLong: Long) {}

            //在图地中动态加载控件
            override fun initLocationMapButton(aMap: AMap, locationStyle: MyLocationStyle) {
                val relativeLayoutDown = RelativeLayout(this@SchoolCarActivity)
                val layoutParamsDown = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                explore_school_car_linearLayout.addView(relativeLayoutDown, layoutParamsDown)

                // 在地图中加入"全校"<->"我"的button和两种模式转换时button需要的逻辑修改（包括定位模式和button上的图片）
                holeSchoolButton = ImageView(this@SchoolCarActivity)
                holeSchoolButton.setBackgroundColor(Color.TRANSPARENT)
                holeSchoolButton.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_school_car_search_hole_school))

                locationStatus = WHOLE_SCHOOL

                holeSchoolButton.setOnClickListener {
                    LogUtils.d(TAG, ifLocation.toString())
                    when (locationStatus) {
                        WHOLE_SCHOOL -> {
                            holeSchoolButton.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_school_car_search_me))

                            if (ifLocation) {
                                locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW)
                                aMap.myLocationStyle = locationStyle
                            }

                            locationStatus = ME
                        }
                        ME -> {
                            holeSchoolButton.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_school_car_search_hole_school))

                            if (ifLocation) {
                                locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)
                                aMap.myLocationStyle = locationStyle
                            }

                            //update为地图重加载使用的数据，参数为地图中心点和缩放大小
                            val update = CameraUpdateFactory.newLatLngZoom(LatLng(29.531876, 106.606789), 17f)
                            aMap.animateCamera(update)
                            locationStatus = WHOLE_SCHOOL
                        }
                    }
                }
                val dm = resources.displayMetrics
                //在地图中加入holeSchoolButton的所在的RelativeLayout
                //自适应屏幕大小
                val stateRelativeLayout = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                stateRelativeLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                stateRelativeLayout.rightMargin = dip(12)
                stateRelativeLayout.topMargin = dip(12)
                stateRelativeLayout.width = dm.widthPixels / 7
                stateRelativeLayout.height = dm.widthPixels / 7
                relativeLayoutDown.addView(holeSchoolButton, stateRelativeLayout)

                //如果用户同意定位权限，则开启定位和初始化定位用到的类
                if (ifLocation) {
                    LogUtils.d(TAG, "schoolCarMap.initLocationType")
                    schoolCarMap!!.initLocationType()
                    initData()
                }
                //加载地图
                schoolCarMap!!.initAMap(ifLocation)
            }
        })
        initView()
    }

    /**
     * 获取smoothMoveData、makerBitmap的实例, toolbar，开启加载动画
     * 在initSchoolcarMap()中调用 -> 在初始化了schoolCarMap实例后
     */
    private fun initView() {
        makerBitmap = getSmoothMakerBitmap()
        smoothMoveData = SchoolcarsSmoothMove(schoolCarMap!!, this@SchoolCarActivity)

        smoothMoveData!!.setCarMapInterface(object : SchoolCarInterface {
            override fun initLocationMapButton(aMap: AMap, locationStyle: MyLocationStyle) {}

            // 回调是否显示地图，和是否开启一个timer轮询接口
            override fun processLocationInfo(carLocationInfo: SchoolCarLocation, aLong: Long) {
                dataList = carLocationInfo.data
                if (aLong == ADD_TIMER) {
                    timer("initView")
                }
                if (!showCarIcon && aLong == ADD_TIMER_AND_SHOW_MAP) {
                    if (disposable != null) disposable!!.dispose()       //取消之前所有的轮询订阅
                    showCarIcon = true
                    timer("showCarIcon")
                    LogUtils.d(TAG, "processLocationInfo: " + carLocationInfo.status)
                    schoolCarMap!!.showMap(carLocationInfo.status, explore_school_car_linearLayout, explore_schoolcar_load)
                }


            }
        })
        //进行一次接口数据请求
        smoothMoveData!!.loadCarLocation(ADD_TIMER)

        common_toolbar.init("校车轨迹")
//        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false)

        //加载动画为4秒
        Observable.timer(4, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<Long> {
            override fun onNext(t: Long) {
                LogUtils.d(TAG, "onNext: 校车正在初始化...$t")
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {
                //完成后开始轮询并且显示地图
                smoothMoveMarkers = mutableListOf()
                smoothMoveData!!.loadCarLocation(ADD_TIMER_AND_SHOW_MAP)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater?.inflate(R.menu.schoolcar_menu, menu)
        menu?.getItem(0)?.setOnMenuItemClickListener(menuListener)
        return super.onCreateOptionsMenu(menu)
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
                    schoolCarMap!!.aMap.isMyLocationEnabled = false
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
     */
    private fun timer(int: String) {
        Observable.interval(2, TimeUnit.SECONDS)
                .doOnNext {
                    for (i in smoothMoveMarkers.indices) smoothMoveMarkers[i].removeMarker()
                    smoothMoveMarkers = mutableListOf()
                    var i = 0
                    while (i < dataList.size && dataList[i].lat != 0.0) {
                        if (showCarIcon) {
                            smoothMoveData!!.smoothMove(smoothMoveMarkers, makerBitmap)
                        }
                        i++
                    }
                    smoothMoveData!!.loadCarLocation(NOT_ADD_Timer)

                }.observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<Long> {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(t: Long) {}

                    override fun onError(e: Throwable) {}

                    override fun onComplete() {}
                })
    }

    private fun getSmoothMakerBitmap(): Bitmap {
        val bitmap = BitmapFactory.decodeResource(this@SchoolCarActivity.resources, R.mipmap.ic_school_car)
        val width = bitmap.width
        val height = bitmap.height
        val scaleWidth = 70.toFloat() / width
        val scaleHeight = 140.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
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
            finish()
        } else {
            locationClient = AMapLocationClient(applicationContext)
            initSchoolCarMap()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState)
        schoolCarMap?.let {
            if (it.mapView != null) {
                it.mapView!!.onSaveInstanceState(outState)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        ExploreSchoolCarDialog.cancelDialog()

        if (schoolCarMap != null) {
            schoolCarMap!!.destroyMap(locationClient)
        }
        if (disposable != null) {
            disposable!!.dispose()
            LogUtils.d(TAG, disposable!!.isDisposed.toString())
        }
    }

    override fun onPause() {
        super.onPause()

        ExploreSchoolCarDialog.cancelDialog()

        if (schoolCarMap != null) {
            schoolCarMap!!.pauseMap()
        }
        if (disposable != null) {
            if (disposable!!.isDisposed) {
                LogUtils.d(TAG, "onRestart: disposed!!!")
            } else {
                LogUtils.d(TAG, "onRestart: not disposed!!!")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (schoolCarMap != null) {
            schoolCarMap!!.resumeMap()
        }

    }

    override fun onStop() {
        super.onStop()
        if (smoothMoveData != null) {
            smoothMoveData!!.clearAllList()
        }
    }
}
