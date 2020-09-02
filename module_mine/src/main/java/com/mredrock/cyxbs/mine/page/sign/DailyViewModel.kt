package com.mredrock.cyxbs.mine.page.sign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.mine.network.model.Product
import com.mredrock.cyxbs.mine.network.model.ScoreStatus
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalWrapper
import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * Created by zia on 2018/8/22.
 * 需要确保用户已登录
 */
class DailyViewModel : BaseViewModel() {

    //签到状态
    private val _status = MutableLiveData<ScoreStatus>()
    val status: LiveData<ScoreStatus>
        get() = _status

    //积分商城物品
    //作为唯一置信源，驱动视图的更新
    private val _products = MutableLiveData<MutableList<Product>>()
    val products: LiveData<List<Product>> = Transformations.map(_products) {
        it.toList()
    }

    //作为唯一置信源，用来弹出寒暑假不可签到的toast，同时为了解决LiveData粘性事件的限制，采用SingleLiveEvent
    private val _isInVacation = SingleLiveEvent<Boolean>()
    val isInVacation: LiveData<Boolean>
        get() = _isInVacation

    //商品兑换的事件，兑换成功为true，兑换失败为false
    private val _exchangeEvent = SingleLiveEvent<Boolean>()
    val exchangeEvent: LiveData<Boolean>
        get() = _exchangeEvent

    private var page = 1

    fun loadAllData() {
        apiService.getScoreStatus()
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            _status.postValue(it)
                        },
                        onError = {
                            BaseApp.context.toast("获取积分失败")
                        }
                )
                .lifeCycle()


    }


    //用flatmap解决嵌套请求的问题
    fun checkIn() {

        apiService.checkIn()
                .flatMap(Function<RedrockApiStatus, Observable<RedrockApiWrapper<ScoreStatus>>> {
                    //如果status为405，说明是在寒暑假，此时不可签到
                    if (it.status == 405) {
                        _isInVacation.postValue(true)
                    }
                    return@Function apiService.getScoreStatus()
                })
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            _status.postValue(it)
                        },
                        onError = {
                            BaseApp.context.toast("签到失败")
                        }
                )
                .lifeCycle()
    }

    fun loadProduct() {
        apiService.getProducts(page++)
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            //由于Rxjava反射不应定能够够保证为空，当为空的说明这一页没有数据，于是停止加载
                            if (it.isEmpty()) {
                                return@safeSubscribeBy
                            }

                            //往_product中添加Product
                            val localProducts = _products.value ?: mutableListOf()
                            localProducts.addAll(it)
                            _products.postValue(localProducts)
                            //加载下一页
                            loadProduct()
                        },
                        onError = {
                            BaseApp.context.toast("加载物品失败")
                        }
                )
                .lifeCycle()
    }

    fun exchangeProduct(product: Product, position: Int) {
        //防止后端粗心的将integral设置为空，同时需要处理为小数的情况
        val productIntegral = if (product.integral.isEmpty()) 0 else product.integral.toFloat().toInt()

        apiService.exchangeProduct(product.name, productIntegral)
                .flatMap(Function<RedrockApiStatus, Observable<RedrockApiWrapper<ScoreStatus>>> {
                    if (it.status == 200) {
                        _exchangeEvent.postValue(true)
                        minusProductCount(product, position)
                    } else {
                        _exchangeEvent.postValue(false)
                    }
                    return@Function apiService.getScoreStatus()
                })
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            _status.postValue(it)
                        },
                        onError = {
                            BaseApp.context.toast("兑换失败")
                        }
                )
                .lifeCycle()
    }

    private fun minusProductCount(product: Product, position: Int) {
        //将product的count减1
        val list = _products.value ?: mutableListOf()
        if (list.size - 1 >= position) {
            val product = list[position]
            list[position] = Product(product.name, product.count.dec(), product.integral, product.src, product.isVirtual)
        }
        _products.postValue(list)
    }
}
