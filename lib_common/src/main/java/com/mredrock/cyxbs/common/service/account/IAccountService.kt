package com.mredrock.cyxbs.common.service.account

import com.alibaba.android.arouter.facade.template.IProvider

/**
 * Created By jay68 on 2019-11-12.
 */
interface IAccountService : IProvider {
    //拆分成几个接口的原因是方法较多，归一下类，通常情况下服务接口只需要一个就好了
    fun getUserService(): IUserService

    fun getVerifyService(): IUserStateService

    fun getUserEditorService(): IUserEditorService

    fun getUserTokenService(): IUserTokenService
}




