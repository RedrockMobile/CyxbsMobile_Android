package com.cyxbsmobile_single.module_todo.repository

import com.cyxbsmobile_single.module_todo.model.bean.DelPushWrapper
import com.cyxbsmobile_single.module_todo.model.bean.TodoListPushWrapper
import com.cyxbsmobile_single.module_todo.model.network.TodoApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * description:
 * author: sanhuzhen
 * date: 2024/8/17 11:20
 */
object TodoRepository {

    /**
     * 从数据库获取全部todo
     */
    fun queryAllTodo() = TodoApiService
        .INSTANCE
        .queryAllTodo()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    /**
     * 获取自上次同步到现在之间修改的所有todo
     */
    fun queryChangedTodo(syncTime: Long) = TodoApiService
        .INSTANCE
        .queryChangedTodo(syncTime)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())


    /**
     * 上传todo到数据库
     */
    fun uploadTodo(pushWrapper: TodoListPushWrapper) = TodoApiService
        .INSTANCE
        .pushTodo(pushWrapper)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * 获取最后修改的时间戳
     */
    fun getLastSyncTime(syncTime: Long) = TodoApiService
        .INSTANCE
        .getLastSyncTime(syncTime)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    /**
     * 删除todo
     */
    fun delTodo(delPushWrapper: DelPushWrapper) = TodoApiService
        .INSTANCE
        .delTodo(delPushWrapper)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    /**
     * 获取分组内的ToDo
     */
    fun getGroupTodo() = TodoApiService
        .INSTANCE
        .getGroupTodo()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}