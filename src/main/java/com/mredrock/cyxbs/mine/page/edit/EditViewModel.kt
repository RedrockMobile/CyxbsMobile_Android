package com.mredrock.cyxbs.mine.page.edit

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalStatus
import com.mredrock.cyxbs.mine.util.user
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Created by zia on 2018/8/26.
 */
class EditViewModel : BaseViewModel() {

    val updateInfoEvent = MutableLiveData<Boolean>()
    val upLoadImageEvent = MutableLiveData<Boolean>()

    fun updateUserInfo(nickname: String, introduction: String, qq: String, phone: String
                       , photoThumbnailSrc: String = user?.photoThumbnailSrc ?: ""
                       , photoSrc: String = user?.photoSrc ?: "", callback: () -> Unit) {
        apiService.updateUserInfo(user?.stuNum ?: return, user?.idNum ?: return,
                nickname, introduction, qq, phone, photoThumbnailSrc, photoSrc)
                .normalStatus(this)
                .safeSubscribeBy(
                        onNext = {
                            user?.let {
                                it.nickname = nickname
                                it.introduction = introduction
                                it.qq = qq
                                it.phone = phone
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
        user?.stuNum?.let { stu ->
            user?.idNum?.let { id ->
                apiService.uploadSocialImg(stuNum, file)
                        .mapOrThrowApiException()
                        .flatMap {
                            user?.photoSrc = it.photosrc
                            user?.photoThumbnailSrc = it.thumbnail_src
                            apiService.updateUserImage(stu, id
                                    , it.thumbnail_src, it.photosrc)
                        }
                        .normalStatus(this)
                        .safeSubscribeBy(onError = { upLoadImageEvent.value = false }
                                , onNext = { upLoadImageEvent.value = true })
                        .lifeCycle()

            }

        }
    }
}