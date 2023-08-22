package com.mredrock.cyxbs.ufield.lyt.repository

import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.ufield.lyt.bean.DoneBean
import com.mredrock.cyxbs.ufield.lyt.bean.TodoBean
import com.mredrock.cyxbs.ufield.lyt.network.CheckApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
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
    fun receiveTodoData(): Single<ApiWrapper<List<TodoBean>>> {
        return CheckApiService
            .INSTANCE
            .getTodoList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun sendPass(id: Int): Single<ApiStatus> {
        return CheckApiService
            .INSTANCE
            .passDecision(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun sendReject(id: Int, reason: String): Single<ApiStatus> {
        return CheckApiService
            .INSTANCE
            .rejectDecision(id, reason)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun receiveDoneData(upID:Int):Single<ApiWrapper<List<DoneBean>>> {
        return CheckApiService
            .INSTANCE
            .getDoneList(upID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}