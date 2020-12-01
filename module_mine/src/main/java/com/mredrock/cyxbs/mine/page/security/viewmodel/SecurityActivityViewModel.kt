package com.mredrock.cyxbs.mine.page.security.viewmodel

import com.mredrock.cyxbs.account.IAccountService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.ApiService
import com.mredrock.cyxbs.mine.network.model.StuNumBody
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

    fun checkBinding(onSuccess: ()-> Unit){
        apiService.checkBinding(
                ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()
        ).setSchedulers().safeSubscribeBy(
                onError = {
                    BaseApp.context.toast("对不起，获取是否绑定邮箱和密保失败")
                },
                onNext = {
                    val bindingResponse = it.data
                    isBindingEmail = bindingResponse.email_is != 0
                    isSetProtect = bindingResponse.question_is != 0
                    canClick = true
                    netRequestSuccess = true
                    onSuccess()
                }
        ).lifeCycle()
    }
}