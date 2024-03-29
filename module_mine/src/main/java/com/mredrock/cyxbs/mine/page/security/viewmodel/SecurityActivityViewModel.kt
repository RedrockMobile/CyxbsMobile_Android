package com.mredrock.cyxbs.mine.page.security.viewmodel

import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.util.apiService

/**
 * Author: RayleighZ
 * Time: 2020-11-29 20:36
 */
class SecurityActivityViewModel : BaseViewModel() {
    var netRequestSuccess = false
    var canClick = false
    var isSetProtect = false
    var isBindingEmail = false

    fun checkBinding(onSuccess: () -> Unit) {
        apiService.checkBinding(
                ServiceManager(IAccountService::class).getUserService().getStuNum()
        )
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    toast("对不起，获取是否绑定邮箱和密保失败，错误原因:$it")
                    true
                }
                .unsafeSubscribeBy {
                    val bindingResponse = it.data
                    isBindingEmail = bindingResponse.email_is != 0
                    isSetProtect = bindingResponse.question_is != 0
                    canClick = true
                    netRequestSuccess = true
                    onSuccess()
                }.lifeCycle()
    }
}