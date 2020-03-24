package com.mredrock.cyxbs.qa.pages.answer.viewmodel

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.utils.isNullOrEmpty
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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
    val backAndFinishActivityEvent = SingleLiveEvent<Boolean>()
    val questionData = MutableLiveData<Question>()
    var editingImgPos = -1
        private set

    fun tryEditImg(pos: Int): String? {
        editingImgPos = pos
        return imageLiveData.value?.get(pos)
    }

    private val isInvalidList = arrayListOf<Boolean>()

    fun submitAnswer(content: String) {
        if (content.isBlank() && imageLiveData.value.isNullOrEmpty()) {
            toastEvent.value = R.string.qa_hint_content_empty
            return
        }
        progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
        var observable: Observable<out Any> = ApiGenerator.getApiService(ApiService::class.java)
                .answer(qid, content)
                .setSchedulers()
                .mapOrThrowApiException()
        if (!imageLiveData.value.isNullOrEmpty()) {
            val files = imageLiveData.value!!.asSequence()
                    .map { File(it) }
                    .toList()
            observable = observable.flatMap { uploadPic(it as String, files) }
        }
        observable.doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnError {
                    BaseApp.context.longToast(it.message!!)
                    backAndRefreshPreActivityEvent.value = true
                }
                .safeSubscribeBy {
                    toastEvent.value = R.string.qa_answer_submit_successfully_text
                    backAndRefreshPreActivityEvent.value = true
                }
    }

    private fun uploadPic(qid: String, files: List<File>): Observable<RedrockApiStatus> {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("answer_id", qid)
        files.forEachIndexed { index, file ->
            val suffix = file.name.substring(file.name.lastIndexOf(".") + 1)
            val imageBody = RequestBody.create("image/$suffix".toMediaTypeOrNull(), file)
            val name = "photo" + (index + 1)
            builder.addFormDataPart(name, file.name, imageBody)
        }
        return ApiGenerator.getApiService(ApiService::class.java)
                .uploadAnswerPic(builder.build().parts)
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
        val s = "{\"title\":\"$content\"${getImgListStrings()}}"
        val json = Base64.encodeToString(s.toByteArray(), Base64.DEFAULT)
        ApiGenerator.getApiService(ApiService::class.java)
                .addItemToDraft("answer", json, qid)
                .setSchedulers()
                .checkError()
                .doOnError {
                    toastEvent.value = R.string.qa_quiz_save_failed
                    backAndFinishActivityEvent.value = true
                }
                .safeSubscribeBy {
                    toastEvent.value = R.string.qa_quiz_save_success
                    backAndFinishActivityEvent.value = true
                }
                .lifeCycle()
    }

    fun updateDraft(content: String?, id: String) {
        if (content.isNullOrBlank()) {
            deleteDraft(id)
            return
        }
        val s = "{\"title\":\"$content\"${getImgListStrings()}}"
        val json = Base64.encodeToString(s.toByteArray(), Base64.DEFAULT)
        ApiGenerator.getApiService(ApiService::class.java)
                .updateDraft(json, id)
                .setSchedulers()
                .checkError()
                .doOnError {
                    toastEvent.value = R.string.qa_quiz_update_failed
                    backAndFinishActivityEvent.value = true
                }
                .safeSubscribeBy {
                    toastEvent.value = R.string.qa_quiz_update_success
                    backAndFinishActivityEvent.value = true
                }
                .lifeCycle()
    }

    fun deleteDraft(id: String) {
        ApiGenerator.getApiService(ApiService::class.java)
                .deleteDraft(id)
                .setSchedulers()
                .checkError()
                .safeSubscribeBy()
                .lifeCycle()
    }

    //通过EventBus拿到id后请求详情
    fun getQuestionInfo() {
        ApiGenerator.getApiService(ApiService::class.java)
                .getQuestion(qid)
                .setSchedulers()
                .doOnError {
                    toastEvent.value = R.string.qa_answer_load_draft_question_failed
                }
                .safeSubscribeBy {
                    questionData.value = it.data
                }
    }

    private fun getImgListStrings(): String {
        val list = imageLiveData.value ?: return ""
        val res = arrayListOf<String>()
        list.forEachIndexed { index, s ->
            if (!isInvalidList[index] || s.isNotEmpty()) res.add(s)
        }
        val s = StringBuilder()
        res.forEach { s.append("\"$it\",") }
        return if (s.isNotEmpty()) ",\"photo_url\":[${s.substring(0, s.length - 1)}]"
        else ""
    }

    fun checkInvalid(b: Boolean) {
        isInvalidList.add(b)
    }

    fun resetInvalid() {
        isInvalidList.clear()
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