package com.mredrock.cyxbs.schoolcar

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapsInitializer
import com.amap.api.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.config.DISCOVER_SCHOOL_CAR
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.dip
import com.mredrock.cyxbs.common.utils.extensions.px2dip
import com.mredrock.cyxbs.schoolcar.Interface.SchoolCarInterface
import com.mredrock.cyxbs.schoolcar.R
import com.mredrock.cyxbs.schoolcar.adapter.CarIconAdapter
import com.mredrock.cyxbs.schoolcar.adapter.CarSiteAdapter
import com.mredrock.cyxbs.schoolcar.bean.SchoolCarLocation
import com.mredrock.cyxbs.schoolcar.widget.SchoolCarsSmoothMove
import com.mredrock.cyxbs.schoolcar.databinding.SchoolcarActivitySchoolcarBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.io.File
import java.util.concurrent.TimeUnit

/**
 *@Author:SnowOwlet
 *@Date:2022/5/6 19:33
 *
 */
const val IS_MAP_SAVED = "isMapSaved"
@Route(path = DISCOVER_SCHOOL_CAR)
class SchoolCarActivity:BaseActivity() {
  companion object {
    const val ADD_TIMER: Long = 3
    const val ADD_TIMER_AND_SHOW_MAP: Long = 55
    const val NOT_ADD_TIMER: Long = 0
  }
  //是否显示车辆
  private var showCarIcon = false
  private val vm by viewModels<SchoolCarViewModel>()
  private lateinit var binding: SchoolcarActivitySchoolcarBinding
  //是否显示定位
  private var ifLocation = true
  //定位client
  private lateinit var locationClient: AMapLocationClient
  //地图
  lateinit var aMap: AMap
  //一个help类
  private var smoothMoveData: SchoolCarsSmoothMove? = null
  private var disposable: Disposable? = null
  private var savedInstanceState:Bundle ?=null
  //是否已经滑动
  var isBeginning = true
  @Override
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    this.savedInstanceState = savedInstanceState
    //这里是用户协议申明同意
    MapsInitializer.updatePrivacyShow(this, true, true)
    MapsInitializer.updatePrivacyAgree(this, true)

    binding = SchoolcarActivitySchoolcarBinding.inflate(layoutInflater)
    setContentView(binding.root)

    if (checkActivityPermission()) {
      locationClient = AMapLocationClient(applicationContext)
      binding.schoolcarMvMap.onCreate(savedInstanceState)
      initView()
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    var hasPermissionsDismiss = false

    if (requestCode == 200) {
      grantResults.forEach { grant ->
        if (grant == -1) {
          hasPermissionsDismiss = true
        }
      }
    }

    if (hasPermissionsDismiss) {
      ifLocation = false
    } else {
      locationClient = AMapLocationClient(applicationContext)
      binding.schoolcarMvMap.onCreate(savedInstanceState)
      locationClient = AMapLocationClient(applicationContext)
      initView()
    }
  }

  private fun initView(){
    //放大缩小
    binding.schoolCarCvOut.setOnClickListener {
      val update = CameraUpdateFactory.zoomOut()
      aMap.animateCamera(update)
    }

    binding.schoolCarCvExpand.setOnClickListener {
      val update = CameraUpdateFactory.zoomIn()
      aMap.animateCamera(update)
    }
    //回到自己定位位置
    binding.schoolCarCvPositioning.setOnClickListener {
      vm.chooseCar(-2)
      aMap.myLocation?.let { location->
        location.longitude.let {
          location.latitude.let {
            val update =
              if (aMap.myLocation.longitude != 0.0 || aMap.myLocation.latitude !== 0.0){
                CameraUpdateFactory.newLatLngZoom(LatLng(aMap.myLocation.latitude, aMap.myLocation.longitude), 17f)
              }else{
                CameraUpdateFactory.newLatLngZoom(LatLng(29.531876, 106.606789), 17f)
              }
            aMap.animateCamera(update)
          }
        }
      }
    }
    binding.schoolCarIvBack.setOnClickListener {
      finishAfterTransition()
    }

    aMap = binding.schoolcarMvMap.map
    //如果用户同意定位权限，则开启定位和初始化定位用到的类
    if (ifLocation) {
      initData()
    }
    //初始化地图配置
    initAMap(ifLocation)

    //限制缩放大小
    aMap.minZoomLevel = 15f
    //初始化help类
    smoothMoveData = SchoolCarsSmoothMove(this,vm)

    smoothMoveData!!.setCarMapInterface(object : SchoolCarInterface {
      // 回调是否显示地图，和是否开启一个timer轮询接口
      override fun processLocationInfo(carLocationInfo: RedrockApiWrapper<SchoolCarLocation>, aLong: Long) {
        if (carLocationInfo.data.data.isEmpty()) {
          if (disposable != null) disposable!!.dispose()
          return
        }
        if (aLong == ADD_TIMER) {
          timer("initView")
        }
        if (aLong == ADD_TIMER_AND_SHOW_MAP) {
          if (disposable != null) disposable!!.dispose()       //取消之前所有的轮询订阅
          timer("showCarIcon")

          if (carLocationInfo.status == 200 ||carLocationInfo.status == 10000) {
            initLocationType()
          }
        }
      }
    })
    //进行一次接口数据请求
    smoothMoveData!!.loadCarLocation(ADD_TIMER)

    //完成后开始轮询并且显示地图
    smoothMoveData!!.loadCarLocation(ADD_TIMER_AND_SHOW_MAP)

    initBottomSheetBehavior()
  }
  //这里保证第一次显示最近的
  private fun initBottomSheetBehavior(){
    val behavior = BottomSheetBehavior.from(binding.schoolcarBts)
    behavior.addBottomSheetCallback(object :BottomSheetBehavior.BottomSheetCallback(){
      var cvHeight:Float = 0.0F
      var ivHeight:Float = 0.0F
      var realPath = 100
      var path:Float = 285F
      override fun onStateChanged(bottomSheet: View, newState: Int) {
        if(vm.line.value == -1 && newState == BottomSheetBehavior.STATE_DRAGGING && isBeginning){
          vm.showRecently()
        }
      }
      override fun onSlide(bottomSheet: View, slideOffset: Float) {
        if (cvHeight == 0.0F && ivHeight == 0.0F) {
          cvHeight = binding.schoolCarCvPositioning.y
          ivHeight = binding.schoolCarCvPositioningIv.y
          val top = binding.schoolCarCvOut.bottom
          val bottom = binding.schoolCarCvPositioning.top
          realPath = bottom - top
          path = px2dip(realPath) - 18F
        }
        binding.schoolCarCvPositioning.y = cvHeight-dip(path*slideOffset).toFloat()
        binding.schoolCarCvPositioningIv.y = ivHeight-dip(path*slideOffset).toFloat()
      }
    })

    vm.bsbState.observe(this){
      when(it){
        0->{
          if (!isBeginning){
            behavior.isDraggable = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
          }
        }
        1->{
          behavior.isDraggable = true
          behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        2->{
          behavior.isDraggable = false
          behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
      }
    }
    var carIconAdapter: CarIconAdapter?= null
    var siteAdapter: CarSiteAdapter?= null

    vm.mapInfo.observe(this){ mapLines ->
      carIconAdapter?: run {
        carIconAdapter = CarIconAdapter(this,mapLines.lines)

        carIconAdapter?.setOnItemListener { position,isIcon ->
          isBeginning = false
          if (isIcon){
            smoothMoveData?.hideCheck()
            siteAdapter?.clear()
          }
          //-1，-2 特殊值
          if(position == -1 || position == -2){
            vm.bsbHide()
            vm.changeLine()
            siteAdapter?.clear()
            if (position == -1 && isIcon){
              startActivity(Intent(this@SchoolCarActivity, SchoolDetailActivity::class.java))
            }
          }else{
            if (isIcon){
              changeSiteView(false)
            }
            binding.apply {
              if (isIcon){
                schoolCarTvTitleBts.text = mapLines.lines[position].name
              }
              schoolCarTvTimeBts.text = "运行时间: ${mapLines.lines[position].runTime}"
              schoolCarCardRunTypeBts.text = mapLines.lines[position].runType
              schoolCarCardLineTypeBts.text = mapLines.lines[position].sendType
            }
            vm.bsbShow()
            if (isIcon){
              vm.changeLine(mapLines.lines[position].id)
            }
            siteAdapter = CarSiteAdapter(this,mapLines.lines[position].stations,mapLines.lines[position].id)

            binding.schoolCarSiteRvBts.apply {
              this.adapter = siteAdapter
              this.layoutManager = LinearLayoutManager(this@SchoolCarActivity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
              }
            }
            if (!isIcon){
              vm.chooseSite.observe(this){
                vm.mapInfo.value?.lines?.get(position)?.stations?.forEachIndexed { index, station ->
                  if (station.id == it){
                    if (vm.line.value != -1) {
                      goneSiteView()
                    }
                      siteAdapter?.choose(index)
                  }
                }
              }
            }
          }
        }
        binding.schoolCarRvBts.apply {
          adapter = carIconAdapter
          layoutManager = LinearLayoutManager(this@SchoolCarActivity).apply {
            orientation = LinearLayoutManager.HORIZONTAL
          }
        }
      }
    }

    vm.carLine.observe(this){ line ->
      if (line == -2){
        carIconAdapter?.let { adapter ->
          adapter.clear()
        }
      }else{
        carIconAdapter?.let { adapter ->
          adapter.choose(line,true)
        }
      }
      siteAdapter?.clear()
    }

    vm.chooseSite.observe(this){
      vm.mapInfo.value?.lines?.forEach { line ->
        line.stations.forEachIndexed { index, station ->
          if (station.id == it){
            binding.schoolCarTvTitleBts.text = station.name
          }
        }
      }
    }

    vm.mapLine.observe(this){ arrays ->
      var i = 0
      if (arrays.size < 1) return@observe
      if (arrays.size == 1){
        vm.mapInfo.value?.lines?.forEach { line ->
          if (line.id == arrays[0]) {
            binding.schoolCarCardTvChangeBts.text = line.name
            binding.schoolCarCardTvChangeBts.setTextColor(resources.getColor(com.mredrock.cyxbs.common.R.color.common_level_one_font_color))
            binding.schoolCarCardIvChangeBts.setImageResource(R.drawable.schoolcar_bts_btn_change)
            binding.schoolCarCardChangeBts.setBackgroundResource(R.drawable.schoolcar_bts_btn_change_shape)
          }
        }
          vm.bsbShow()
        changeSiteView()
        carIconAdapter?.let { adapter ->
          if (vm.line.value != -1){
            adapter.choose(arrays[0],isShowIcon = true)
          }else{
            adapter.choose(arrays[0])
          }
        }
        binding.schoolCarCardChangeBts.isClickable = false
        return@observe
      }

      binding.schoolCarCardChangeBts.isClickable = true

      var choose = arrays[i]
      if (i < arrays.size-1) i++ else i = 0
      var next = arrays[i]
      vm.mapInfo.value?.lines?.forEach { line ->
        if (line.id == choose) {
            binding.schoolCarCardTvChangeBts.text = line.name
            binding.schoolCarCardTvChangeBts.setTextColor(resources.getColor(com.mredrock.cyxbs.common.R.color.common_white_font_color))
            binding.schoolCarCardChangeBts.setBackgroundResource(R.drawable.schoolcar_bts_btn_change_shape_select)
            binding.schoolCarCardIvChangeBts.setImageResource(R.drawable.schoolcar_bts_btn_change_select)
        }
      }
      vm.bsbShow()
      changeSiteView()
      binding.schoolCarCardChangeBts.setOnClickListener {
        vm.mapInfo.value?.lines?.forEach { line ->
          if (line.id == next) {
            binding.schoolCarCardTvChangeBts.text = line.name
            carIconAdapter?.let { adapter ->
              adapter.choose(choose)
            }
          }
        }
        if (i < arrays.size-1) i++ else i = 0
        choose = next
        next = arrays[i]
      }
      carIconAdapter?.let { adapter ->
        if (vm.line.value != -1){
          adapter.choose(choose,isShowIcon = true)
        }else{
          adapter.choose(choose)
        }
      }
      vm.bsbShow()
    }

    vm.initMapInfo()
  }


  private fun initAMap(ifLocation: Boolean) {
    if (ifLocation) {
      aMap.isMyLocationEnabled = true
      aMap.myLocationStyle = initLocationType()
    }

    //加载地图材质包
    aMap.uiSettings.isZoomControlsEnabled = false
    val parent = File(filesDir , "/maoXhMap")

    if(!defaultSharedPreferences.getBoolean(IS_MAP_SAVED, false)) MapStyleHelper(this).saveMapStyle{
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

  private fun initData() {
    val locationListener = AMapLocationListener {
      if (!showCarIcon) {
          if (!vm.showRecently.hasObservers()){
            vm.showRecently.observe(this){ i->
              if (isBeginning && i != 0){
                vm.recentlySite(it.latitude,it.longitude)
              }
            }
          }
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

  private fun changeSiteView(show:Boolean = true){
      if (show){
        binding.schoolCarCardChangeBts.visibility = View.VISIBLE
        binding.apply {
          schoolCarCardLineTypeBts.visibility = View.INVISIBLE
          schoolCarCardRunTypeBts.visibility = View.INVISIBLE
          schoolCarCardRunTypeBtsCard.visibility = View.INVISIBLE
          schoolCarCardLineTypeBtsCard.visibility = View.INVISIBLE
          schoolCarTvTimeBts.visibility = View.INVISIBLE
        }
      }else{
        binding.schoolCarCardChangeBts.visibility = View.GONE
        binding.apply {
          schoolCarCardLineTypeBts.visibility = View.VISIBLE
          schoolCarCardRunTypeBts.visibility = View.VISIBLE
          schoolCarCardRunTypeBtsCard.visibility = View.VISIBLE
          schoolCarCardLineTypeBtsCard.visibility = View.VISIBLE
          schoolCarTvTimeBts.visibility = View.VISIBLE
        }
      }
  }

  private fun goneSiteView(){
    binding.apply {
      schoolCarCardChangeBts.visibility = View.GONE
      schoolCarCardLineTypeBts.visibility = View.INVISIBLE
      schoolCarCardRunTypeBts.visibility = View.INVISIBLE
      schoolCarCardRunTypeBtsCard.visibility = View.INVISIBLE
      schoolCarCardLineTypeBtsCard.visibility = View.INVISIBLE
      schoolCarTvTimeBts.visibility = View.INVISIBLE
    }
  }

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

  //权限检查
  private fun checkActivityPermission(): Boolean {
    if (ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        200
      )
    }
    return ContextCompat.checkSelfPermission(
      this,
      Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
  }

  //得到自己定位图标样式
  private fun initLocationType(): MyLocationStyle = MyLocationStyle().apply {
    val descriptor: BitmapDescriptor =
      BitmapDescriptorFactory.fromResource(R.drawable.schoolcar_ic_my)
    interval(2000)
    strokeWidth(0f)
    radiusFillColor(Color.alpha(0))
    myLocationIcon(descriptor)
    myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)
  }


  override fun onDestroy() {
    super.onDestroy()

    if (checkActivityPermission()) {
      locationClient.onDestroy()
    }
    binding.schoolcarMvMap.onDestroy()
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

    binding.schoolcarMvMap.onPause()
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
    binding.schoolcarMvMap.onResume()

    vm.bsbHide(isBeginning)
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