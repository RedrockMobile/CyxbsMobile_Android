package com.cyxbsmobile_single.module_todo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cyxbsmobile_single.module_todo.model.bean.DelPushWrapper
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.bean.TodoListGetWrapper
import com.cyxbsmobile_single.module_todo.model.bean.TodoListPushWrapper
import com.cyxbsmobile_single.module_todo.model.bean.TodoListSyncTimeWrapper
import com.cyxbsmobile_single.module_todo.model.bean.TodoPinData
import com.cyxbsmobile_single.module_todo.model.database.TodoDatabase
import com.cyxbsmobile_single.module_todo.repository.TodoRepository
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import com.mredrock.cyxbs.lib.utils.utils.LogUtils
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
    private val _categoryTodoStudy = MutableLiveData<TodoListSyncTimeWrapper>()
    private val _categoryTodoLife = MutableLiveData<TodoListSyncTimeWrapper>()
    private val _categoryTodoOther = MutableLiveData<TodoListSyncTimeWrapper>()

    val allTodo: LiveData<TodoListSyncTimeWrapper>
        get() = _allTodo
    val changedTodo: LiveData<TodoListGetWrapper>
        get() = _changedTodo
    val categoryTodoStudy: LiveData<TodoListSyncTimeWrapper>
        get() = _categoryTodoStudy
    val categoryTodoLife: LiveData<TodoListSyncTimeWrapper>
        get() = _categoryTodoLife
    val categoryTodoOther: LiveData<TodoListSyncTimeWrapper>
        get() = _categoryTodoOther
    private val _isEnabled = MutableLiveData<Boolean>()
    val isEnabled: LiveData<Boolean> get() = _isEnabled

    private val _isChanged = MutableLiveData<Boolean>()
    val isChanged: LiveData<Boolean> get() = _isChanged
    var rawTodo: Todo? = null

    fun setEnabled(click: Boolean) {
        _isEnabled.value = click
        LogUtils.d("TodoViewModel", "isEnabled set to ${_isEnabled.value}")
    }

    fun setChangeState(state: Boolean) {
        _isChanged.value = state
    }

    fun judgeChange(todoAfterChange: Todo) {
        _isChanged.value = todoAfterChange != rawTodo
    }

    init {
        _isChanged.value = false
        syncTodo()
    }

    /**
     * 获取所有待办事项
     */
    fun getAllTodo() {
        TodoRepository
            .queryAllTodo()
            .doOnError {
                viewModelScope.launch(Dispatchers.IO) {
                    val modifyTime = System.currentTimeMillis() / 1000
                    val todoList = TodoDatabase.instance.todoDao().queryAll()
                    _allTodo.postValue(todoList?.let { it1 ->
                        TodoListSyncTimeWrapper(
                            it1, modifyTime
                        )
                    })
                }
            }
            .safeSubscribeBy {
                _allTodo.postValue(TodoListSyncTimeWrapper(it.data.todoArray, it.data.syncTime))
                _categoryTodoStudy.postValue(
                    TodoListSyncTimeWrapper(
                        it.data.todoArray.filter { todo -> todo.type == "study" },
                        it.data.syncTime
                    )
                )
                _categoryTodoLife.postValue(
                    TodoListSyncTimeWrapper(
                        it.data.todoArray.filter { todo -> todo.type == "life" },
                        it.data.syncTime
                    )
                )
                _categoryTodoOther.postValue(
                    TodoListSyncTimeWrapper(
                        it.data.todoArray.filter { todo -> todo.type == "other" },
                        it.data.syncTime
                    )
                )
                it.data.syncTime.apply {
                    setLastSyncTime(this)
                }
            }
    }

    /**
     * 获取自上次同步到现在之间修改的所有todo
     */
    fun getChangedTodo(syncTime: Long) {
        TodoRepository
            .queryChangedTodo(syncTime)
            .doOnError { }
            .safeSubscribeBy {
                _changedTodo.postValue(it.data)
            }
    }

    /**
     * 获取最后修改的时间戳
     */
    fun getLastSyncTime(syncTime: Long) {
        TodoRepository
            .getLastSyncTime(syncTime)
            .doOnError { }
            .safeSubscribeBy {
                setLastSyncTime(it.data.syncTime)
            }
    }

    /**
     * 推送todo
     */
    fun pushTodo(pushWrapper: TodoListPushWrapper) {
        TodoRepository
            .pushTodo(pushWrapper)
            .doOnError {
                viewModelScope.launch {
                    val modifyTime = System.currentTimeMillis() / 1000
                    setLastModifyTime(modifyTime)
                    pushWrapper.todoList.forEach { todo ->
                        TodoDatabase.instance.todoDao().insert(todo)
                    }
                    getAllTodo()
                }
            }
            .safeSubscribeBy {
                getAllTodo()
                viewModelScope.launch {
                    setLastModifyTime(it.data.syncTime)
                    pushWrapper.todoList.forEach { todo ->
                        TodoDatabase.instance.todoDao().insert(todo)
                    }
                }
                it.data.syncTime.apply {
                    setLastSyncTime(this)
                }
            }
    }

    /**
     * 详情页更新todo
     */
    fun updateTodo(todo: Todo) {
        val syncTime = getLastSyncTime()
        val pushWrapper = TodoListPushWrapper(
            todoList = listOf(todo),
            syncTime = syncTime,
            force = TodoListPushWrapper.NONE_FORCE,
            firsPush = if (syncTime == 0L) 1 else 0
        )

        TodoRepository.pushTodo(pushWrapper)
            .doOnError {
                viewModelScope.launch {
                    val modifyTime = System.currentTimeMillis() / 1000
                    setLastModifyTime(modifyTime)
                    pushWrapper.todoList.forEach { todo ->
                        TodoDatabase.instance.todoDao().insert(todo)
                    }
                }
            }.safeSubscribeBy {
                viewModelScope.launch {
                    setLastModifyTime(it.data.syncTime)
                    pushWrapper.todoList.forEach { todo ->
                        TodoDatabase.instance.todoDao().insert(todo)
                    }
                }
                it.data.syncTime.apply {
                    setLastSyncTime(this)
                    setLastModifyTime(this)
                }
            }
        rawTodo = todo
    }


    /**
     * 删除todo
     */
    fun delTodo(delPushWrapper: DelPushWrapper) {
        TodoRepository
            .delTodo(delPushWrapper)
            .doOnError {
                viewModelScope.launch {
                    val modifyTime = System.currentTimeMillis() / 1000
                    setLastModifyTime(modifyTime)
                    delPushWrapper.delTodoList.forEach { todoId ->
                        TodoDatabase.instance.todoDao().deleteTodoById(todoId)
                    }
                    getAllTodo()
                }
            }
            .safeSubscribeBy {
                getAllTodo()
                viewModelScope.launch {
                    delPushWrapper.delTodoList.forEach { todoId ->
                        TodoDatabase.instance.todoDao().deleteTodoById(todoId)
                    }
                }
                it.data.syncTime.apply {
                    setLastSyncTime(this)
                    setLastModifyTime(this)
                }
            }
    }

    /**
     * 置顶
     */
    fun pinTodo(todoPinData: TodoPinData) {
        TodoRepository
            .pinTodo(todoPinData)
            .doOnError {
                viewModelScope.launch(Dispatchers.IO) {
                    val todo = TodoDatabase.instance.todoDao().queryById(todoPinData.todoId)
                    if (todo != null) {
                        todo.isPinned = TodoPinData.IS_PIN
                        TodoDatabase.instance.todoDao().insert(todo)
                    }
                }
            }
            .safeSubscribeBy {
                getAllTodo()
                viewModelScope.launch(Dispatchers.IO) {
                    val todo = TodoDatabase.instance.todoDao().queryById(todoPinData.todoId)
                    if (todo != null) {
                        todo.isPinned = TodoPinData.IS_PIN
                        TodoDatabase.instance.todoDao().insert(todo)
                    }
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
        getAllTodo()

        // 本地数据为空，直接同步远端
        if (getLastModifyTime() == 0L) {
            if (allTodo.value?.todoArray?.isEmpty() == false) {
                syncWithRemote()
            }
            return
        }

        // 如果远端数据为空，直接同步本地数据
        if (getLastSyncTime() == 0L) {
            syncWithLocal()
            return
        }

        // 检查同步时间
        val syncTime = getLastSyncTime()
        val modifyTime = getLastModifyTime()

        if (syncTime == modifyTime) {
            return // 数据已同步，无需操作
        }

        // 根据时间戳决定同步方向
        if (modifyTime > syncTime) {
            syncRemoteFromLocal(syncTime, modifyTime)
        } else {
            if (allTodo.value?.todoArray?.isNotEmpty() == true) {
                syncLocalFromRemote()
            }
        }

        getAllTodo()
    }

    private fun syncWithRemote() {
        viewModelScope.launch(Dispatchers.IO) {
            TodoDatabase.instance.todoDao().apply {
                deleteAll()
                insertAll(allTodo.value?.todoArray)
            }
        }
    }

    private fun syncWithLocal() {
        viewModelScope.launch(Dispatchers.IO) {
            TodoDatabase.instance.todoDao().apply {
                pushTodo(TodoListPushWrapper(queryAll()!!, getLastModifyTime(), 1, 0))
            }
        }
    }

    private fun syncRemoteFromLocal(syncTime: Long, modifyTime: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            TodoDatabase.instance.todoDao().apply {
                pushTodo(TodoListPushWrapper(queryAll()!!, syncTime, 1, 0))

                // 得到本地数据库中被删除的元素
                val deletedIds = allTodo.value?.todoArray
                    ?.filterNot { it in queryAll()!! }
                    ?.map { it.todoId }
                    ?.toLongArray() ?: longArrayOf()

                if (deletedIds.isNotEmpty()) {
                    delTodo(DelPushWrapper(deletedIds.toList(), syncTime))
                }
            }
        }
    }

    private fun syncLocalFromRemote() {
        viewModelScope.launch(Dispatchers.IO) {
            TodoDatabase.instance.todoDao().apply {
                deleteAll()
                insertAll(allTodo.value?.todoArray)
            }
        }
    }

}