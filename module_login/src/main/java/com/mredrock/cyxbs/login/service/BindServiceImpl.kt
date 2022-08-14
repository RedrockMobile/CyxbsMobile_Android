package com.mredrock.cyxbs.login.service

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.login.IBindService
import com.mredrock.cyxbs.api.login.LOGIN_BIND_SERVICE

/**
 * @author : why
 * @time   : 2022/8/12 11:32
 * @bless  : God bless my code
 */

@Route(path = LOGIN_BIND_SERVICE)
class BindServiceImpl : IBindService {

    //判断是否绑定成功的boolean值
    override val isBindSuccessBoolean: Boolean
        get() = isBindSuccess.value ?: false

    // 判断是否绑定成功的LiveData
    internal val _isBindSuccess = MutableLiveData<Boolean>()
    override val isBindSuccess: LiveData<Boolean>
        get() = _isBindSuccess

    override fun init(context: Context?) {
    }
}