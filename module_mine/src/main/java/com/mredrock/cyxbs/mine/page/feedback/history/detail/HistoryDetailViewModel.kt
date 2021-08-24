package com.mredrock.cyxbs.mine.page.feedback.history.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.page.feedback.history.detail.bean.Feedback
import com.mredrock.cyxbs.mine.page.feedback.history.detail.bean.Reply

/**
 *@author ZhiQiang Tu
 *@time 2021/8/24  9:12
 *@signature 我们不明前路，却已在路上
 */
class HistoryDetailViewModel : BaseViewModel() {
    //反馈的信息
    private val _feedback:MutableLiveData<Feedback> = MutableLiveData()
    val feedback:LiveData<Feedback> = _feedback
    fun setFeedback(value:Feedback){
        _feedback.value = value
    }

    //回复的信息
    private val _reply:MutableLiveData<Reply> = MutableLiveData()
    val reply:LiveData<Reply> = _reply
    fun setReply(value: Reply){
        _reply.value = value
    }

}