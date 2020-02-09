package com.mredrock.cyxbs.mine.page.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.account.IUserService
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.mine.network.model.Comment
import com.mredrock.cyxbs.mine.network.model.RelateMeItem
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.mapOrThrowApiExceptionWithDataCanBeNull
import com.mredrock.cyxbs.mine.util.ui.RvFooter
import com.mredrock.cyxbs.mine.util.user

/**
 * Created by zia on 2018/9/13.
 */
class CommentViewModel : BaseViewModel() {

    val stuNum = ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum()
    val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")

    private val pageSize = 6
    private var commentPage: Int = 1
    private var responsePage: Int = 1

    //发出评论部分
    private val _eventOnComment = SingleLiveEvent<RvFooter.State>()
    val eventOnComment: LiveData<RvFooter.State>
        get() = _eventOnComment

    private val _commentList = MutableLiveData<MutableList<Comment>>()
    val commentList: LiveData<List<Comment>>
        get() = Transformations.map(_commentList) {
            it.toList()
        }

    fun loadCommentList() {
        apiService.getCommentList(stuNum, idNum
                ?: return, commentPage++, pageSize)
                .mapOrThrowApiExceptionWithDataCanBeNull()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy(
                        onNext = {
                            //由于Rxjava反射不应定能够够保证为空，当为空的说明这一页没有数据，于是停止加载
                            if (it == null) {
                                _eventOnComment.postValue(RvFooter.State.NOMORE)
                                return@safeSubscribeBy
                            }

                            val localComment = _commentList.value ?: mutableListOf()
                            localComment.addAll(it)
                            _commentList.postValue(localComment)
                        },
                        onError = {
                            _eventOnComment.postValue(RvFooter.State.ERROR)
                        })
                .lifeCycle()
    }
    fun cleanCommentPage() {
        commentPage = 1
        _commentList.value = mutableListOf()
    }


    val errorEvent = MutableLiveData<String>()
    val dataEvent = MutableLiveData<List<RelateMeItem>>()

    var page = 1

    var fakeComments: MutableLiveData<List<Comment>> = MutableLiveData()

    fun loadData(type: Int) {
        apiService.getRelateMeList(stuNum, idNum
                ?: return, page++, pageSize, type)
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy(
                        onNext = {
                            dataEvent.postValue(it)
                        },
                        onError = {
                            errorEvent.postValue(it.message)
                        }
                ).lifeCycle()
    }


    fun cleanPage() {
        page = 1
    }

}