package com.mredrock.cyxbs.qa.pages.quiz

import android.annotation.SuppressLint
import android.util.Base64
import androidx.lifecycle.MutableLiveData
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
import com.mredrock.cyxbs.qa.pages.quiz.ui.dialog.RewardSetDialog
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
class QuizViewModel : BaseViewModel() {
    val imageLiveData = MutableLiveData<ArrayList<String>>()
    val backAndRefreshPreActivityEvent = SingleLiveEvent<Boolean>()
    val backAndFinishActivityEvent = SingleLiveEvent<Boolean>()

    var editingImgPos = -1
        private set
    var myRewardCount = 0
        private set
    var isAnonymous = false
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


    fun setDisAppearTime(rawTime: String): Boolean {
        val date = rawTime.toDate("yyyy-MM-dd HH时mm分")
        //计算与当前时间差，不允许低于TimePickDialog.MIN_GAP_HOUR定义的值，允许5分钟误差/
        if (date.time - System.currentTimeMillis() < RewardSetDialog.MIN_GAP_HOUR * 3600000 - 300000) {
            longToastEvent.value = R.string.qa_quiz_error_time_too_short
            return false
        }
        disappearTime = date.toFormatString("yyyy-MM-dd HH:mm:ss")
        return true
    }

    fun getMyReward() {
        ApiGenerator.getApiService(ApiService::class.java)
                .getScoreStatus()
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
        val isAnonymousInt = 1.takeIf { isAnonymous } ?: 0
        var observable: Observable<out Any> = ApiGenerator.getApiService(ApiService::class.java)
                .quiz(title, content, isAnonymousInt, type, "", reward, disappearTime)
                .setSchedulers()
                .mapOrThrowApiException()
        if (!imageLiveData.value.isNullOrEmpty()) {
            val files = imageLiveData.value!!.asSequence()
                    .map { File(it) }
                    .toList()
            observable = observable.flatMap {
                val id = (it as QuizResult).id
                uploadPic(id, files)
            }
        }
        observable.doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnError { BaseApp.context.longToast(it.message!!) }
                .safeSubscribeBy { backAndRefreshPreActivityEvent.value = true }

        return true
    }

    private fun uploadPic(qid: String, files: List<File>): Observable<RedrockApiStatus> {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("question_id", qid)
        files.forEachIndexed { index, file ->
            val suffix = file.name.substring(file.name.lastIndexOf(".") + 1)
            val imageBody = RequestBody.create(MediaType.parse("image/$suffix"), file)
            val name = "photo" + (index + 1)
            builder.addFormDataPart(name, file.name, imageBody)
        }
        return ApiGenerator.getCommonApiService(ApiService::class.java)
                .uploadQuestionPic(builder.build().parts())
                .setSchedulers()
                .checkError()
    }

    fun submitTitleAndContent(title: String?, content: String?, type: String?): Boolean {
        var result = false

        if (title.isNullOrBlank()) {
            toastEvent.value = R.string.qa_quiz_hint_title_empty
        } else if (content.isNullOrBlank() && imageLiveData.value.isNullOrEmpty()) {
            toastEvent.value = R.string.qa_hint_content_empty
        } else {
            this.title = title
            this.content = content ?: ""
            this.type = type ?: "学习"
            result = true
        }
        return result
    }

    fun addItemToDraft(title: String?, content: String?, type: String?) {
        if (title.isNullOrBlank() && content.isNullOrBlank()) {
            return
        }
        val s = "{\"title\":\"$title\",\"description\":\"$content\",\"kind\" :\"$type\",${getImgListStrings()}}"
        val json = Base64.encodeToString(s.toByteArray(), Base64.DEFAULT)
        ApiGenerator.getApiService(ApiService::class.java)
                .addItemToDraft("question", json, "")
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

    fun updateDraftItem(title: String?, content: String?, id: String, type: String?) {
        val s = "{\"title\":\"$title\",\"description\":\"$content\",\"kind\":\"$type\",${getImgListStrings()}}"
        val json = Base64.encodeToString(s.toByteArray(), Base64.DEFAULT)
        ApiGenerator.getApiService(ApiService::class.java)
                .updateDraft(json, id)
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
}