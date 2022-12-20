package com.mredrock.cyxbs.mine.page.sign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.mine.network.model.ScoreStatus
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalWrapper
import com.mredrock.cyxbs.api.store.IStoreService
import io.reactivex.rxjava3.functions.Function

/**
 * Created by zia on 2018/8/22.
 * 需要确保用户已登录
 */
class DailyViewModel : BaseViewModel() {

    //签到状态
    private val _status = MutableLiveData<ScoreStatus>()
    val status: LiveData<ScoreStatus>
        get() = _status

    //作为唯一置信源，用来弹出寒暑假不可签到的toast，同时为了解决LiveData粘性事件的限制，采用SingleLiveEvent
    private val _isInVacation = SingleLiveEvent<Boolean>()
    val isInVacation: LiveData<Boolean>
        get() = _isInVacation

    fun loadAllData() {
        apiService.getScoreStatus()
                .normalWrapper(this)
                .unsafeSubscribeBy(
                        onNext = {
                            _status.postValue(it)
                        },
                        onError = {
                            BaseApp.appContext.toast("获取积分失败")
                        }
                )
                .lifeCycle()
    }

    //用flatmap解决嵌套请求的问题
    fun checkIn() {
        apiService.checkIn()
                .flatMap(Function {
                    //如果status为405，说明是在寒暑假，此时不可签到
                    if (it.status == 405) {
                        _isInVacation.postValue(true)
                    }
                    return@Function apiService.getScoreStatus()
                })
                .normalWrapper(this)
                .unsafeSubscribeBy(
                        onNext = {
                            _status.postValue(it)
                          ServiceManager.getService(IStoreService::class.java)
                            .postTask(IStoreService.Task.DAILY_SIGN, "")
                        },
                        onError = {
                            BaseApp.appContext.toast("签到失败")
                        }
                )
                .lifeCycle()
    }
}
