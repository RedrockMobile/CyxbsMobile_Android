package com.mredrock.cyxbs.store.page.exchange.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiException
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.store.bean.ExchangeState
import com.mredrock.cyxbs.store.bean.ProductDetail
import com.mredrock.cyxbs.store.network.ApiService
import com.mredrock.cyxbs.store.utils.StoreType

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 11:56
 */
class ProductExchangeViewModel : BaseViewModel() {
    // 商品详细页内容
    val productDetail by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<ProductDetail>() }

    // 兑换成功
    val exchangeResult by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<ExchangeState>() }

    // 兑换失败
    val exchangeError by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<Int>() }

    fun getProductDetail(id: String) {
        ApiGenerator.getApiService(ApiService::class.java)
            .getProductDetail(id)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    toast("获取商品详情失败")
                },
                onNext = {
                    productDetail.postValue(it)
                }
            )
            .lifeCycle()
    }

    fun getExchangeResult(id: String) {
        ApiGenerator.getApiService(ApiService::class.java)
            .buyProduct(id)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    if (it is RedrockApiException) { // 学长封装的错误, 用于请求虽然成功, 但 statue 不为 10000 时
                        exchangeError.postValue(it.status) // 这里包括了 http 的请求成功的情况, 包含库存不足和积分不够
                    }else {
                        exchangeError.postValue(StoreType.ExchangeError.OTHER_ERROR) // 这里是其他网络错误
                    }
                },
                onNext = {
                    exchangeResult.postValue(it)
                }
            )
            .lifeCycle()
    }
}