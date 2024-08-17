package com.cyxbsmobile_single.module_todo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cyxbsmobile_single.module_todo.model.bean.DelPushWrapper
import com.cyxbsmobile_single.module_todo.model.bean.TodoListGetWrapper
import com.cyxbsmobile_single.module_todo.model.bean.TodoListPushWrapper
import com.cyxbsmobile_single.module_todo.model.bean.TodoListSyncTimeWrapper
import com.cyxbsmobile_single.module_todo.repository.TodoRepository
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException

/**
 * description:
 * author: sanhuzhen
 * date: 2024/8/17 11:18
 */
class TodoViewModel: BaseViewModel() {

    private val _allTodo = MutableLiveData<TodoListSyncTimeWrapper>()
    private val _changedTodo = MutableLiveData<TodoListGetWrapper>()

    val allTodo : LiveData<TodoListSyncTimeWrapper>
            get() = _allTodo
    val changedTodo : LiveData<TodoListGetWrapper>
            get() = _changedTodo

    init {
        getAllTodo()
    }

    /**
     * 得到全部todo
     */
    fun getAllTodo() {
        TodoRepository
            .queryAllTodo()
            .mapOrInterceptException { }
            .doOnError { }
            .safeSubscribeBy {
                Log.d("TodoViewModel2","$it")
                _allTodo.postValue(it)
            }
    }

    /**
     * 获取自上次同步到现在之间修改的所有todo
     */
    fun getChangedTodo(syncTime: Long) {
        TodoRepository
            .queryChangedTodo(syncTime)
            .mapOrInterceptException { }
            .doOnError { }
            .safeSubscribeBy {
                _changedTodo.postValue(it)
            }
    }
    /**
     * 上传todo到数据库
     */
    fun pushTodo(pushWrapper: TodoListPushWrapper) {
        TodoRepository
            .pushTodo(pushWrapper)
            .mapOrInterceptException { }
            .doOnError { }
            .safeSubscribeBy {
                getAllTodo()
            }
    }

    /**
     * 获取最后修改的时间戳
     */
    fun getLastSyncTime(syncTime: Long) {
        TodoRepository
            .getLastSyncTime(syncTime)
            .mapOrInterceptException { }
            .doOnError { }
            .safeSubscribeBy {
                getChangedTodo(it.syncTime)
            }
    }
    /**
     * 删除todo
     */
    fun delTodo(delPushWrapper: DelPushWrapper) {
        TodoRepository
            .delTodo(delPushWrapper)
            .mapOrInterceptException { }
            .doOnError { }
            .safeSubscribeBy {
                getAllTodo()
            }
    }

}