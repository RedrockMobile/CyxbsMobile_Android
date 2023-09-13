package com.mredrock.cyxbs.discover.pages.discover

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.bean.NewsListItem
import com.mredrock.cyxbs.discover.network.RollerViewInfo
import com.mredrock.cyxbs.discover.network.ApiServices
import com.mredrock.cyxbs.lib.utils.extensions.launchCatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.util.concurrent.TimeUnit

/**
 * @author zixuan
 * 2019/11/20
 */
class DiscoverHomeViewModel : BaseViewModel() {
  val viewPagerInfo = MutableLiveData<List<RollerViewInfo>>()
  val jwNews = MutableLiveData<List<NewsListItem>>()
  val hasUnread = MutableLiveData<Boolean>()
  var functionRvState: Parcelable? = null
  private val apiServices: ApiServices by lazy {
    ApiGenerator.createSelfRetrofit(okHttpClientConfig = {
      it.apply {
        callTimeout(10, TimeUnit.SECONDS)
        readTimeout(2, TimeUnit.SECONDS)
        writeTimeout(2, TimeUnit.SECONDS)
        retryOnConnectionFailure(true)
      }
    }, tokenNeeded = true).create(ApiServices::class.java)
  }
  
  init {
    getRollInfo()
//    getHasUnread()
    getNotificationUnReadStatus()
    getJwNews()
  }
  
  private fun getRollInfo() {
    apiServices.getRollerViewInfo()
      .mapOrThrowApiException()
      .setSchedulers()
      .unsafeSubscribeBy {
        viewPagerInfo.value = it
      }
      .lifeCycle()
  }

  /**
   * 该网络请求的接口好像还在用，暂时不动它
   */
  private fun getHasUnread() {
    apiServices.getHashUnreadMsg()
      .mapOrThrowApiException()
      .setSchedulers()
      .unsafeSubscribeBy {
        hasUnread.value = it.has
      }
      .lifeCycle()
  }
  
  private fun getJwNews() {
    apiServices.getNewsList(1)
      .mapOrThrowApiException()
      .setSchedulers()
      .unsafeSubscribeBy {
        jwNews.value = it
      }
      .lifeCycle()
  }

  /**
   * 用携程异步获取未读的(新的)notification数量
   */
  fun getNotificationUnReadStatus() {
    viewModelScope.launchCatch {
      val uFieldActivityList = async(Dispatchers.IO) {
        apiServices.getUFieldActivityList() }
      val itineraryList = listOf(
        async(Dispatchers.IO) { apiServices.getSentItinerary() },
        async(Dispatchers.IO) { apiServices.getReceivedItinerary() }
      )
      uFieldActivityList.await().apply {
        if (isSuccess() && data.any { !it.clicked }){
          hasUnread.value = true
          return@launchCatch
        }
      }
      itineraryList.awaitAll().apply {
        if (this[0].isSuccess() && this[0].data.any { !it.hasRead }) {
          hasUnread.value = true
          return@launchCatch
        }
        if (this[1].isSuccess() && this[1].data.any { !it.hasRead }) {
          hasUnread.value = true
          return@launchCatch
        }
      }
      hasUnread.value = false

    }.catch {
      it.printStackTrace()
//      "获取最新消息失败,请检查网络连接".toast()
    }
  }
}
