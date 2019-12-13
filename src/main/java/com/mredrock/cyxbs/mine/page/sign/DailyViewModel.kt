package com.mredrock.cyxbs.mine.page.sign

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
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

    val status = MutableLiveData<ScoreStatus>()//签到状态

    fun loadAllData(user: User) {
        apiService.getScoreStatus(user.stuNum ?: return, user.idNum ?: return)
                .normalWrapper(this)
                .safeSubscribeBy {
                    status.postValue(it)
                }
                .lifeCycle()


    }


    //用flatmap解决嵌套请求的问题
    fun checkIn(user: User) {
        apiService.checkIn(user.stuNum ?: return, user.idNum ?: return)
                .flatMap(Function<RedrockApiStatus, Observable<RedrockApiWrapper<ScoreStatus>>> {
                    return@Function user.stuNum?.let { stuNum ->
                        user.idNum?.let { idNum ->
                            apiService.getScoreStatus(stuNum, idNum)
                        }
                    }
                })
                .setSchedulers()
                .normalWrapper(this)
                .safeSubscribeBy {
                    status.postValue(it)
                }
                .lifeCycle()
    }
}
