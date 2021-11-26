package com.mredrock.cyxbs.qa.pages.mine.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.Message
import com.mredrock.cyxbs.qa.network.ApiServiceNew
import com.mredrock.cyxbs.qa.pages.dynamic.model.DynamicDataSource

/**
 * @date 2021-02-23
 * @author Sca RayleighZ
 */
class MyDynamicViewModel : BaseViewModel(){
    private var factory: DynamicDataSource.Factory?=null

    var dynamicList: LiveData<PagedList<Message>>?=null
    var networkState: LiveData<Int>?=null
    var initialLoad: LiveData<Int>?=null

    val ignorePeople = MutableLiveData<Boolean>()
    var deleteTips = MutableLiveData<Boolean>()


    fun getDynamicData(redid:String?){
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(3)
            .setPageSize(6)
            .setInitialLoadSizeHint(6)
            .build()
        factory = if(redid==null){
            DynamicDataSource.Factory("mine")
        }else{
            DynamicDataSource.Factory("personal",redid)
        }

        dynamicList = LivePagedListBuilder(factory!!, config).build()
        networkState =
            Transformations.switchMap(factory!!.dynamicDataSourceLiveData) { it.networkState }
        initialLoad =
            Transformations.switchMap(factory!!.dynamicDataSourceLiveData) { it.initialLoad }

    }
    fun deleteId(id: String, model: String) {
        ApiGenerator.getApiService(ApiServiceNew::class.java)
            .deleteId(id, model)
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.qa_delete_dynamic_failure
            }
            .safeSubscribeBy {
                deleteTips.value = true
                toastEvent.value = R.string.qa_delete_dynamic_success
            }
    }

    fun invalidateDynamicList() = dynamicList?.value?.dataSource?.invalidate()

    fun retry() = factory?.dynamicDataSourceLiveData?.value?.retry()
}