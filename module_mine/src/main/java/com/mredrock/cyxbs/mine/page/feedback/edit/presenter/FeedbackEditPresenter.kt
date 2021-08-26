package com.mredrock.cyxbs.mine.page.feedback.edit.presenter

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.CompoundButton
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.google.android.material.chip.Chip
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.base.presenter.BasePresenter
import com.mredrock.cyxbs.mine.page.feedback.edit.viewmodel.FeedbackEditViewModel
import org.w3c.dom.Text
import top.limuyang2.photolibrary.LPhotoHelper

/**
 * @Date : 2021/8/23   20:59
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackEditPresenter: BasePresenter<FeedbackEditViewModel>(), FeedbackEditContract.IPresenter {

    /**
     * 初始化数据
     */
    override fun fetch() {
        vm?.setEditDesNum(0)
        vm?.setEditTitleNum(12)
    }

    /**
     * 内容详情的监听器
     */
    inner class DesTextWatcher:TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            vm?.setEditDesNum(p1+p3)
        }
        override fun afterTextChanged(p0: Editable?) {}
    }

    /**
     * 标题的监听器
     */
    inner class TitleTextWatcher:TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            vm?.setEditTitleNum(12-p1-p3)
        }
        override fun afterTextChanged(p0: Editable?) {}
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        p0 as Chip
        if(p1){
            p0.apply {
                setChipStrokeColorResource(R.color.mine_edit_chip_border)
                setTextColor(Color.parseColor("#4F4AE9"))
                text
            }
        }else{
            p0.apply {
                setChipStrokeColorResource(R.color.mine_edit_chip_border_un)
                setTextColor(resources.getColor(R.color.mine_edit_chip_tv_un,context.theme))
            }
        }
    }

    /**
     * Add
     * 处理ActivityResult返回的图片
     */
    fun dealPic(data: Intent?) {
        //获取选择的图片
        val selectImageUris = ArrayList(LPhotoHelper.getSelectedPhotos(data))
        //把图片地址存入vm中
        if (selectImageUris.size != 0) {
            vm?.setUris(selectImageUris)
        }
    }

    /**
     * Add
     * 当移除图片的按钮被点击的时候移除对应的图片
     */
    fun removePic(i: Int) {
        //防止重复点击删除导致的数组越界
        try {
            val urls = vm?.uris?.value ?: return
            val list: MutableList<Uri> = mutableListOf<Uri>().apply { addAll(urls) }
            list.removeAt(i)
            vm?.setUris(list)
        } catch (e: Exception) {

        }
    }
}