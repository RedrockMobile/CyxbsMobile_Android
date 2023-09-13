package com.mredrock.cyxbs.declare.pages.main.page.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.declare.pages.main.bean.VotesBean
import com.mredrock.cyxbs.declare.pages.main.net.HomeApiService
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/3/25
 * @Description:
 */
class PostedViewModel : BaseViewModel() {
    val postedLiveData: LiveData<List<VotesBean>>
        get() = _mutablePostedLiveData
    private val _mutablePostedLiveData = MutableLiveData<List<VotesBean>>()

    val postedErrorLiveData: LiveData<Boolean>
        get() = _mutablePostedErrorLiveData
    private val _mutablePostedErrorLiveData = MutableLiveData<Boolean>()

    /**
     * 获取自己发布过的投票
     */
    fun getPostedVotes() {
        HomeApiService::class.api
            .getPostedVotes()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                _mutablePostedErrorLiveData.postValue(true)
            }
            .safeSubscribeBy {
                _mutablePostedErrorLiveData.postValue(false)
                _mutablePostedLiveData.postValue(it)
            }
    }
}