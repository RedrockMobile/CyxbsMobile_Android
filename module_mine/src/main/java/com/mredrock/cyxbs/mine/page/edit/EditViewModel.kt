package com.mredrock.cyxbs.mine.page.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.CommonApiService
import com.mredrock.cyxbs.common.utils.down.bean.DownMessageText
import com.mredrock.cyxbs.common.utils.down.params.DownMessageParams
import com.mredrock.cyxbs.common.utils.extensions.errorHandler
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.mine.Bean.PersonData
import com.mredrock.cyxbs.mine.network.ApiService
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.widget.ExecuteOnceObserver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Created by zia on 2018/8/26.
 */
class EditViewModel : com.mredrock.cyxbs.lib.base.ui.BaseViewModel() {

    val upLoadImageEvent = MutableLiveData<Boolean>()

    private val _personData = MutableLiveData<PersonData>()
    val personData: LiveData<PersonData>
        get() = _personData

    fun uploadAvatar(
        requestBody: RequestBody,
        file: MultipartBody.Part
    ) {
        ApiService::class.api
            .uploadSocialImg(requestBody, file)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { upLoadImageEvent.postValue(false) }
            .safeSubscribeBy {
                upLoadImageEvent.postValue(true)
                apiService.updateUserImage(it.data.thumbnail_src, it.data.photosrc)
            }
    }

    fun getUserData() {
        ApiService::class.api
            .getPersonData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                toast("抱歉，网不好，没有获取到信息")
            }.safeSubscribeBy {
                _personData.postValue(it)
            }
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