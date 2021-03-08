package com.mredrock.cyxbs.qa.pages.mine.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.beannew.CommentWrapper
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.pages.mine.model.MyCommentDataSource

/**
 * @date 2021-03-05
 * @author Sca RayleighZ
 */
class MyCommentViewModel: BaseViewModel() {

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

        networkState = Transformations.switchMap(factory.cwListDataSource){ it.networkState }
        initialLoad = Transformations.switchMap(factory.cwListDataSource){ it.initialLoad }
    }

    fun invalidateCWList() = cwList.value?.dataSource?.invalidate()

    fun retry() = factory.cwListDataSource.value?.retry()

    fun getMyComment(){
        ApiGenerator.getApiService(ApiServiceNew::class.java)
//                .getUserReplay()
    }
}