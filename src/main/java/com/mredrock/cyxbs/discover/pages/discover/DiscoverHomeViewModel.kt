package com.mredrock.cyxbs.discover.pages.discover

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.network.RollerViewInfo
import com.mredrock.cyxbs.discover.network.Services
import com.mredrock.cyxbs.discover.news.bean.NewsListItem
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


class DiscoverHomeViewModel : BaseViewModel() {
    val viewPagerInfos = MutableLiveData<List<RollerViewInfo>>()
    val jwNews = MutableLiveData<List<NewsListItem>>()
    var scrollFlag = true
    fun getRollInfos() {
        ApiGenerator.getApiService(Services::class.java)
                .getRollerViewInfo("5")
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    viewPagerInfos.value = it
                }
                .lifeCycle()
    }

    fun getJwNews(page: Int) {
        ApiGenerator.getApiService(Services::class.java)
                .getNewsList(page)
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    jwNews.value = it
                }
                .lifeCycle()
    }

    fun startSwitchViewPager(callback: () -> Unit) {
        Observable.interval(4444, TimeUnit.MILLISECONDS)
                .setSchedulers()
                .safeSubscribeBy {
                    callback.invoke()
                }.lifeCycle()
    }
}
