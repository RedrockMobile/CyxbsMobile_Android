package com.mredrock.cyxbs.api.login

import androidx.lifecycle.LiveData
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author : why
 * @time   : 2022/8/12 11:22
 * @bless  : God bless my code
 */
interface IBindService : IProvider {

    /**
     * 判断绑定ids操作是否成功的LiveData
     */
    val isBindSuccess: LiveData<Boolean>

    /**
     * 判断绑定ids操作是否成功的boolean值(默认为false)
     */
    val isBindSuccessBoolean: Boolean
}