package com.mredrock.cyxbs.ufield.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.ufield.network.PublishApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
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
        requestMap: MutableMap<String, RequestBody>,
        coverFile: File
    ) {
        PublishApiService
            .INSTANCE
            .postActivityWithCover(
                requestMap,
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
        requestMap: MutableMap<String, RequestBody>
    ) {
        PublishApiService
            .INSTANCE
            .postActivityNotCover(
                requestMap
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
        val requestFile = asRequestBody("image/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(name, this.name, requestFile)
    }

}



