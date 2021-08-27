package com.mredrock.cyxbs.mine.page.feedback.edit.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.page.feedback.edit.presenter.FeedbackEditContract

/**
 * @Date : 2021/8/23   20:59
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackEditViewModel:BaseViewModel(),FeedbackEditContract.IVM {
    /**
     * 内容详情的字数
     */
    private val _editDesNum = MutableLiveData<Int>()
    val editDesNum : LiveData<Int>
        get() = _editDesNum
    override fun setEditDesNum(value:Int){
        _editDesNum.value = value
    }

    /**
     * 标题剩余能写的数字
     */
    private val _editTitleNum = MutableLiveData<Int>()
    val editTitleNum:LiveData<Int>
        get() = _editTitleNum
    override fun setEditTitleNum(value: Int){
        _editTitleNum.value = value
    }

    /**
     * Add
     * 图片的Uri地址
     */
    private val _uris: MutableLiveData<List<Uri>> = MutableLiveData(listOf())
    val uris: LiveData<List<Uri>>
    get() = _uris
    override fun setUris(value: List<Uri>) {
        _uris.value = value
    }

    private val _position = MutableLiveData<Int>()
    val position: LiveData<Int>
    get() = _position
    fun setPosition(position:Int){
        _position.value = position
    }


}