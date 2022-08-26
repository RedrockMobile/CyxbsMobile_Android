package com.mredrock.cyxbs.mine.page.security.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.lib.utils.extensions.throwApiExceptionIfFail
import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.mine.network.ApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.RequestBody

/**
 * @author : why
 * @time   : 2022/8/21 18:46
 * @bless  : God bless my code
 */
class FindPasswordByIdsViewModel : BaseViewModel() {

    /**
     * 用于储存学号
     */
    var stuNum: String = ""


    /**
     * ApiService的实例
     */
    val apiService = ApiGenerator.getCommonApiService(ApiService::class)

    /**
     * 保存接口获取到的验证码（用于修改密码）
     */
    private var mCode: Int = -1


    /**
     * 用于观测获取请求是否出错
     */
    val isGetCodeSuccess: LiveData<Boolean> get() = _isGetCodeSuccess
    private val _isGetCodeSuccess = MutableLiveData<Boolean>()

    /**
     * 用于观测修改密码是否成功
     */
    val isChangeSuccess: LiveData<Boolean> get() = _isChangeSuccess
    private val _isChangeSuccess = MutableLiveData<Boolean>()

    /**
     * 获取验证码
     */
    fun getCode(requestBody: RequestBody) {
        apiService
            .getIdsCode(requestBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrThrowApiException()
            .doOnError {
                _isGetCodeSuccess.postValue(false)
            }
            .safeSubscribeBy {
                _isGetCodeSuccess.postValue(true)
                mCode = it.code
            }
    }

    /**
     * 更改密码
     * @param stuNum 学号
     * @param newPassword 新密码
     */
    fun changePassword(stuNum: String, newPassword: String) {
        apiService
            .changePasswordByIds(stuNum, newPassword, mCode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .throwApiExceptionIfFail()
            .doOnError {
                _isChangeSuccess.postValue(false)
            }
            .safeSubscribeBy {
                _isChangeSuccess.postValue(true)
            }
    }
}