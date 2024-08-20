package com.cyxbsmobile_single.module_todo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cyxbsmobile_single.module_todo.model.bean.DelPushWrapper
import com.cyxbsmobile_single.module_todo.model.bean.TodoListGetWrapper
import com.cyxbsmobile_single.module_todo.model.bean.TodoListPushWrapper
import com.cyxbsmobile_single.module_todo.model.bean.TodoListSyncTimeWrapper
import com.cyxbsmobile_single.module_todo.model.database.TodoDataBase
import com.cyxbsmobile_single.module_todo.repository.TodoRepository
import com.cyxbsmobile_single.module_todo.util.isNetworkAvailable
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * description:
 * author: sanhuzhen
 * date: 2024/8/17 11:18
 */
class TodoViewModel : BaseViewModel() {

    private val _allTodo = MutableLiveData<TodoListSyncTimeWrapper>()
    private val _changedTodo = MutableLiveData<TodoListGetWrapper>()
    private val _categoryTodo = MutableLiveData<TodoListGetWrapper>()

    val allTodo: LiveData<TodoListSyncTimeWrapper>
        get() = _allTodo
    val changedTodo: LiveData<TodoListGetWrapper>
        get() = _changedTodo
    val categoryTodo: LiveData<TodoListGetWrapper>
        get() = _categoryTodo

    init {
        getAllTodo()
    }

    /**
     * 得到全部todo
     */
    fun getAllTodo() {
        if (isNetworkAvailable(appContext)) {
            TodoRepository
                .queryAllTodo()
                .mapOrInterceptException { }
                .doOnError {
                    viewModelScope.launch(Dispatchers.IO) {
                        val modifyTime = System.currentTimeMillis() / 1000
                        val todoList = TodoDataBase.INSTANCE.todoDao().queryAll()
                        _allTodo.postValue(TodoListSyncTimeWrapper(modifyTime, todoList))
                    }
                }
                .safeSubscribeBy {
                    _allTodo.postValue(it)
                }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val modifyTime = System.currentTimeMillis() / 1000
                val todoList = TodoDataBase.INSTANCE.todoDao().queryAll()
                _allTodo.postValue(TodoListSyncTimeWrapper(modifyTime, todoList))
            }
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
        if (isNetworkAvailable(appContext)) {
            TodoRepository
                .pushTodo(pushWrapper)
                .mapOrInterceptException { }
                .doOnError {
                    viewModelScope.launch {
                        val modifyTime = System.currentTimeMillis() / 1000
                        setLastModifyTime(modifyTime)
                        pushWrapper.todoList.forEach { todo ->
                            TodoDataBase.INSTANCE.todoDao().insert(todo)
                        }
                        getAllTodo()
                    }
                }
                .safeSubscribeBy {
                    if (it.syncTime == getLastModifyTime()) {
                        setLastModifyTime(it.syncTime)
                    }
                    setLastSyncTime(it.syncTime)
                    getAllTodo()
                }
        } else {
            viewModelScope.launch {
                val modifyTime = System.currentTimeMillis() / 1000
                setLastModifyTime(modifyTime)
                pushWrapper.todoList.forEach { todo ->
                    TodoDataBase.INSTANCE.todoDao().insert(todo)
                }
                getAllTodo()
            }
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
            }
    }

    /**
     * 删除todo
     */
    fun delTodo(delPushWrapper: DelPushWrapper) {
        if (isNetworkAvailable(appContext)) {
            TodoRepository
                .delTodo(delPushWrapper)
                .mapOrInterceptException { }
                .doOnError {
                    viewModelScope.launch {
                        val modifyTime = System.currentTimeMillis() / 1000
                        setLastModifyTime(modifyTime)
                        delPushWrapper.delTodoList.forEach { todoId ->
                            TodoDataBase.INSTANCE.todoDao().deleteTodoById(todoId)
                        }
                        getAllTodo()
                    }
                }
                .safeSubscribeBy {
                    if (it.syncTime == getLastModifyTime()) {
                        setLastModifyTime(it.syncTime)
                    }
                    setLastSyncTime(it.syncTime)
                    getAllTodo()
                }
        } else {
            viewModelScope.launch {
                val modifyTime = System.currentTimeMillis() / 1000
                setLastModifyTime(modifyTime)
                delPushWrapper.delTodoList.forEach { todoId ->
                    TodoDataBase.INSTANCE.todoDao().deleteTodoById(todoId)
                }
                getAllTodo()
            }
        }


    }

    /**
     * 得到分组的todo
     */
    fun getTodoByType(type: String) {
        if (!isNetworkAvailable(appContext)) {
            viewModelScope.launch(Dispatchers.IO) {
                val modifyTime = System.currentTimeMillis() / 1000
                val todoList = TodoDataBase.INSTANCE.todoDao().queryByType(type)
                _categoryTodo.postValue(TodoListGetWrapper(todoList, modifyTime))
            }
        } else {
            TodoRepository
                .getTodoByType(type)
                .mapOrInterceptException { }
                .doOnError {
                    val modifyTime = System.currentTimeMillis() / 1000
                    viewModelScope.launch(Dispatchers.IO) {
                        val todoList = TodoDataBase.INSTANCE.todoDao().queryByType(type)
                        _categoryTodo.postValue(TodoListGetWrapper(todoList, modifyTime))
                    }
                }
                .safeSubscribeBy {
                    _categoryTodo.postValue(it)
                }
        }
    }

    /**
     * 得到和设置本地最后修改的时间戳
     */
    private fun getLastModifyTime(): Long =
        appContext.getSp("todo").getLong("TODO_LAST_MODIFY_TIME", 0L)

    private fun setLastModifyTime(modifyTime: Long) {
        appContext.getSp("todo").edit().apply {
            putLong("TODO_LAST_MODIFY_TIME", modifyTime)
            commit()
        }
    }

    /**
     * 得到和设置本地最后同步的时间戳
     */
    private fun getLastSyncTime(): Long =
        appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)

    private fun setLastSyncTime(syncTime: Long) {
        appContext.getSp("todo").edit().apply {
            putLong("TODO_LAST_SYNC_TIME", syncTime)
            commit()
        }
    }

    /**
     * 同步远端与本地数据
     */
    fun syncTodo() {
        if (isNetworkAvailable(appContext)){
            val syncTime = getLastSyncTime()
            val modifyTime = getLastModifyTime()
            if (syncTime != modifyTime){

            }
        }
    }
}