package com.mredrock.cyxbs.mine.page.myproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.MyProduct
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.disposeAll
import com.mredrock.cyxbs.mine.util.extension.normalWrapper
import com.mredrock.cyxbs.mine.util.widget.RvFooter
import io.reactivex.disposables.Disposable

/**
 * Created by roger on 2020/2/15
 */
class MyProductViewModel : BaseViewModel() {
    private val disposableForUnClaimed: MutableList<Disposable> = mutableListOf()
    private val disposableForClaimed: MutableList<Disposable> = mutableListOf()

    private val pageSize = 6
    private var unclaimedPage: Int = 1
    private var claimedPage: Int = 1

    //未领取的物品
    private val _eventOnUnclaimed = MutableLiveData<RvFooter.State>()
    val eventOnUnClaimed: LiveData<RvFooter.State>
        get() = _eventOnUnclaimed

    private val _unclaimedList = MutableLiveData<MutableList<MyProduct>>()
    val unclaimedList: LiveData<List<MyProduct>>
        get() = Transformations.map(_unclaimedList) {
            it.toList()
        }

    fun loadMyProductUnclaimed() {
        val disposable = apiService.getMyProducts(unclaimedPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = { list ->
                            if (list.isEmpty()) {
                                if (_unclaimedList.value.isNullOrEmpty()) {
                                    _eventOnUnclaimed.postValue(RvFooter.State.NOTHING)
                                    return@safeSubscribeBy
                                } else {
                                    _eventOnUnclaimed.postValue(RvFooter.State.NOMORE)
                                    return@safeSubscribeBy
                                }
                            }
                            val local = _unclaimedList.value ?: mutableListOf()
                            local.addAll(list.filter {
                                it.isReceived == 0
                            })
                            _unclaimedList.postValue(local)
                            //下一页
                            loadMyProductUnclaimed()
                        },
                        onError = {
                            _eventOnUnclaimed.postValue(RvFooter.State.ERROR)
                        }
                ).lifeCycle()
        disposableForUnClaimed.add(disposable)
    }

    fun cleanUnclaimedPage() {
        disposeAll(disposableForUnClaimed)

        unclaimedPage = 1
        _unclaimedList.value = mutableListOf()
    }

    //已领取的物品
    private val _eventOnClaimed = MutableLiveData<RvFooter.State>()
    val eventOnClaimed: LiveData<RvFooter.State>
        get() = _eventOnClaimed

    private val _claimedList = MutableLiveData<MutableList<MyProduct>>()
    val claimedList: LiveData<List<MyProduct>>
        get() = Transformations.map(_claimedList) {
            it.toList()
        }

    fun loadMyProductClaimed() {
        val disposable = apiService.getMyProducts(claimedPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = { list ->
                            if (list.isEmpty()) {
                                if (_claimedList.value.isNullOrEmpty()) {
                                    _eventOnClaimed.postValue(RvFooter.State.NOTHING)
                                    return@safeSubscribeBy
                                } else {
                                    _eventOnClaimed.postValue(RvFooter.State.NOMORE)
                                    return@safeSubscribeBy
                                }
                            }
                            val local = _claimedList.value ?: mutableListOf()
                            local.addAll(list.filter {
                                it.isReceived == 1
                            })
                            _claimedList.postValue(local)
                            //下一页
                            loadMyProductClaimed()
                        },
                        onError = {
                            _eventOnClaimed.postValue(RvFooter.State.ERROR)
                        }
                ).lifeCycle()
        disposableForClaimed.add(disposable)
    }

    fun cleanClaimedPage() {
        disposeAll(disposableForClaimed)

        claimedPage = 1
        _claimedList.value = mutableListOf()
    }
}