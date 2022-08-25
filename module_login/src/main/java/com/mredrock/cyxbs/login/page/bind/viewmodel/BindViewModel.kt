/**
 * @Author fxy
 * @Date 2019-12-10 20:44
 */

package com.mredrock.cyxbs.login.page.bind.viewmodel

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mredrock.cyxbs.api.login.IBindService
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.login.bean.IdsBean
import com.mredrock.cyxbs.login.bean.IdsStatus
import com.mredrock.cyxbs.login.network.BindIdsApiService
import com.mredrock.cyxbs.login.service.BindServiceImpl
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class BindViewModel : BaseViewModel() {
    companion object {
        const val STATUS_OK = "10000"
        const val ERROR = "10010"
    }


    private var isAnimating = false

    private val bindIdsApiService = ApiGenerator.getApiService(BindIdsApiService::class)

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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                sleepThread(startTime)
            }
            .doOnError {
                sleepThread(startTime)
            }
            .safeSubscribeBy(
                onNext = {
                    viewModelScope.launch {
                        bindServiceImpl._bindEvent.emit(true)
                    }
                    (ServiceManager(IBindService::class) as BindServiceImpl)
                    "绑定成功".toast()
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
                            viewModelScope.launch {
                                bindServiceImpl._bindEvent.emit(false)
                            }
                            "绑定失败".toast()
                        }
                    } else {
                        viewModelScope.launch {
                            bindServiceImpl._bindEvent.emit(false)
                        }
                        "绑定失败".toast()
                    }
                    isAnimating = false
                }
            )
    }

    private fun sleepThread(startTime: Long) {
        val curTime = System.currentTimeMillis()
        val waitTime = 1500L
        if (curTime - startTime < waitTime) {
            Thread.sleep(waitTime - curTime + startTime)
        }
    }
}