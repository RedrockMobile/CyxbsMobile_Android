package com.mredrock.cyxbs.qa.pages.quiz

import android.app.ProgressDialog
import android.content.Context
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.utils.extensions.checkError
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.CommentReleaseResult
import com.mredrock.cyxbs.qa.beannew.Topic
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.model.TopicDataSet
import com.mredrock.cyxbs.qa.utils.isNullOrEmpty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


/**
 * Created By jay68 on 2018/8/26.
 * rebuild By xgl
 */
class QuizViewModel : BaseViewModel() {
    val imageLiveData = MutableLiveData<ArrayList<String>>()
    val backAndRefreshPreActivityEvent = SingleLiveEvent<Boolean>()
    val finishActivityEvent = MutableLiveData<Boolean>()
    var allCircle = MutableLiveData<List<Topic>>()
    var editingImgPos = -1
        private set
    private var type: String = ""
    private var title: String = ""
    private var content: String = ""
    private val isInvalidList = arrayListOf<Boolean>()
    val commentReleaseResult = MutableLiveData<CommentReleaseResult>()
    fun tryEditImg(pos: Int): String? {
        editingImgPos = pos
        return imageLiveData.value?.get(pos)
    }

    fun setImageList(imageList: ArrayList<String>) {
        imageLiveData.value = imageList
    }

    fun getAllCirCleData(topic_name: String, instruction: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getTopicGround(topic_name, instruction)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnError {
                    toastEvent.value = R.string.qa_get_circle_data_failure
                }
                .safeSubscribeBy {
                    allCircle.value = it
                }.lifeCycle()
    }

    fun submitDynamic() {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
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
        }
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .releaseDynamic(builder.build().parts)
                .mapOrThrowApiException()
                .setSchedulers()
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

    fun submitTitleAndContent(type: String, content: String): Boolean {
        var result = false
        if (type.isBlank()) {
            toastEvent.value = R.string.qa_quiz_hint_title_empty
        } else if (content.isBlank()) {
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

    fun releaseComment(postId: String, content: String, replyId: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .releaseComment(content, postId, replyId)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnSubscribe {
                }
                .doOnError {
                }
                .safeSubscribeBy {
                    commentReleaseResult.postValue(it)
                }
    }
}