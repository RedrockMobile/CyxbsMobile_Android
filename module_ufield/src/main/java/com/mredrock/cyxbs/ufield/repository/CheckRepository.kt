package com.mredrock.cyxbs.ufield.repository

import com.mredrock.cyxbs.ufield.network.CheckApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 *  author : lytMoon
 *  date : 2023/8/19 21:10
 *  description :
 *  version ： 1.0
 */
object CheckRepository {

    /**
     * 获得待审核的活动的数据
     */
    fun receiveTodoData() = CheckApiService
        .INSTANCE
        .getTodoList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun receiveTodoUpData(lowerID: Int) = CheckApiService
        .INSTANCE
        .getTodoUpList(lowerID)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun sendPass(id: Int) = CheckApiService
        .INSTANCE
        .passDecision(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun sendReject(id: Int, reason: String) = CheckApiService
        .INSTANCE
        .rejectDecision(id, reason)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun receiveDoneData() = CheckApiService
        .INSTANCE
        .getDoneList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun receiveDoneUpData(upID: Int) = CheckApiService
        .INSTANCE
        .getDoneUpList(upID)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

}