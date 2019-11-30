package com.mredrock.cyxbs.mine.page.sign

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.ScoreStatus
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.normalWrapper

/**
 * Created by zia on 2018/8/22.
 * 需要确保用户已登录
 */
class DailyViewModel : BaseViewModel() {

    val status = MutableLiveData<ScoreStatus>()//签到状态

    //伪造的后端数据，仅供测试使用
    val fakeStatus = MutableLiveData<Array<Int>>()

    fun loadAllData(user: User) {
        if (user.stuNum != null && user.idNum != null) {
            apiService.getScoreStatus(user.stuNum!!, user.idNum!!)
                    .normalWrapper(this)
                    .safeSubscribeBy {
                        status.postValue(it)
                    }
                    .lifeCycle()
        }

    }

    fun checkIn(user: User, action: () -> Unit) {
        if (user.stuNum != null && user.idNum != null) {
            apiService.checkIn(user.stuNum!!, user.idNum!!)
                    .setSchedulers()
                    .subscribe {
                        if (it.status == 200) {
                            action.invoke()
                        }
                    }
                    .lifeCycle()
        }
    }
}
