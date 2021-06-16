package com.mredrock.cyxbs.qa.pages.mine.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.beannew.Comment
import com.mredrock.cyxbs.qa.beannew.CommentWrapper
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.pages.mine.model.MyCommentDataSource
import com.mredrock.cyxbs.qa.utils.removeContinuousEnters

/**
 * @date 2021-03-05
 * @author Sca RayleighZ
 */
class MyCommentViewModel : BaseViewModel() {

    private val factory: MyCommentDataSource.Factory
    val cwList: LiveData<PagedList<CommentWrapper>>
    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>

    init {
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(3)
                .setPageSize(6)
                .setInitialLoadSizeHint(6)
                .build()
        factory = MyCommentDataSource.Factory()
        cwList = LivePagedListBuilder<Int, CommentWrapper>(factory, config).build()

        networkState = Transformations.switchMap(factory.cwListDataSource) { it.networkState }
        initialLoad = Transformations.switchMap(factory.cwListDataSource) { it.initialLoad }
    }

    fun invalidateCWList() = cwList.value?.dataSource?.invalidate()

    fun retry() = factory.cwListDataSource.value?.retry()

    fun praise(id: String, onSuccess: () -> Unit) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .praise(id, "2")//model确定为对评论进行点赞
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { true }
                .safeSubscribeBy(
                        onError = {
                            BaseApp.context.toast("点赞失败")
                        },
                        onNext = {
                            onSuccess.invoke()
                        }
                )
    }

    fun reply(comment: Comment, content: String, onSuccess: () -> Unit){
        var rContent = content
        val commentId = if (comment.replyId == "") comment.commentId else comment.replyId
        if (comment.fromNickname != ""){
            //TODO: 实现手段有待商榷
            rContent = "回复 @${comment.fromNickname}: "+content
        }
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .releaseComment(
                        rContent.removeContinuousEnters(),
                        comment.postId,
                        commentId
                )
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { true }
                .safeSubscribeBy(
                        onError = {
                            BaseApp.context.toast("评论失败o(*￣▽￣*)o")
                        },
                        onNext = {
                            onSuccess.invoke()
                        }
                )
    }
}