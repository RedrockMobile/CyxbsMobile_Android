package com.mredrock.cyxbs.mine.page.edit

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.account.IUserEditorService
import com.mredrock.cyxbs.common.service.account.IUserService
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.apiServiceForSign
import com.mredrock.cyxbs.mine.util.extension.normalStatus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Created by zia on 2018/8/26.
 */
class EditViewModel : BaseViewModel() {

    private val userService: IUserService by lazy {
        ServiceManager.getService(IAccountService::class.java).getUserService()
    }

    private val userEditService: IUserEditorService by lazy {
        ServiceManager.getService(IAccountService::class.java).getUserEditorService()
    }

    val updateInfoEvent = MutableLiveData<Boolean>()
    val upLoadImageEvent = MutableLiveData<Boolean>()

    fun updateUserInfo(nickname: String, introduction: String, qq: String, phone: String
                       , photoThumbnailSrc: String = userService.getAvatarImgUrl()
                       , photoSrc: String = userService.getAvatarImgUrl()) {


        apiServiceForSign.updateUserInfo(nickname, introduction, qq, phone, photoThumbnailSrc, photoSrc)
                .normalStatus(this)
                .observeOn(Schedulers.io())
                .map {
                    //setInfo请求后，本地数据需要调用refresh接口刷新并保存即时的用户信息到sp
                    ServiceManager.getService(IAccountService::class.java).getVerifyService().refresh(
                            onError = {
                                updateInfoEvent.postValue(false)
                            },
                            action = { s: String ->
                                updateInfoEvent.postValue(true)
                            }
                    )
                    it
                }
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribeBy(
                )
                .lifeCycle()
    }

    fun uploadAvatar(requestBody: RequestBody,
                     file: MultipartBody.Part) {
        apiService.uploadSocialImg(requestBody, file)
                .mapOrThrowApiException()
                .flatMap {
                    userEditService.apply {
                        setAvatarImgUrl(it.photosrc)
                    }
                    apiServiceForSign.updateUserImage( it.thumbnail_src, it.photosrc)
                }
                .normalStatus(this)
                .safeSubscribeBy(onError = { upLoadImageEvent.value = false }
                        , onNext = { upLoadImageEvent.value = true })
                .lifeCycle()
    }
}