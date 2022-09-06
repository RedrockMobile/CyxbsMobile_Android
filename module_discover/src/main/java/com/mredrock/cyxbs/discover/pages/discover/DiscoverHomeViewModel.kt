package com.mredrock.cyxbs.discover.pages.discover

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.bean.NewsListItem
import com.mredrock.cyxbs.discover.network.RollerViewInfo
import com.mredrock.cyxbs.discover.network.ApiServices
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * @author zixuan
 * 2019/11/20
 */
class DiscoverHomeViewModel : BaseViewModel() {
    val viewPagerInfo = MutableLiveData<List<RollerViewInfo>>()
    val jwNews = MutableLiveData<List<NewsListItem>>()
    val viewPagerTurner = MutableLiveData<Int>()
    val hasUnread = MutableLiveData<Boolean>()
    var disposable: Disposable? = null
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

    //标记是否未经被滑动，被滑动就取消下一次自动滚动
    var scrollFlag = true
    fun getRollInfo() {
        apiServices.getRollerViewInfo()
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    viewPagerInfo.value = it
                }
                .lifeCycle()
    }

    fun getHasUnread(){
        apiServices.getHashUnreadMsg()
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy {
                hasUnread.value = it.has
            }
            .lifeCycle()
    }

    fun getJwNews(page: Int) {
        apiServices.getNewsList(page)
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    jwNews.value = it
                }
                .lifeCycle()
    }

    fun startSwitchViewPager() {
        stopPageTurner()
        disposable = Observable.interval(4444, TimeUnit.MILLISECONDS)
                .setSchedulers()
                .safeSubscribeBy {
                    viewPagerTurner.setValue(viewPagerTurner.value ?: 1)
                }.lifeCycle()
    }

    fun stopPageTurner() {
        disposable?.dispose()
        disposable = null
    }
}
