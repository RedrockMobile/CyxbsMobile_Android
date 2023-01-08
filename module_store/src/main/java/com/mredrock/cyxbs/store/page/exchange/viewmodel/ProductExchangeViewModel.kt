package com.mredrock.cyxbs.store.page.exchange.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
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
class ProductExchangeViewModel(
    shopId: Int
) : BaseViewModel() {
    
    // 商品详细页内容
    private val _productDetail = MutableLiveData<ProductDetail>()
    val productDetail: LiveData<ProductDetail>
        get() = _productDetail

    // 兑换成功（状态）
    private val _exchangeResultState = MutableLiveData<ExchangeState>()
    val exchangeResultState: LiveData<ExchangeState>
        get() = _exchangeResultState
    // 兑换成功（事件）
    val exchangeResultEvent = _exchangeResultState.asShareFlow()

    // 兑换失败（状态）
    private val _exchangeErrorState = MutableLiveData<Int>()
    val exchangeErrorState: LiveData<Int>
        get() = _exchangeErrorState
    // 兑换失败（事件）
    val exchangeErrorEvent = _exchangeErrorState.asShareFlow()
    
    init {
      getProductDetail(shopId) // 只需要这里加载一次即可
    }

    private fun getProductDetail(id: Int) {
        ApiService::class.api
            .getProductDetail(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                toast("获取商品详情失败")
            }.safeSubscribeBy {
                _productDetail.postValue(it)
            }
    }

    fun getExchangeResult(id: Int) {
        ApiService::class.api
            .buyProduct(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                ApiException {
                    _exchangeErrorState.postValue(it.status) // 这里包括了 http 的请求成功的情况, 包含库存不足和积分不够
                }.catchOther {
                    _exchangeErrorState.postValue(StoreType.ExchangeError.OTHER_ERROR) // 这里是其他网络错误
                }
            }.safeSubscribeBy {
                _exchangeResultState.postValue(it)
            }
    }
}