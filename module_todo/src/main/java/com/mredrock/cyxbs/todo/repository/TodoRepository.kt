package com.mredrock.cyxbs.todo.repository

import com.mredrock.cyxbs.todo.model.bean.DelPushWrapper
import com.mredrock.cyxbs.todo.model.bean.TodoListPushWrapper
import com.mredrock.cyxbs.todo.model.bean.TodoPinData
import com.mredrock.cyxbs.todo.model.network.TodoApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * description:
 * author: sanhuzhen
 * date: 2024/8/17 11:20
 */
object TodoRepository {


    fun queryAllTodo() = TodoApiService
        .INSTANCE
        .queryAllTodo()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun queryChangedTodo(syncTime: Long) = TodoApiService
        .INSTANCE
        .queryChangedTodo(syncTime)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())


    fun pushTodo(pushWrapper: TodoListPushWrapper) = TodoApiService
        .INSTANCE
        .pushTodo(pushWrapper)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())


    fun getLastSyncTime(syncTime: Long) = TodoApiService
        .INSTANCE
        .getLastSyncTime(syncTime)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun delTodo(delPushWrapper: DelPushWrapper) = TodoApiService
        .INSTANCE
        .delTodo(delPushWrapper)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun pinTodo(todoPinData: TodoPinData) = TodoApiService
        .INSTANCE
        .pinTodo(todoPinData)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

}