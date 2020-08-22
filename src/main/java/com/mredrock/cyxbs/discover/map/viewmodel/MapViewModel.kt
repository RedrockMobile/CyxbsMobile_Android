package com.mredrock.cyxbs.discover.map.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.isSuccessful
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.*
import com.mredrock.cyxbs.discover.map.component.MapToast
import com.mredrock.cyxbs.discover.map.model.DataSet
import com.mredrock.cyxbs.discover.map.network.MapApiService
import com.mredrock.cyxbs.discover.map.widget.MapDialogTips
import com.mredrock.cyxbs.discover.map.widget.OnSelectListenerTips
import com.mredrock.cyxbs.discover.map.widget.ProgressDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


/**
 *@author zhangzhe
 *@date 2020/8/8
 *@description 由于掌邮地图模块采用单activity多fragment模式，所以所有fragment都共用本viewModel
 */

class MapViewModel : BaseViewModel() {
    private lateinit var mapApiService: MapApiService

    //网络请求得到的数据(地图基本信息接口)，如何使用：在要使用的activity或者fragment观察即可
    val mapInfo = MutableLiveData<MapInfo>()

    //搜索框下方按钮内容
    val buttonInfo = MutableLiveData<ButtonInfo>()

    //喜欢列表内容
    val collectList = MutableLiveData<MutableList<FavoritePlace>>()

    //当前展示的地点信息
    val placeDetails = MutableLiveData<PlaceDetails>()

    //是否正在显示收藏页面
    val fragmentFavoriteEditIsShowing = MutableLiveData<Boolean>()

    //是否正在显示全部图片界面
    val fragmentAllPictureIsShowing = MutableLiveData<Boolean>()

    //详细页面正在显示的地点id
    var showingPlaceId = "-1"

    //是否显示bottomSheet，用于监听并隐藏
    val bottomSheetStatus = MutableLiveData<Int>()

    //是否在动画中
    val mapViewIsInAnimation = MutableLiveData<Boolean>()

    //搜索框的文字，只用在搜索界面fragment观察本变量即可实现搜索
    val searchText = MutableLiveData("")

    //搜索结果
    val searchResult = ObservableArrayList<PlaceItem>()

    //搜索记录
    var searchHistory = mutableListOf<String>()

    //当历史记录点击，通知输入框更新
    val searchHistoryString = MutableLiveData<String>("")

    //显示的地点id
    val showSomePlaceIconById = MutableLiveData<MutableList<String>>()

    //缩放到某一个地点id
    val showIconById = MutableLiveData<String>()

    //用于通知mainFragment关闭搜索框
    val closeSearchFragment = MutableLiveData<Boolean>()

    //网络请求失败，使用本地缓存
    val loadFail = MutableLiveData<Boolean>()

    //是否锁定
    val isLock = MutableLiveData<Boolean>(false)

    //是否点击了标签或收藏
    val isClickSymbol = MutableLiveData<Boolean>()

    //通知收藏列表关闭
    val showPopUpWindow = MutableLiveData<Boolean>()

    //通知标签栏取消选择
    val unCheck = MutableLiveData<Boolean>()

    //掌邮传过来的OpenSiteId
    val openId = MutableLiveData<String>()
    fun init() {

        /**
         * 初始化网络请求
         */
        mapApiService = ApiGenerator.getApiService(MapApiService::class.java)
        /**
         * 获得地图基本信息，地点坐标等
         */
        mapApiService.getMapInfo()//网络请求替换为：apiService.getMapInfo()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    MapToast.makeText(BaseApp.context, R.string.map_network_connect_error, Toast.LENGTH_SHORT).show()
                    //使用缓存数据
                    val mapInfoStore = DataSet.getMapInfo()
                    if (mapInfoStore != null) {
                        mapInfo.postValue(mapInfoStore)
                    }
                    loadFail.postValue(true)
                    true
                }
                .safeSubscribeBy {
                    mapInfo.value = it.data
                    DataSet.saveMapInfo(it.data)
                }.lifeCycle()
        mapApiService.getButtonInfo()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    MapToast.makeText(BaseApp.context, R.string.map_network_connect_error, Toast.LENGTH_SHORT).show()
                    //使用缓存数据
                    val buttonInfoStore = DataSet.getButtonInfo()
                    if (buttonInfoStore != null) {
                        buttonInfo.postValue(buttonInfoStore)
                    }
                    true
                }
                .safeSubscribeBy {
                    buttonInfo.postValue(it.data)
                    DataSet.saveButtonInfo(it.data)
                }.lifeCycle()
        /**
         * 刷新收藏列表
         */
        refreshCollectList(false)

    }


    /**
     * 获得地点详细信息，如 图片、标签 等
     * 当地图标签被点击，执行此网络请求，在对应的fragment观察数据即可
     */
    fun getPlaceDetails(placeId: String, showBottomSheetAfterSuccess: Boolean) {
        mapApiService.getPlaceDetails(placeId)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    MapToast.makeText(BaseApp.context, R.string.map_network_connect_error, Toast.LENGTH_SHORT).show()

                    //有缓存则使用缓存（上面和下面的逻辑基本和这里是一样的，所有缓存在DataSet里
                    //去掉有DataSet的语句就是基本的业务逻辑了（逻辑就很清晰了））
                    val t = DataSet.getPlaceDetails(placeId)
                    if (t != null) {
                        showingPlaceId = placeId
                        placeDetails.postValue(t)
                        if (bottomSheetStatus.value == BottomSheetBehavior.STATE_HIDDEN) {
                            bottomSheetStatus.postValue(BottomSheetBehavior.STATE_COLLAPSED)
                        }
                        if (showBottomSheetAfterSuccess) {
                            bottomSheetStatus.postValue(BottomSheetBehavior.STATE_EXPANDED)
                        }
                    } else {
                        bottomSheetStatus.postValue(BottomSheetBehavior.STATE_HIDDEN)
                    }

                    true
                }
                .safeSubscribeBy() {
                    showingPlaceId = placeId
                    placeDetails.postValue(it.data)
                    if (bottomSheetStatus.value == BottomSheetBehavior.STATE_HIDDEN) {
                        bottomSheetStatus.postValue(BottomSheetBehavior.STATE_COLLAPSED)
                    }
                    DataSet.savePlaceDetails(it.data, placeId)

                    if (showBottomSheetAfterSuccess) {
                        bottomSheetStatus.postValue(BottomSheetBehavior.STATE_EXPANDED)
                    }
                }.lifeCycle()
    }

    /**
     * 刷新收藏列表
     * @param showCollectedPlace 是否在获得网络数据后，显示全部收藏地点并缩放
     */
    fun refreshCollectList(showCollectedPlace: Boolean) {
        mapApiService.getCollect()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    val list = DataSet.getCollect()
                    if (list != null) {
                        collectList.postValue(list)
                    }
                    if (collectList.value?.size == 0) {
                        MapToast.makeText(BaseApp.context, R.string.map_favorite_empty, Toast.LENGTH_SHORT).show()
                    } else {
                        if (showCollectedPlace) {
                            val listTmp = mutableListOf<String>()
                            if (list != null) {
                                listTmp.addAll(list.map { t -> t.placeId })
                            }
                            showSomePlaceIconById.value = listTmp
                        }
                    }

                    true
                }
                .safeSubscribeBy {
                    /**
                     * 同步服务端与缓存。因为之前是可以取placeNickname的所以这里还可以简化，
                     * 但保留功能扩展性，而且本来这里数据量很小，故下列代码也可以接受
                     */
                    it.data.placeIdList.forEach { item ->
                        DataSet.addCollect(item)
                    }

                    DataSet.getCollect()?.forEach { t ->
                        var existInServer = false
                        for (i in it.data.placeIdList) {
                            if (t.placeId == i) {
                                existInServer = true
                                break
                            }
                        }
                        if (!existInServer) {
                            DataSet.deleteCollect(t.placeId)
                        }
                    }

                    collectList.value = DataSet.getCollect()

                    if (collectList.value?.size == 0) {
                        MapToast.makeText(BaseApp.context, R.string.map_favorite_empty, Toast.LENGTH_SHORT).show()
                    } else {
                        if (showCollectedPlace) {
                            showSomePlaceIconById.value = it.data.placeIdList
                        }
                    }
                }.lifeCycle()
    }

    /**
     * 添加收藏
     */
    fun addCollect(placeNickname: String, id: String) {
        var notExist = true
        if (collectList.value != null) {
            for (t: FavoritePlace in collectList.value!!) {
                if (t.placeId == id) {
                    notExist = false
                }
            }
        }

        if (notExist) {
            mapApiService.addCollect(id)
                    .setSchedulers()
                    .doOnErrorWithDefaultErrorHandler {
                        MapToast.makeText(BaseApp.context, R.string.map_network_connect_error, Toast.LENGTH_SHORT).show()
                        ProgressDialog.hide()
                        refreshCollectList(true)
                        true
                    }
                    .safeSubscribeBy {
                        if (it.isSuccessful) {
                            DataSet.addCollect(FavoritePlace(placeNickname, id))
                            MapToast.makeText(BaseApp.context, R.string.map_collect_success, Toast.LENGTH_SHORT).show()
                        } else {
                            MapToast.makeText(BaseApp.context, R.string.map_network_connect_error, Toast.LENGTH_SHORT).show()
                        }
                        ProgressDialog.hide()
                        refreshCollectList(true)
                    }.lifeCycle()
        } else {
            DataSet.addCollect(FavoritePlace(placeNickname, id))
            ProgressDialog.hide()
            refreshCollectList(true)
        }


    }

    /**
     * 删除收藏
     */
    fun deleteCollect(id: String) {
        mapApiService.deleteCollect(id.toInt())
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    MapToast.makeText(BaseApp.context, R.string.map_network_connect_error, Toast.LENGTH_SHORT).show()
                    ProgressDialog.hide()
                    refreshCollectList(true)
                    true
                }
                .safeSubscribeBy {
                    if (it.isSuccessful) {
                        DataSet.deleteCollect(id)
                    } else {
                        MapToast.makeText(BaseApp.context, R.string.map_network_connect_error, Toast.LENGTH_SHORT).show()
                    }
                    ProgressDialog.hide()
                    refreshCollectList(true)
                }.lifeCycle()

    }

    /**
     * 历史记录变化，从缓存中重新读取，并通知更新
     */
    fun notifySearchHistoryChange() {
        searchHistory = DataSet.getSearchHistory() ?: mutableListOf()
        searchHistory.reverse()
    }

    /**
     * 上传图片
     */

    fun uploadPicture(imgPath: String?, context: Context) {
        if (imgPath == null) {
            return
        }
        val file = File(imgPath)
        val requestFile: RequestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val fileNameByTimeStamp: String = System.currentTimeMillis().toString() + ".jpg"
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("upload_picture", fileNameByTimeStamp, requestFile)
        val params: MutableMap<String, Int> = HashMap()
        params["place_id"] = showingPlaceId.toInt()

        mapApiService.uploadPicture(body, params)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    ProgressDialog.hide()
                    MapToast.makeText(BaseApp.context, R.string.map_upload_picture_failed_network, Toast.LENGTH_LONG).show()
                    true
                }
                .safeSubscribeBy {
                    ProgressDialog.hide()
                    if (it.isSuccessful) {
                        MapDialogTips.show(context, context.resources.getString(R.string.map_upload_picture_success_title), context.resources.getString(R.string.map_upload_picture_success_content), true, object : OnSelectListenerTips {
                            override fun onPositive() {}
                        })
                    } else {
                        MapToast.makeText(BaseApp.context, R.string.map_upload_picture_failed_server, Toast.LENGTH_LONG).show()

                    }
                }.lifeCycle()

    }
    fun getPlaceSearch(openString: String?) {
        mapApiService.placeSearch(openString ?: "-1")
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    true
                }
                .safeSubscribeBy {
                    if (it.status == 200) {
                        openId.value = it.data.placeId
                        getPlaceDetails(it.data.placeId, false)
                        bottomSheetStatus.value = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    if (it.status == 500) {
                        openId.value = "500"
                        getPlaceDetails(mapInfo.value!!.openSiteId.toString(), false)
                        bottomSheetStatus.value = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }.lifeCycle()
    }
}


