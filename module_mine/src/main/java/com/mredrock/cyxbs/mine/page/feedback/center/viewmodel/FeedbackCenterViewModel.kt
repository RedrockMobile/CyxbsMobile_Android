package com.mredrock.cyxbs.mine.page.feedback.center.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.page.feedback.center.presenter.FeedbackCenterContract
import com.mredrock.cyxbs.mine.page.feedback.network.bean.NormalFeedback

/**
 * @Date : 2021/8/23   20:57
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackCenterViewModel:BaseViewModel(), FeedbackCenterContract.IVM{
    /**
     * 标题列表
     */
    private val _contentList = MutableLiveData<List<NormalFeedback.Data>>()
    val contentList : LiveData<List<NormalFeedback.Data>>
        get() = _contentList
    override fun setContentList(value:List<NormalFeedback.Data>){
        _contentList.value = value
    }
}