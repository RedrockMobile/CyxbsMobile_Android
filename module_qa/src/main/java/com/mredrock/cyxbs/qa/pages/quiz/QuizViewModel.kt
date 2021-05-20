package com.mredrock.cyxbs.qa.pages.quiz

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.DIR_PHOTO
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.mine.network.model.DynamicDraft
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Topic
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.pages.dynamic.model.TopicDataSet
import com.mredrock.cyxbs.qa.utils.isNullOrEmpty
import com.mredrock.cyxbs.qa.utils.removeContinuousEnters
import io.reactivex.Observable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


/**
 * Created By jay68 on 2018/8/26.
 * rebuild By xgl
 */
class QuizViewModel : BaseViewModel() {
    val imageLiveData = MutableLiveData<ArrayList<String>>()

    //默认的图片的最大上限(5M)，超过之后需要压缩到5m
    private var fileMaxSize = 5242880

    val draft = MutableLiveData<DynamicDraft>()
    var isReleaseSuccess = false

    //为再次进入图库保存以前添加的图片，进行的逻辑
    val lastImageLiveData = ArrayList<String>()
    val backAndRefreshPreActivityEvent = SingleLiveEvent<Boolean>()
    val finishActivityEvent = MutableLiveData<Boolean>()
    val finishReleaseCommentEvent = MutableLiveData<Boolean>()
    var allCircle = MutableLiveData<List<Topic>>()
    var editingImgPos = -1
        private set
    private var type: String = ""
    private var content: String = ""
    private val isInvalidList = arrayListOf<Boolean>()
    fun tryEditImg(pos: Int): String? {
        editingImgPos = pos
        return imageLiveData.value?.get(pos)
    }

    fun setImageList(imageList: ArrayList<String>) {
        imageLiveData.value = imageList
    }

    fun getAllCirCleData() {
        val topicList = ArrayList<Topic>()
        val map: Map<String?, *>? = TopicDataSet.getAllTopic()
        if (!map.isNullOrEmpty()) {
            for ((key, value) in map) {
                val gson = Gson()
                if (!key.equals("outTime"))
                    topicList.add(gson.fromJson(value.toString(), Topic::class.java))
            }
            allCircle.value = topicList
        }
        getDraft()
    }

    fun submitDynamic() {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("content", content.removeContinuousEnters())
            .addFormDataPart("topic_id", type)
        if (!imageLiveData.value.isNullOrEmpty()) {
            Observable.fromArray(imageLiveData.value)
                .setSchedulers()
                .map {
                    it.map { path ->
                        LogUtils.d("Gibson", "file name = $path")
                        fileCaster(path)
                    }
                }
                .safeSubscribeBy {
                    it?.let { list ->
                        list.forEachIndexed { index, pair ->
                            val suffix = pair.first.name.substring(pair.first.name.lastIndexOf(".") + 1)
                            val imageBody =
                                RequestBody.create("image/$suffix".toMediaTypeOrNull(), pair.first)
                            val name = "photo" + (index + 1)
                            builder.addFormDataPart(name, pair.first.name, imageBody)
                        }
                        sendDynamicRequest(parts = builder.build().parts, filePairs = list)
                    }
                }
        } else {
            //不含图片的情况
            sendDynamicRequest(parts = builder.build().parts, filePairs = null)
        }
    }

    //暂时想不出太好的解耦合方案，这里设计的不太好，可能需要重构
    fun sendDynamicRequest(parts :List<MultipartBody.Part>, filePairs: List<Pair<File, Boolean>>?){
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .releaseDynamic(parts)
            .mapOrThrowApiException()
            .setSchedulers()
            .doFinally {
                filePairs?.forEach { pair ->
                    if (pair.second && pair.first.exists()) {
                        pair.first.delete()
                    }
                }
            }
            .doOnError { throwable ->
                isReleaseSuccess = false
                throwable?.let { e ->
                    BaseApp.context.toast(e.toString())
                }
                backAndRefreshPreActivityEvent.value = true
            }
            .safeSubscribeBy {
                isReleaseSuccess = true
                toastEvent.value = R.string.qa_release_dynamic_success
                backAndRefreshPreActivityEvent.value = true
            }
    }

    fun checkTitleAndContent(type: String, content: String): Boolean {
        var result = false
        if (type.isBlank() || type == "0") {
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

    fun getDraft() {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .getDraft()
            .mapOrThrowApiException()
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.qa_get_draft_failure
            }
            .safeSubscribeBy {
                LogUtils.d("Gibson", it.toString())
                //后端返回的json的images可能是[""]，会引起fileNotFind，这里要进行一次过滤
                //判空的原因是后端还有可能返回的是null，不加就会空指针
                val newList = it.images?.filter { path ->
                    path != ""
                }
                it.images = newList
                draft.value = it
                LogUtils.d("Gibson", "after cast = $it, imagesIsNullOrEmpty = ${draft.value?.images.isNullOrEmpty()}, imageList size = ${draft.value?.images?.size}")
            }
    }

    fun deleteDraft() {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .deleteDraft()
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.qa_delete_draft
            }
            .safeSubscribeBy {
                LogUtils.d("deletedraft", it.toString())
            }
    }

    fun updateDraftItem(flag: String, content: String, type: String) {
        LogUtils.d("Gibson", "flag = $flag, content = $content")
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("flag", flag)
            .addFormDataPart("content", content)
            .addFormDataPart("type", type)
        if (!imageLiveData.value.isNullOrEmpty()) {
            val name = "images"
            imageLiveData.value?.forEach { path ->
                builder.addFormDataPart(name, path)
            }
        }
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .updateDraft(builder.build().parts)
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.qa_save_draft_failure
                backAndRefreshPreActivityEvent.value = true

            }
            .safeSubscribeBy {
                toastEvent.value = R.string.qa_save_draft_success
                backAndRefreshPreActivityEvent.value = true

            }
    }

    fun checkInvalid(b: Boolean) {
        isInvalidList.add(b)
    }

    fun resetInvalid() {
        isInvalidList.clear()
    }

    fun getImageLimits() {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .getImageMaxSize()
            .setSchedulers()
            .safeSubscribeBy {
                fileMaxSize = it.data.image_limit * 1024 * 1024
            }
    }

    fun submitComment(postId: String, content: String, replyId: String) {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("content", content.removeContinuousEnters())
            .addFormDataPart("post_id", postId)
            .addFormDataPart("reply_id", replyId)
        var files: List<Pair<File, Boolean>>? = null
        if (!imageLiveData.value.isNullOrEmpty()) {
            files = imageLiveData.value!!.asSequence()
                .map { fileCaster(it) }
                .toList()
            files.forEachIndexed { index, pair ->
                val suffix = pair.first.name.substring(pair.first.name.lastIndexOf(".") + 1)
                val imageBody = RequestBody.create("image/$suffix".toMediaTypeOrNull(), pair.first)
                val name = "photo" + (index + 1)
                builder.addFormDataPart(name, pair.first.name, imageBody)
            }
        }
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .releaseComment(builder.build().parts)
            .mapOrThrowApiException()
            .setSchedulers()
            .doFinally {
                files?.forEach { pair ->
                    if (pair.second && pair.first.exists()) {
                        pair.first.delete()
                    }
                }
                progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT
            }
            .doOnError {
                toastEvent.value = R.string.qa_release_comment_failure
                finishReleaseCommentEvent.value = false
            }
            .safeSubscribeBy {
                toastEvent.value = R.string.qa_release_comment_success
                finishReleaseCommentEvent.value = true
            }
    }

    //图像压缩
    private fun fileCaster(imagePath: String): Pair<File, Boolean> {
        val file = File(imagePath)
        if (file.length() >= fileMaxSize) {
            //超过阈值，需要进行压缩
            val bitMap = BitmapFactory.decodeFile(imagePath)
            //如果是奇奇怪怪的格式，不能转换层bitmap，就直接吧文件传出去
            bitMap?.let { bm ->
                val quality = fileMaxSize * 100 / file.length()//计算压缩质量
                if (quality in 0..100) {//从数学上说，这个是恒成立的，但是为了求稳还是写一步判断
                    val outputStream = ByteArrayOutputStream()
                    val newPath = "${
                        Environment.getExternalStorageDirectory()
                            .toString() + DIR_PHOTO + imagePath.substring(
                            imagePath.lastIndexOf('/'),
                            imagePath.lastIndexOf('.')
                        )
                    }compassByCyxbs${imagePath.substring(imagePath.lastIndexOf('.'))}"
                    val newFile = File(newPath)//写一个新的文件名
                    if (!newFile.exists()) newFile.createNewFile()
                    val fos = FileOutputStream(newFile)
                    bm.compress(Bitmap.CompressFormat.JPEG, quality.toInt(), outputStream)
                    fos.write(outputStream.toByteArray())
                    fos.flush()
                    fos.close()
                    return Pair(newFile, true)
                }
            }
        }
        return Pair(file, false)
    }
}