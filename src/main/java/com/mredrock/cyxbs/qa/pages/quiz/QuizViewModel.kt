package com.mredrock.cyxbs.qa.pages.quiz

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import com.mredrock.cyxbs.qa.bean.QuizResult
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.pages.quiz.ui.dialog.TimePickDialog
import com.mredrock.cyxbs.qa.utils.isNullOrEmpty
import com.mredrock.cyxbs.qa.utils.toDate
import com.mredrock.cyxbs.qa.utils.toFormatString
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.longToast
import java.io.File


/**
 * Created By jay68 on 2018/8/26.
 */
class QuizViewModel(var type: String) : BaseViewModel() {
    val imageLiveData = MutableLiveData<ArrayList<String>>()
    val tagLiveData = MutableLiveData<String>()
    val backAndRefreshPreActivityEvent = SingleLiveEvent<Boolean>()

    var editingImgPos = -1
        private set
    var myRewardCount = 0
        private set
    var isAnonymous = false

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

    fun setTag(tag: String) {
        if (tag.isBlank()) {
            return
        }
        tagLiveData.value = tag
    }

    fun setDisAppearTime(rawTime: String): Boolean {
        val date = rawTime.toDate("yyyy年MM月dd日 HH时mm分")
        //计算与当前时间差，不允许低于TimePickDialog.MIN_GAP_HOUR定义的值（可以有5分钟的误差）
        if (date.time - System.currentTimeMillis() < TimePickDialog.MIN_GAP_HOUR * 3600000 - 3000000) {
            longToastEvent.value = R.string.qa_quiz_error_time_too_short
            return false
        }
        disappearTime = date.toFormatString("yyyy-MM-dd HH:mm:ss")
        return true
    }

    fun getMyReward() {
        val user = BaseApp.user ?: return
        ApiGenerator.getApiService(ApiService::class.java)
                .getScoreStatus(user.stuNum ?: "", user.idNum ?: "")
                .setSchedulers()
                .mapOrThrowApiException()
                .safeSubscribeBy { myRewardCount = it.integral }
    }

    @SuppressLint("CheckResult")
    fun quiz(reward: Int): Boolean {
        if (reward > myRewardCount) {
            longToastEvent.value = R.string.qa_quiz_error_reward_not_enough
            return false
        }
        progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT
        val user = BaseApp.user ?: return true
        val stuNum = user.stuNum ?: ""
        val idNum = user.idNum ?: ""
        val tags = tagLiveData.value ?: ""
        val isAnonymousInt = 1.takeIf { isAnonymous } ?: 0

        var observable: Observable<out Any> = ApiGenerator.getApiService(ApiService::class.java)
                .quiz(stuNum, idNum, title, content, isAnonymousInt, type, tags, reward, disappearTime)
                .setSchedulers()
                .mapOrThrowApiException()
        if (!imageLiveData.value.isNullOrEmpty()) {
            val files = imageLiveData.value!!.asSequence()
                    .map { File(it) }
                    .toList()
            observable = observable.flatMap {
                val id = (it as QuizResult).id
                uploadPic(stuNum, idNum, id, files)
            }
        }
        observable.doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnError { BaseApp.context.longToast(it.message!!) }
                .safeSubscribeBy { backAndRefreshPreActivityEvent.value = true }

        return true
    }

    private fun uploadPic(stuNum: String, idNum: String, qid: String, files: List<File>): Observable<RedrockApiStatus> {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("stuNum", stuNum)
                .addFormDataPart("idNum", idNum)
                .addFormDataPart("question_id", qid)
        files.forEachIndexed { index, file ->
            val suffix = file.name.substring(file.name.lastIndexOf(".") + 1)
            val imageBody = RequestBody.create(MediaType.parse("image/$suffix"), file)
            val name = "photo" + (index + 1)
            builder.addFormDataPart(name, file.name, imageBody)
        }
        return ApiGenerator.getApiService(ApiService::class.java)
                .uploadQuestionPic(builder.build().parts())
                .setSchedulers()
                .checkError()
    }

    fun submitTitleAndContent(title: String?, content: String?): Boolean {
        var result = false

        if (title.isNullOrBlank()) {
            toastEvent.value = R.string.qa_quiz_hint_title_empty
        } else if (content.isNullOrBlank() && imageLiveData.value.isNullOrEmpty()) {
            toastEvent.value = R.string.qa_hint_content_empty
        } else if (tagLiveData.value.isNullOrBlank()) {
            toastEvent.value = R.string.qa_hint_tag_empty
        } else {
            this.title = title ?: ""
            this.content = content ?: ""
            result = true
        }
        return result
    }

    fun addItemToDraft(title: String?, content: String?) {
        if (title.isNullOrBlank() && content.isNullOrBlank()) {
            return
        }
        val user = BaseApp.user ?: return
        val t = tagLiveData.value ?: ""
        val s = "{\"title\":\"$title\",\"description\":\"$content\",\"kind\":\"$type\",\"tags\":\"$t\"${getImgListStrings()}}"
        val json = Base64.encodeToString(s.toByteArray(), Base64.DEFAULT)
        ApiGenerator.getApiService(ApiService::class.java)
                .addItemToDraft(user.stuNum ?: "", user.idNum ?: "", "question", json, "")
                .setSchedulers()
                .checkError()
                .safeSubscribeBy(
                        onError = {
                            toastEvent.value = R.string.qa_quiz_save_failed
                        })
                .lifeCycle()
    }

    fun updateDraftItem(title: String?, content: String?, id: String) {
        val user = BaseApp.user ?: return
        val t = tagLiveData.value ?: ""
        val s = "{\"title\":\"$title\",\"description\":\"$content\",\"kind\":\"$type\",\"tags\":\"$t\"${getImgListStrings()}}"
        val json = Base64.encodeToString(s.toByteArray(), Base64.DEFAULT)
        ApiGenerator.getApiService(ApiService::class.java)
                .updateDraft(user.stuNum ?: "", user.idNum ?: "", json, id)
                .setSchedulers()
                .checkError()
                .safeSubscribeBy(
                        onError = {
                            toastEvent.value = R.string.qa_quiz_save_failed
                        })
                .lifeCycle()
    }

    fun deleteDraft(id: String) {
        val user = BaseApp.user ?: return
        ApiGenerator.getApiService(ApiService::class.java)
                .deleteDraft(user.stuNum ?: "", user.idNum ?: "", id)
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
        val s = res.toString()
        return if (s.isNotEmpty()) ",\"photo_thumbnail_src\":\"${s.substring(1, s.length - 1)}\""
            else ""
    }

    fun checkInvalid(b: Boolean) {
        isInvalidList.add(b)
    }

    fun resetInvalid() {
        isInvalidList.clear()
    }

    class Factory(private val type: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
                return QuizViewModel(type) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}