package com.mredrock.cyxbs.discover.pages.discover

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.config.END_POINT_REDROCK
import com.mredrock.cyxbs.common.config.END_POINT_REDROCK_VERSION_TWO
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.converter.QualifiedTypeConverterFactory
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.network.RollerViewInfo
import com.mredrock.cyxbs.discover.network.Services
import com.mredrock.cyxbs.discover.news.bean.NewsListItem
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author zixuan
 * 2019/11/20
 */
class DiscoverHomeViewModel : BaseViewModel() {
    val viewPagerInfos = MutableLiveData<List<RollerViewInfo>>()
    val jwNews = MutableLiveData<List<NewsListItem>>()
    val viewPagerTurner = MutableLiveData<Int>()
    var disposable: Disposable? = null
    private val retrofit =  Retrofit.Builder()
            .baseUrl(END_POINT_REDROCK_VERSION_TWO)
            .addConverterFactory(QualifiedTypeConverterFactory(GsonConverterFactory.create(), SimpleXmlConverterFactory.create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    //标记是否未经被滑动，被滑动就取消下一次自动滚动
    var scrollFlag = true
    fun getRollInfos() {
        ApiGenerator.getApiService(retrofit ,Services::class.java)
                .getRollerViewInfo()
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

    fun startSwitchViewPager() {
        stopPageTurner()
        disposable = Observable.interval(4444, TimeUnit.MILLISECONDS)
                .setSchedulers()
                .safeSubscribeBy {
                    viewPagerTurner.setValue(viewPagerTurner.value?:1)
                }.lifeCycle()
    }

    fun stopPageTurner(){
        disposable?.dispose()
        disposable = null
    }
}
