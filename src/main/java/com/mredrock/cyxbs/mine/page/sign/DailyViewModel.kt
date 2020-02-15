package com.mredrock.cyxbs.mine.page.sign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.mine.network.ApiGeneratorForSign
import com.mredrock.cyxbs.mine.network.ApiService
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

    val stuNum = ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()
    val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")
    //临时使用，因为后端测试的baseurl与apiService不同，故作区分
    private val apiServiceForSign: ApiService by lazy { ApiGeneratorForSign.getApiService(ApiService::class.java) }

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

    private var page = 1

    fun loadAllData() {
        val stuNum = ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()
        val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")
                ?: return
        apiService.getScoreStatus(stuNum, idNum)
                .normalWrapper(this)
                .safeSubscribeBy {
                    _status.postValue(it)
                }
                .lifeCycle()


    }


    //用flatmap解决嵌套请求的问题
    fun checkIn() {

        apiService.checkIn(stuNum, idNum ?: return)
                .flatMap(Function<RedrockApiStatus, Observable<RedrockApiWrapper<ScoreStatus>>> {
                    //如果status为405，说明是在寒暑假，此时不可签到
                    if (it.status == 405) {
                        _isInVacation.postValue(true)
                    }
                    return@Function apiService.getScoreStatus(stuNum, idNum)
                })
                .normalWrapper(this)
                .safeSubscribeBy {
                    _status.postValue(it)
                }
                .lifeCycle()
    }

    fun loadProduct() {
        apiServiceForSign.getProducts(stuNum, idNum ?: return, page++)
                .normalWrapper(this)
                .safeSubscribeBy {
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
                }
                .lifeCycle()
    }

    fun exchangeProduct(product: Product) {
        apiServiceForSign.exchangeProduct(stuNum, idNum, product.name, product.integral.toInt())
                .flatMap(Function<RedrockApiStatus, Observable<RedrockApiWrapper<ScoreStatus>>> {
                    return@Function apiService.getScoreStatus(stuNum, idNum)
                })
                .normalWrapper(this)
                .safeSubscribeBy {
                    _status.postValue(it)
                }
                .lifeCycle()
    }
}
