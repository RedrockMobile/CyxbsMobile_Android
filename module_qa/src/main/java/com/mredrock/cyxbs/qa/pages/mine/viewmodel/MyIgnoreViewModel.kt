package com.mredrock.cyxbs.qa.pages.mine.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.beannew.Ignore
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.pages.mine.model.MyIgnoreDataSource

/**
 * @date 2021-03-05
 * @author Sca RayleighZ
 */
class MyIgnoreViewModel : BaseViewModel() {
    private val factory: MyIgnoreDataSource.Factory
    val ignoreList: LiveData<PagedList<Ignore>>
    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>

    init {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(3)
            .setPageSize(6)
            .setInitialLoadSizeHint(6)
            .build()
        factory = MyIgnoreDataSource.Factory()
        ignoreList = LivePagedListBuilder<Int, Ignore>(factory, config).build()

        networkState =
            Transformations.switchMap(factory.ignoreDataSourceLiveData) { it.networkState }
        initialLoad = Transformations.switchMap(factory.ignoreDataSourceLiveData) { it.initialLoad }
    }

    fun invalidateList() = ignoreList.value?.dataSource?.invalidate()

    fun retry() = factory.ignoreDataSourceLiveData.value?.retry()

    fun antiIgnore(uid: String, onSuccess: () -> Unit) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .cancelIgnoreUid(uid)
            .doOnErrorWithDefaultErrorHandler { true }
            .setSchedulers()
            .unsafeSubscribeBy(
                onNext = {
                    onSuccess()
                },
                onError = {
                    toast(it.toString())
                }
            )
    }
}