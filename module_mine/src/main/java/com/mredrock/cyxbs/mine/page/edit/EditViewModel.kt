package com.mredrock.cyxbs.mine.page.edit

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.CommonApiService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserService
import com.mredrock.cyxbs.common.utils.down.bean.DownMessageText
import com.mredrock.cyxbs.common.utils.down.params.DownMessageParams
import com.mredrock.cyxbs.common.utils.extensions.errorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalStatus
import com.mredrock.cyxbs.mine.util.widget.ExecuteOnceObserver
import com.mredrock.cyxbs.api.store.IStoreService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Created by zia on 2018/8/26.
 */
class EditViewModel : BaseViewModel() {

    private val userService: IUserService by lazy {
        ServiceManager(IAccountService::class).getUserService()
    }

    val updateInfoEvent = MutableLiveData<Boolean>()
    val upLoadImageEvent = MutableLiveData<Boolean>()

    fun updateUserInfo(nickname: String, introduction: String, qq: String, phone: String
                       , gender: String, birthday: String
                       , photoUrl: String = userService.getAvatarImgUrl()) {

        apiService.updateUserInfo(nickname, introduction, qq, phone, photoUrl, gender, birthday)
                .normalStatus(this)
                .doOnError {
                    updateInfoEvent.postValue(false)
                }
                .unsafeSubscribeBy {
                    updateInfoEvent.postValue(true)
                    if (nickname.isNotBlank() && introduction.isNotBlank() && qq.isNotBlank() && phone.isNotBlank()) {
                      ServiceManager(IStoreService::class)
                        .postTask(IStoreService.Task.EDIT_INFO, null)
                    }
                }
                .lifeCycle()
    }

    fun uploadAvatar(requestBody: RequestBody,
                     file: MultipartBody.Part) {
        apiService.uploadSocialImg(requestBody, file)
                .mapOrThrowApiException()
                .flatMap {
                    apiService.updateUserImage(it.thumbnail_src, it.photosrc)
                }
                .normalStatus(this)
                .doOnError { upLoadImageEvent.postValue(false) }
                .unsafeSubscribeBy {
                    upLoadImageEvent.postValue(true)
                }
                .lifeCycle()
    }


    val portraitAgreementList: MutableList<DownMessageText> = mutableListOf()

    fun getPortraitAgreement(successCallBack: () -> Unit) {
        val key = "zscy-portrait-agreement"
        val time = System.currentTimeMillis()
        ApiGenerator.getCommonApiService(CommonApiService::class.java)
                .getDownMessage(DownMessageParams(key))
                .setSchedulers(observeOn = Schedulers.io())
                .errorHandler()
                .doOnNext {
                    //有时候网路慢会转一下圈圈，但是有时候网络快，圈圈就像是闪了一下，像bug，就让它最少转一秒吧
                    val l = 1000 - (System.currentTimeMillis() - time)
                    Thread.sleep(if (l > 0) l else 0)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ExecuteOnceObserver(
                        onExecuteOnceNext = {
                            portraitAgreementList.clear()
                            portraitAgreementList.addAll(it.data.textList)
                            successCallBack()
                        }
                ))
    }
}