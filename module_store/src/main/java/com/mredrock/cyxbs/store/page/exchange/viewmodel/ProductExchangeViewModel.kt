package com.mredrock.cyxbs.store.page.exchange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.lib.utils.network.ApiException
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.store.bean.ExchangeState
import com.mredrock.cyxbs.store.bean.ProductDetail
import com.mredrock.cyxbs.store.network.ApiService
import com.mredrock.cyxbs.store.utils.StoreType
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 11:56
 */
class ProductExchangeViewModel : BaseViewModel() {
    // 商品详细页内容
    private val _productDetail = MutableLiveData<ProductDetail>()
    val productDetail: LiveData<ProductDetail>
        get() = _productDetail

    // 兑换成功
    private val _exchangeResult = MutableLiveData<ExchangeState>()
    val exchangeResult: LiveData<ExchangeState>
        get() = _exchangeResult

    // 兑换失败
    private val _exchangeError = MutableLiveData<Int>()
    val exchangeError: LiveData<Int>
        get() = _exchangeError

    fun getProductDetail(id: String) {
        ApiService::class.api
            .getProductDetail(id)
            .mapOrThrowApiException()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                toast("获取商品详情失败")
            }.safeSubscribeBy {
                _productDetail.postValue(it)
            }
    }

    fun getExchangeResult(id: String) {
        ApiService::class.api
            .buyProduct(id)
            .mapOrThrowApiException()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                if (it is ApiException) { // 学长封装的错误, 用于请求虽然成功, 但 statue 不为 10000 时
                    _exchangeError.postValue(it.status) // 这里包括了 http 的请求成功的情况, 包含库存不足和积分不够
                } else {
                    _exchangeError.postValue(StoreType.ExchangeError.OTHER_ERROR) // 这里是其他网络错误
                }
            }
            .safeSubscribeBy {
                _exchangeResult.postValue(it)
            }
    }
}