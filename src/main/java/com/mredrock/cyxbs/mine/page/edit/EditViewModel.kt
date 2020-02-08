package com.mredrock.cyxbs.mine.page.edit

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IUserEditorService
import com.mredrock.cyxbs.common.service.account.IUserService
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalStatus
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Created by zia on 2018/8/26.
 */
class EditViewModel : BaseViewModel() {

    val updateInfoEvent = MutableLiveData<Boolean>()
    val upLoadImageEvent = MutableLiveData<Boolean>()

    fun updateUserInfo(nickname: String, introduction: String, qq: String, phone: String
                       , photoThumbnailSrc: String = ServiceManager.getService(IUserService::class.java).getAvatarImgUrl()
                       , photoSrc: String = ServiceManager.getService(IUserService::class.java).getAvatarImgUrl(), callback: () -> Unit) {
        val stuNum = ServiceManager.getService(IUserService::class.java).getStuNum()
        val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")
                ?: return
        apiService.updateUserInfo(stuNum, idNum,
                nickname, introduction, qq, phone, photoThumbnailSrc, photoSrc)
                .normalStatus(this)
                .safeSubscribeBy(
                        onNext = {
                            //更新User信息
                            val userEditService = ServiceManager.getService(IUserEditorService::class.java)
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

    fun uploadAvatar(stuNum: RequestBody,
                     file: MultipartBody.Part) {
        val stu = ServiceManager.getService(IUserService::class.java).getStuNum()
        val id = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")
                ?: return
        apiService.uploadSocialImg(stuNum, file)
                .mapOrThrowApiException()
                .flatMap {
                    val userEditService = ServiceManager.getService(IUserEditorService::class.java)
                    userEditService.apply {
                        setAvatarImgUrl(it.photosrc)
                    }
                    apiService.updateUserImage(stu, id
                            , it.thumbnail_src, it.photosrc)
                }
                .normalStatus(this)
                .safeSubscribeBy(onError = { upLoadImageEvent.value = false }
                        , onNext = { upLoadImageEvent.value = true })
                .lifeCycle()


    }
}