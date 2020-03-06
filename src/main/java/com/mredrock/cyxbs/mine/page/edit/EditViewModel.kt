package com.mredrock.cyxbs.mine.page.edit

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.account.IUserEditorService
import com.mredrock.cyxbs.common.service.account.IUserService
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.mine.network.model.UserLocal
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalStatus
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Created by zia on 2018/8/26.
 */
class EditViewModel : BaseViewModel() {

    //提示视图Fragment，User的信息已更新
    private val _isUserUpdate = SingleLiveEvent<Boolean>()
    val isUserUpdate: SingleLiveEvent<Boolean>
        get() = _isUserUpdate


    private val userService: IUserService by lazy {
        ServiceManager.getService(IAccountService::class.java).getUserService()
    }
    private val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")
    private val userEditService: IUserEditorService by lazy {
        ServiceManager.getService(IAccountService::class.java).getUserEditorService()
    }

    val updateInfoEvent = MutableLiveData<Boolean>()
    val upLoadImageEvent = MutableLiveData<Boolean>()

    fun updateUserInfo(nickname: String, introduction: String, qq: String, phone: String
                       , photoThumbnailSrc: String = userService.getAvatarImgUrl()
                       , photoSrc: String = userService.getAvatarImgUrl(), callback: () -> Unit) {


        apiService.updateUserInfo(userService.getStuNum(), idNum ?: return,
                nickname, introduction, qq, phone, photoThumbnailSrc, photoSrc)
                .normalStatus(this)
                .safeSubscribeBy(
                        onNext = {
                            //更新User信息

                            userEditService.apply {
                                setQQ(qq)
                                setNickname(nickname)
                                setIntroduction(introduction)
                                setPhone(phone)
                            }

                            updateInfoEvent.postValue(true)
                        },
                        onComplete = callback,
                        onError = {
                            updateInfoEvent.postValue(false)
                        }
                )
                .lifeCycle()
    }

    fun uploadAvatar(requestBody: RequestBody,
                     file: MultipartBody.Part) {
        val idNum = idNum ?: return
        apiService.uploadSocialImg(requestBody, file)
                .mapOrThrowApiException()
                .flatMap {
                    userEditService.apply {
                        setAvatarImgUrl(it.photosrc)
                    }
                    apiService.updateUserImage(userService.getStuNum(), idNum
                            , it.thumbnail_src, it.photosrc)
                }
                .normalStatus(this)
                .safeSubscribeBy(onError = { upLoadImageEvent.value = false }
                        , onNext = { upLoadImageEvent.value = true })
                .lifeCycle()


    }

    fun getUserInfo() {
        apiService.getPersonInfo(userService.getStuNum(), idNum ?: return)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy(onNext = {
                    //更新BaseUser
                    freshBaseUser(it)
                    _isUserUpdate.postValue(true)

                }).lifeCycle()
    }

    private fun freshBaseUser(user: UserLocal) {
        ServiceManager.getService(IAccountService::class.java).getUserEditorService().apply {
            setIntroduction(user.introduction)
            setNickname(user.nickname)
            setQQ(user.qq)
            setPhone(user.phone)
            setAvatarImgUrl(user.photoSrc)
        }

    }
}