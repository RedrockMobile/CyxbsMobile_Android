package com.mredrock.cyxbs.discover.pages.discover

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.network.RollerViewInfo
import com.mredrock.cyxbs.discover.network.Services

class DiscoverHomeViewModel : BaseViewModel() {
    val viewPagerInfos = MutableLiveData<List<RollerViewInfo>>()

    fun getRollInfos() {
        ApiGenerator.getApiService(Services::class.java)
                .getRollerViewInfo("5")
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy {
                    viewPagerInfos.value = it
                }
                .lifeCycle()
    }
}
