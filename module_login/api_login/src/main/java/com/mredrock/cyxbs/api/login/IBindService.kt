package com.mredrock.cyxbs.api.login

import com.alibaba.android.arouter.facade.template.IProvider
import kotlinx.coroutines.flow.SharedFlow

/**
 * @author : why
 * @time   : 2022/8/12 11:22
 * @bless  : God bless my code
 */
interface IBindService : IProvider {

    /**
     * 判断绑定ids操作是否成功的 SharedFlow
     */
    val bindEvent: SharedFlow<Boolean>
}