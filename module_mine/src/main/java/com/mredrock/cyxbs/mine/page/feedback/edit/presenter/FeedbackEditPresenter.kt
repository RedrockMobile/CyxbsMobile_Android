package com.mredrock.cyxbs.mine.page.feedback.edit.presenter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.annotation.RequiresApi
import com.google.android.material.chip.Chip
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.getRequestBody
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.base.presenter.BasePresenter
import com.mredrock.cyxbs.mine.page.feedback.adapter.rv.RvBinder
import com.mredrock.cyxbs.mine.page.feedback.edit.viewmodel.FeedbackEditViewModel
import com.mredrock.cyxbs.mine.page.feedback.history.list.adapter.PicBannerBinderAdd
import com.mredrock.cyxbs.mine.page.feedback.history.list.adapter.PicBannerBinderPic
import com.mredrock.cyxbs.mine.page.feedback.history.list.bean.Pic
import com.mredrock.cyxbs.mine.page.feedback.network.bean.apiServiceNew
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import top.limuyang2.photolibrary.LPhotoHelper
import java.io.File

/**
 * @Date : 2021/8/23   20:59
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackEditPresenter(val activity:Activity) : BasePresenter<FeedbackEditViewModel>(),
    FeedbackEditContract.IPresenter {

    /**
     * 初始化数据
     */
    override fun fetch() {

    }

    /**
     * Post数据
     */
    fun postFeedbackInfo(productId:String, type:String, title:String, content:String, file: List<File>){
        val productIdRB = productId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val typeRB = type.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val titleRB = title.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val contentRB = content.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val map = HashMap<String, RequestBody>()
        map.apply {
            put("product_id", productIdRB)
            put("type", typeRB)
            put("title", titleRB)
            put("content", contentRB)
        }
        val fileBody = if (file.isNotEmpty()){
            (file.indices).map {
                MultipartBody.Part.createFormData("file", file[it].name, file[it].getRequestBody())
            }
        }else{
            null
        }
        apiServiceNew.postFeedbackInfo(map,fileBody)
            .setSchedulers()
            .doOnSubscribe {}
            .safeSubscribeBy(
                onNext = {
                    BaseApp.context.toast("提交成功  我们会在十四个工作日内回复")
                },
                onError = {
                    BaseApp.context.toast("网络异常")
                    vm?.sendFinishEvent()
                },
                onComplete = {
                    vm?.sendFinishEvent()
                })
    }

    /**
     * 对chip是否选中的处理
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        p0 as Chip
        if (p1) {
            p0.apply {
                setChipStrokeColorResource(R.color.mine_edit_chip_border)
                setTextColor(Color.parseColor("#4F4AE9"))
            }
        } else {
            p0.apply {
                setChipStrokeColorResource(R.color.mine_edit_chip_border_un)
                setTextColor(resources.getColor(R.color.mine_edit_chip_tv_un, context.theme))
            }
        }
    }

    /**
     * 处理ActivityResult返回的图片
     */
    override fun dealPic(data: Intent?) {
        //获取选择的图片
        val selectImageUris = ArrayList(LPhotoHelper.getSelectedPhotos(data))
        //把图片地址存入vm中
        if (selectImageUris.size != 0) {
            vm?.setUris(selectImageUris)
        }
    }

    /**
     * 当移除图片的按钮被点击的时候移除对应的图片
     */
    override fun removePic(i: Int) {
        //防止重复点击删除导致的数组越界
        vm?.setPosition(i)
        try {
            val urls = vm?.uris?.value ?: return
            val list: MutableList<Uri> = mutableListOf<Uri>().apply { addAll(urls) }
            list.removeAt(i)
            vm?.setUris(list)
        } catch (e: Exception) {

        }
    }

    /**
     * 初始化rv中的binder
     */
    override fun getBinderList(
        uri: List<Uri>,
        contentListener: (View, Int) -> Unit,
        iconListener: (View, Int) -> Unit,
        clickListener: (View, Int) -> Unit
    ): MutableList<RvBinder<*>> {
        return mutableListOf<RvBinder<*>>().apply {
            addAll(
                uri.map {
                    Pic(it)
                }.map {
                    PicBannerBinderPic(it).apply {
                        setOnContentClickListener { view, i -> contentListener(view, i) }
                        setOnIconClickListener { view, i -> iconListener(view, i) }
                        itemId = it.picUri.toString()
                    }
                }
            )
            //添加Add的图标
            if (size < 3) {
                add(
                    PicBannerBinderAdd().apply {
                        setClickListener { view, i -> clickListener(view, i) }
                    }
                )
            }
        }
    }

    /**
     * 内容详情的监听器
     */
    inner class DesTextWatcher : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            vm?.setEditDesNum(p1 + p3)
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    /**
     * 标题的监听器
     */
    inner class TitleTextWatcher : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            vm?.setEditTitleNum(12 - p1 - p3)
        }

        override fun afterTextChanged(p0: Editable?) {}
    }
}