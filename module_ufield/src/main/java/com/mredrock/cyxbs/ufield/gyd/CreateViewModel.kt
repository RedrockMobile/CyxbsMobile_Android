package com.mredrock.cyxbs.ufield.gyd

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.ufield.gyd.network.PublishApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 *
 * author : 苟云东
 * date : 2023/8/15 16:45
 */
class CreateViewModel : BaseViewModel() {

    private val _publishStatus = MutableLiveData<Boolean>()

    val publishStatus: LiveData<Boolean>
        get() = _publishStatus

    @SuppressLint("CheckResult")
    fun postActivityWithCover(
        activityTitle: String,
        activityType: String,
        activityStartAt: Int,
        activityEndAt: Int,
        activityPlace: String,
        activityRegistrationType: String,
        activityOrganizer: String,
        creatorPhone: String,
        activityDetail: String,
        coverFile: File
    ) {
        PublishApiService
            .INSTANCE
            .postActivityWithCover(
                activityTitle.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                activityType.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                activityStartAt.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                activityEndAt.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                activityPlace.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                activityRegistrationType.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                activityOrganizer.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                creatorPhone.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                activityDetail.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                coverFile.toMultipartBodyPart("activity_cover_file")
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                _publishStatus.postValue(false)
            }
            .safeSubscribeBy {
                    _publishStatus.postValue(true)
            }


    }

    @SuppressLint("CheckResult")
    fun postActivityNotCover(
        activityTitle: String,
        activityType: String,
        activityStartAt: Int,
        activityEndAt: Int,
        activityPlace: String,
        activityRegistrationType: String,
        activityOrganizer: String,
        creatorPhone: String,
        activityDetail: String,
    ) {
        PublishApiService
            .INSTANCE
            .postActivityNotCover(
                activityTitle.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                activityType.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                activityStartAt.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                activityEndAt.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                activityPlace.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                activityRegistrationType.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                activityOrganizer.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                creatorPhone.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                activityDetail.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                _publishStatus.postValue(false)
            }
            .safeSubscribeBy {
                _publishStatus.postValue(true)
            }
    }
    private fun File.toMultipartBodyPart(name: String): MultipartBody.Part {
        val requestFile = RequestBody.create("image/png".toMediaTypeOrNull(), this)
        return MultipartBody.Part.createFormData(name, this.name, requestFile)
    }

}



