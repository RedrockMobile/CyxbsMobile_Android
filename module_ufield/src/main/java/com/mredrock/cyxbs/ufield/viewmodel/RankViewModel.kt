package com.mredrock.cyxbs.ufield.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.setSchedulers
import com.mredrock.cyxbs.lib.utils.network.mapOrThrowApiException
import com.mredrock.cyxbs.ufield.bean.RankBean
import com.mredrock.cyxbs.ufield.network.LxhService

class RankViewModel : BaseViewModel() {
    private val _rank = MutableLiveData<List<RankBean>>()
    val rank: LiveData<List<RankBean>> get() = _rank
    fun getRank(
        type: String,
        number: Int,
        order: String
    ) {
        LxhService.INSTANCE.getRank(type, number, order)
            .setSchedulers()
            .mapOrThrowApiException()
            .doOnError {
                toast("服务君似乎打盹了呢~")
            }.safeSubscribeBy {
                _rank.postValue(it)
            }
    }
}