package com.mredrock.cyxbs.declare.pages.detail.page.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.declare.pages.detail.bean.CancelChoiceBean
import com.mredrock.cyxbs.declare.pages.detail.bean.DetailBean
import com.mredrock.cyxbs.declare.pages.detail.bean.VotedBean
import com.mredrock.cyxbs.declare.pages.detail.net.DetailApiService
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/13
 * @Description:
 */
class DeclareDetailViewModel : BaseViewModel() {
    val detailLiveData: LiveData<DetailBean>
        get() = _mutableDetailLiveData
    private val _mutableDetailLiveData = MutableLiveData<DetailBean>()

    val votedLiveData: LiveData<VotedBean>
        get() = _mutableVotedLiveData
    private val _mutableVotedLiveData = MutableLiveData<VotedBean>()

    val cancelLiveData: LiveData<CancelChoiceBean>
        get() = _mutableCancelLiveData
    private val _mutableCancelLiveData = MutableLiveData<CancelChoiceBean>()

    /**
     * 获取投票详情数据
     */
    fun getDeclareDetail(id: Int) {
        DetailApiService::class.api
            .getDetailDeclareData(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                toast("${this.throwable.message}")
            }
            .safeSubscribeBy {
                _mutableDetailLiveData.postValue(it)
            }
    }

    /**
     * 投票
     */
    fun putChoice(id: Int, choice: String) {
        DetailApiService::class.api
            .putChoice(id, choice)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                toast("${this.throwable.message}")
            }
            .safeSubscribeBy {
                _mutableVotedLiveData.postValue(it)
            }

    }

    /**
     * 取消投票
     */
    fun cancelChoice(id: Int) {
        DetailApiService::class.api
            .cancelChoice(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                toast("${this.throwable}")
                Log.d("RQ", "cancelChoice: ${this.throwable.printStackTrace()}")
            }
            .safeSubscribeBy {
                _mutableCancelLiveData.postValue(it)
            }
    }
}