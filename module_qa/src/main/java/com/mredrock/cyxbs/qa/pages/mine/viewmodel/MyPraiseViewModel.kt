package com.mredrock.cyxbs.qa.pages.mine.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.beannew.CommentWrapper
import com.mredrock.cyxbs.qa.beannew.Praise
import com.mredrock.cyxbs.qa.pages.mine.model.MyCommentDataSource
import com.mredrock.cyxbs.qa.pages.mine.model.MyPraiseDataSource

/**
 * Author: RayleighZ
 * Time: 2021-03-11 0:32
 */
class MyPraiseViewModel: BaseViewModel() {
    private val factory: MyPraiseDataSource.Factory
    val praiseList: LiveData<PagedList<Praise>>
    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>

    init {
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(3)
                .setPageSize(6)
                .setInitialLoadSizeHint(6)
                .build()
        factory = MyPraiseDataSource.Factory()
        praiseList = LivePagedListBuilder<Int, Praise>(factory, config).build()

        networkState = Transformations.switchMap(factory.praiseDataSourceLiveData) { it.networkState }
        initialLoad = Transformations.switchMap(factory.praiseDataSourceLiveData) { it.initialLoad }
    }

    fun invalidateCWList() = praiseList.value?.dataSource?.invalidate()

    fun retry() = factory.praiseDataSourceLiveData.value?.retry()
}