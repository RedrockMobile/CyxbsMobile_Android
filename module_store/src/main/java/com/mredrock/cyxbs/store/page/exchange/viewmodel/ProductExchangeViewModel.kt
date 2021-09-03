package com.mredrock.cyxbs.store.page.exchange.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.bean.ExchangeState
import com.mredrock.cyxbs.store.bean.ProductDetail
import com.mredrock.cyxbs.store.network.ApiService
import okhttp3.ResponseBody
import retrofit2.HttpException

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
    val exchangeError by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<String>() }

    fun getProductDetail(id: String) {
        ApiGenerator.getApiService(ApiService::class.java)
            .getProductDetail(id)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    toastEvent.postValue(R.string.store_product_detail_failure)
                },
                onNext = {
                    productDetail.postValue(it)
                }
            )
    }

    fun getExchangeResult(id: String) {
        ApiGenerator.getApiService(ApiService::class.java)
            .buyProduct(id)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    if (it is HttpException) {
                        // 在库存不足和邮票不够时接口都返回 http 的错误码 500 导致回调到 onError 方法 所以这里手动拿到返回的 bean 类
                        val responseBody: ResponseBody? = it.response()?.errorBody()
                        val code = it.response()?.code()//返回的code 如果为500即为余额不足/库存不足
                        if (code == 500) {
                            val gson = Gson()
                            val exchangeState = gson.fromJson(responseBody?.string(), RedrockApiStatus::class.java)
                            exchangeError.postValue(exchangeState.info)
                        } else {
                            toastEvent.postValue(R.string.store_exchange_product_failure)
                        }
                    }
                },
                onNext = {
                    exchangeResult.postValue(it)
                }
            )
    }
}