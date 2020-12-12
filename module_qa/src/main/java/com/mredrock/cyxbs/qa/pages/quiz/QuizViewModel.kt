package com.mredrock.cyxbs.qa.pages.quiz

import android.annotation.SuppressLint
import android.util.Base64
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.CommonApiService
import com.mredrock.cyxbs.common.utils.down.bean.DownMessageText
import com.mredrock.cyxbs.common.utils.down.params.DownMessageParams
import com.mredrock.cyxbs.common.utils.extensions.checkError
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.QuizResult
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.utils.isNullOrEmpty
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


/**
 * Created By jay68 on 2018/8/26.
 */
class QuizViewModel : BaseViewModel() {
    val imageLiveData = MutableLiveData<ArrayList<String>>()
    val backAndRefreshPreActivityEvent = SingleLiveEvent<Boolean>()
    val finishActivityEvent = MutableLiveData<Boolean>()

    var editingImgPos = -1
        private set
    var myRewardCount = 0
        private set
    var isAnonymous = false
    var rewardExplainList: List<DownMessageText> = listOf()
    private var type: String = ""
    private var title: String = ""
    private var content: String = ""
    private var disappearTime: String = ""
    private val isInvalidList = arrayListOf<Boolean>()

    fun tryEditImg(pos: Int): String? {
        editingImgPos = pos
        return imageLiveData.value?.get(pos)
    }

    fun setImageList(imageList: ArrayList<String>) {
        imageLiveData.value = imageList
    }

    fun submitDynamic(stuNum: String) {
        if (content.isBlank() && imageLiveData.value.isNullOrEmpty()) {
            toastEvent.value = R.string.qa_hint_content_empty
            return
        }
        progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("stdNum", "stdNum")
                .addFormDataPart("stdNum", stuNum)
                .addFormDataPart("content", content)
                .addFormDataPart("topic_id", type)
        if (!imageLiveData.value.isNullOrEmpty()) {
            val files = imageLiveData.value!!.asSequence()
                    .map { File(it) }
                    .toList()
            files.forEachIndexed { index, file ->
                val suffix = file.name.substring(file.name.lastIndexOf(".") + 1)
                val imageBody = RequestBody.create("image/$suffix".toMediaTypeOrNull(), file)
                val name = "photo" + (index + 1)
                builder.addFormDataPart(name, file.name, imageBody)
            }
            ApiGenerator.getApiService(ApiServiceNew::class.java)
                    .uploadQuestionPic(builder.build().parts)
                    .setSchedulers()
                    .checkError()
                    .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                    .doOnError {
                        toastEvent.value = R.string.qa_upload_pic_failed
                        backAndRefreshPreActivityEvent.value = true
                    }
                    .safeSubscribeBy {
                        toastEvent.value = R.string.qa_answer_submit_successfully_text
                        backAndRefreshPreActivityEvent.value = true
                    }
        }
    }

    fun submitTitleAndContent(type: String, content: String): Boolean {
        var result = false
        if (type.isNullOrBlank()) {
            toastEvent.value = R.string.qa_quiz_hint_title_empty
        } else if (content.isNullOrBlank() && imageLiveData.value.isNullOrEmpty()) {
            toastEvent.value = R.string.qa_hint_content_empty
        } else {
            this.content = content
            this.type = type
            result = true
        }
        return result
    }

    fun addItemToDraft(content: String?, type: String?) {
        if (type.isNullOrBlank() && content.isNullOrBlank()) {
            return
        }
        val s = "{\"title\":\"$title\",\"description\":\"$content\",\"kind\":\"$type\"${getImgListStrings()}}"
        val json = Base64.encodeToString(s.toByteArray(), Base64.DEFAULT)
        ApiGenerator.getApiService(ApiService::class.java)
                .addItemToDraft("question", json, "")
                .setSchedulers()
                .checkError()
                .doOnError {
                    toastEvent.value = R.string.qa_quiz_save_failed
                    finishActivityEvent.value = true
                }
                .safeSubscribeBy {
                    toastEvent.value = R.string.qa_quiz_save_success
                    finishActivityEvent.value = true
                }
                .lifeCycle()
    }

    fun updateDraftItem(content: String?, id: String, type: String?) {
        if (type.isNullOrEmpty() && content.isNullOrEmpty()) {
            deleteDraft(id)
        }
        val s = "{\"title\":\"$title\",\"description\":\"$content\",\"kind\":\"$type\"${getImgListStrings()}}"
        val json = Base64.encodeToString(s.toByteArray(), Base64.DEFAULT)
        ApiGenerator.getApiService(ApiService::class.java)
                .updateDraft(json, id)
                .setSchedulers()
                .checkError()
                .doOnError {
                    toastEvent.value = R.string.qa_quiz_update_failed
                    finishActivityEvent.value = true
                }
                .safeSubscribeBy {
                    toastEvent.value = R.string.qa_quiz_update_success
                    finishActivityEvent.value = true
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
}