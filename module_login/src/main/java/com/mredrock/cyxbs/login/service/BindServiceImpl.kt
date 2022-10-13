package com.mredrock.cyxbs.login.service

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserStateService
import com.mredrock.cyxbs.api.login.IBindService
import com.mredrock.cyxbs.api.login.LOGIN_BIND_SERVICE
import com.mredrock.cyxbs.lib.utils.extensions.launch
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.service.impl
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * @author : why
 * @time   : 2022/8/12 11:32
 * @bless  : God bless my code
 */

@Route(path = LOGIN_BIND_SERVICE)
class BindServiceImpl : IBindService {
    // 判断是否绑定成功的SharedFlow
    internal val _bindEvent = MutableSharedFlow<Boolean>()
    
    // 判断是否绑定成功的 LiveData
    private val _bindLiveData = MutableLiveData<Boolean>()
    
    override val bindEvent: SharedFlow<Boolean>
        get() = _bindEvent
    override val bindLiveData: LiveData<Boolean>
        get() = _bindLiveData
    
    override fun init(context: Context?) {
        IAccountService::class.impl
            .getVerifyService()
            .observeUserStateEvent()
            .unsafeSubscribeBy {
                if (it == IUserStateService.UserState.NOT_LOGIN) {
                    launch {
                        _bindEvent.emit(false)
                    }
                }
            }
        launch {
            bindEvent.collect {
                _bindLiveData.value = it
            }
        }
    }
}