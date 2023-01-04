package com.mredrock.cyxbs.discover.pages.discover

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.bean.NewsListItem
import com.mredrock.cyxbs.discover.network.RollerViewInfo
import com.mredrock.cyxbs.discover.network.ApiServices
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
    getHasUnread()
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
}
