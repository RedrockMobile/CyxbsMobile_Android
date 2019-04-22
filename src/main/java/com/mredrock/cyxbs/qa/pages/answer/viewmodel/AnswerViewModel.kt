package com.mredrock.cyxbs.qa.pages.answer.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.util.Base64
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.checkError
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.utils.isNullOrEmpty
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.longToast
import java.io.File

/**
 * Created By jay68 on 2018/12/3.
 */
class AnswerViewModel(var qid: String) : BaseViewModel() {
    val imageLiveData = MutableLiveData<ArrayList<String>>()
    val backAndRefreshPreActivityEvent = SingleLiveEvent<Boolean>()

    var editingImgPos = -1
        private set

    fun tryEditImg(pos: Int): String? {
        editingImgPos = pos
        return imageLiveData.value?.get(pos)
    }

    fun submitAnswer(content: String) {
        if (content.isBlank() && imageLiveData.value.isNullOrEmpty()) {
            toastEvent.value = R.string.qa_hint_content_empty
            return
        }
        progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
        val user = BaseApp.user ?: return
        val stuNum = user.stuNum ?: ""
        val idNum = user.idNum ?: ""
        var observable: Observable<out Any> = ApiGenerator.getApiService(ApiService::class.java)
                .answer(stuNum, idNum, qid, content)
                .setSchedulers()
                .mapOrThrowApiException()
        if (!imageLiveData.value.isNullOrEmpty()) {
            val files = imageLiveData.value!!.asSequence()
                    .map { File(it) }
                    .toList()
            observable = observable.flatMap { uploadPic(stuNum, idNum, it as String, files) }
        }
        observable.doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnError { BaseApp.context.longToast(it.message!!) }
                .safeSubscribeBy { backAndRefreshPreActivityEvent.value = true }
    }

    private fun uploadPic(stuNum: String, idNum: String, qid: String, files: List<File>): Observable<RedrockApiStatus> {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("stuNum", stuNum)
                .addFormDataPart("idNum", idNum)
                .addFormDataPart("answer_id", qid)
        files.forEachIndexed { index, file ->
            val suffix = file.name.substring(file.name.lastIndexOf(".") + 1)
            val imageBody = RequestBody.create(MediaType.parse("image/$suffix"), file)
            val name = "photo" + (index + 1)
            builder.addFormDataPart(name, file.name, imageBody)
        }
        return ApiGenerator.getApiService(ApiService::class.java)
                .uploadAnswerPic(builder.build().parts())
                .setSchedulers()
                .checkError()
    }

    fun setImageList(imageList: ArrayList<String>) {
        imageLiveData.value = imageList
    }

    fun addItemToDraft(content: String?) {
        if (content.isNullOrBlank()) {
            return
        }
        val user = BaseApp.user ?: return
        val s = "{\"title\":\"$content\"}"
        val json = Base64.encodeToString(s.toByteArray(), Base64.DEFAULT)
        ApiGenerator.getApiService(ApiService::class.java)
                .addItemToDraft(user.stuNum ?: "", user.idNum ?: "", "answer", json, qid)
                .setSchedulers()
                .checkError()
                .safeSubscribeBy(
                        onError = {
                            toastEvent.value = R.string.qa_quiz_save_failed
                        },
                        onNext = {
                            toastEvent.value = R.string.qa_quiz_save_success
                        }
                )
                .lifeCycle()
    }

    fun updateDraft(content: String?, id: String) {
        if (content.isNullOrBlank()) {
            deleteDraft(id)
            return
        }
        val user = BaseApp.user ?: return
        val s = "{\"title\":\"$content\"}"
        val json = Base64.encodeToString(s.toByteArray(), Base64.DEFAULT)
        ApiGenerator.getApiService(ApiService::class.java)
                .updateDraft(user.stuNum ?: "", user.idNum ?: "", json, id)
                .setSchedulers()
                .checkError()
                .safeSubscribeBy(
                        onError = {
                            toastEvent.value = R.string.qa_quiz_save_failed
                        },
                        onNext = {
                            toastEvent.value = R.string.qa_quiz_save_success
                        }
                )
                .lifeCycle()
    }

    fun deleteDraft(id: String) {
        val user = BaseApp.user ?: return
        ApiGenerator.getApiService(ApiService::class.java)
                .deleteDraft(user.stuNum ?: "", user.idNum ?: "", id)
                .setSchedulers()
                .checkError()
                .safeSubscribeBy(
                        onError = {
                            toastEvent.value = R.string.qa_quiz_save_failed
                        },
                        onNext = {}
                )
                .lifeCycle()
    }

    class Factory(private val qid: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(AnswerViewModel::class.java)) {
                return AnswerViewModel(qid) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}