/**
 * @Author fxy
 * @Date 2019-12-10 20:44
 */

package com.mredrock.cyxbs.login.page.bind.viewmodel

import com.google.gson.Gson
import com.mredrock.cyxbs.api.login.IBindService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.login.bean.IdsBean
import com.mredrock.cyxbs.login.bean.IdsStatus
import com.mredrock.cyxbs.login.network.BindIdsApiService
import com.mredrock.cyxbs.login.service.BindServiceImpl
import retrofit2.HttpException

class BindViewModel : BaseViewModel() {
    companion object {
        const val STATUS_OK = "10000"
        const val ERROR = "10010"
    }


    private var isAnimating = false

    private val bindIdsApiService = ApiGenerator.getApiService(BindIdsApiService::class.java)

    /**
     * API接口实现类的实例，用于更新绑定结果的LiveData
     */
    private val bindServiceImpl = ServiceManager(IBindService::class) as BindServiceImpl


    fun bindIds(idsNum: String, idsPassword: String, bubble: () -> Unit) {
        if (isAnimating) {
            return
        }
        isAnimating = true
        bubble.invoke()
        val startTime = System.currentTimeMillis()
        bindIdsApiService.bindIds(IdsBean(idsNum, idsPassword))
            .doOnNext {
                sleepThread(startTime)
            }
            .doOnError {
                sleepThread(startTime)
            }
            .setSchedulers()
            .safeSubscribeBy(
                onNext = {
                    bindServiceImpl._isBindSuccess.postValue(true)
                    (ServiceManager(IBindService::class) as BindServiceImpl)
                    BaseApp.appContext.toast("绑定成功")
                    bubble.invoke()
                    isAnimating = false
                },
                onError = {
                    bubble.invoke()
                    //密码错误的话,会导致状态码为400，Retrofit无法回调onNext
                    //详见：https://www.cnblogs.com/fuyaozhishang/p/8607706.html
                    // todo 以后学弟重构的话，记得让后端该下逻辑
                    if (it is HttpException && it.code() == 400) {
                        val body = (it).response()?.errorBody() ?: return@safeSubscribeBy
                        val data = Gson().fromJson(body.string(), IdsStatus::class.java)
                        if (data.errorCode == ERROR) {
                            bindServiceImpl._isBindSuccess.postValue(false)
                            BaseApp.appContext.toast("绑定失败")
                        }
                    } else {
                        bindServiceImpl._isBindSuccess.postValue(false)
                        BaseApp.appContext.toast("绑定失败")
                    }
                    isAnimating = false
                }
            ).lifeCycle()
    }

    private fun sleepThread(startTime: Long) {
        val curTime = System.currentTimeMillis()
        val waitTime = 1500L
        if (curTime - startTime < waitTime) {
            Thread.sleep(waitTime - curTime + startTime)
        }
    }
}