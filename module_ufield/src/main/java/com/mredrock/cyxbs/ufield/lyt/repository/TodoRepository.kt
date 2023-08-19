package com.mredrock.cyxbs.ufield.lyt.repository

import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.ufield.lyt.bean.TodoBean
import com.mredrock.cyxbs.ufield.lyt.network.TodoApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 *  author : lytMoon
 *  date : 2023/8/19 21:10
 *  description :
 *  version ： 1.0
 */
object TodoRepository {

    /**
     * 获得待审核的活动的数据
     */

    fun receiveTodoData(): Single<ApiWrapper<List<TodoBean>>> {
        return TodoApiService
            .INSTANCE
            .getTodoList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}