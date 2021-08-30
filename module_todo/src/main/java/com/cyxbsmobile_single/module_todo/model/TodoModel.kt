package com.cyxbsmobile_single.module_todo.model

import androidx.sqlite.db.SimpleSQLiteQuery
import com.cyxbsmobile_single.module_todo.model.TodoModel.ModifyType.CHANGE
import com.cyxbsmobile_single.module_todo.model.TodoModel.ModifyType.DEL
import com.cyxbsmobile_single.module_todo.model.bean.DelPushWrapper
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.bean.TodoListPushWrapper
import com.cyxbsmobile_single.module_todo.model.database.TodoDatabase
import com.cyxbsmobile_single.module_todo.model.network.Api
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.TODO_LAST_MODIFY_TIME
import com.mredrock.cyxbs.common.config.TODO_LAST_SYNC_TIME
import com.mredrock.cyxbs.common.config.TODO_OFFLINE_DEL_LIST
import com.mredrock.cyxbs.common.config.TODO_OFFLINE_MODIFY_LIST
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.ExecuteOnceObserver
import com.mredrock.cyxbs.common.utils.extensions.*
import io.reactivex.Observable

/**
 * Author: RayleighZ
 * Time: 2021-08-29 0:08
 */
class TodoModel {
    companion object {
        val INSTANCE by lazy { TodoModel() }
        val apiGenerator by lazy { ApiGenerator.getApiService(Api::class.java) }
    }

    val todoList by lazy { ArrayList<Todo>() }

    //获取todolist
    fun getTodoList(onSuccess: (todoList: List<Todo>) -> Unit) {
        //首先试图从远程获取todo
        getFromNet(onSuccess, onError = {
            //G了之后, 从本地数据库拿
            TodoDatabase.INSTANCE.todoDao()
                .queryAllTodo()
                .toObservable()
                .setSchedulers()
                .subscribe(
                    ExecuteOnceObserver(
                        onExecuteOnceNext = {
                            onSuccess(it)
                        }
                    )
                )
        })
    }

    fun updateTodo(todo: Todo, onSuccess: () -> Unit) {
        val syncTime = getLastSyncTime()
        Observable.just(todo)
            .map {
                TodoDatabase.INSTANCE.todoDao()
                    .updateTodo(todo)
            }.setSchedulers().safeSubscribeBy {
                onSuccess.invoke()
            }
        apiGenerator.pushTodo(
            TodoListPushWrapper(
                todoList = listOf(todo),
                syncTime = syncTime,
                force = TodoListPushWrapper.NONE_FORCE,
                firsPush = if (syncTime == 0L) 1 else 0
            )
        ).setSchedulers()
            .safeSubscribeBy(
                onNext = {
                    setLastSyncTime(it.data.syncTime)
                    setLastModifyTime(it.data.syncTime)
                },
                onError = {
                    //缓存为本地修改
                    addOffLineModifyTodo(todo.todoId, CHANGE)
                }
            )
    }

    fun delTodo(todoId: Long, onSuccess: () -> Unit) {
        val syncTime = getLastSyncTime()
        Observable.just(todoId)
            .map {
                TodoDatabase.INSTANCE.todoDao()
                    .deleteTodoById(todoId)
            }.setSchedulers().safeSubscribeBy {
                onSuccess.invoke()
            }
        apiGenerator.delTodo(
            DelPushWrapper(
                delTodoList = listOf(todoId),
                syncTime = syncTime,
                force = TodoListPushWrapper.NONE_FORCE
            )
        ).setSchedulers()
            .safeSubscribeBy(
                onNext = {
                    setLastSyncTime(it.data.syncTime)
                    setLastModifyTime(it.data.syncTime)
                },
                onError = {
                    //缓存为本地修改
                    addOffLineModifyTodo(todoId, DEL)
                }
            )
    }

    fun addTodo(todo: Todo, onSuccess: (todoId: Long) -> Unit) {
        val syncTime = getLastSyncTime()
        TodoDatabase.INSTANCE.todoDao()
            .insertTodo(todo)
            .toObservable()
            .setSchedulers()
            .safeSubscribeBy {
                onSuccess(it)
                todo.todoId = it
                apiGenerator.pushTodo(
                    TodoListPushWrapper(
                        todoList = listOf(todo),
                        syncTime = syncTime,
                        force = TodoListPushWrapper.NONE_FORCE,
                        firsPush = if (syncTime == 0L) 1 else 0
                    )
                ).setSchedulers()
                    .safeSubscribeBy(
                        onNext = {
                            setLastSyncTime(it.data.syncTime)
                            setLastModifyTime(it.data.syncTime)
                        },
                        onError = {
                            //缓存为本地修改
                            addOffLineModifyTodo(todo.todoId, CHANGE)
                        }
                    )
            }
    }

    enum class ModifyType {
        CHANGE,
        DEL
    }

    fun getOfflineModifyTodo(type: ModifyType): List<Long> {
        val key = if (type == CHANGE) TODO_OFFLINE_MODIFY_LIST else TODO_OFFLINE_DEL_LIST
        val json =
            BaseApp.context.defaultSharedPreferences.getString(key, "[]")
        return Gson().fromJson(json, object : TypeToken<List<Long>>() {}.type)
    }

    //TODO: 逻辑有待优化
    fun addOffLineModifyTodo(id: Long = -1, type: ModifyType) {
        val key = if (type == CHANGE) TODO_OFFLINE_MODIFY_LIST else TODO_OFFLINE_DEL_LIST
        if (id == -1L) {
            BaseApp.context.defaultSharedPreferences.editor {
                putString(key, "[]")
                commit()
            }
            return
        }
        val json =
            BaseApp.context.defaultSharedPreferences.getString(key, "[]")
        val arrayList: ArrayList<Long> =
            Gson().fromJson(json, object : TypeToken<ArrayList<Long>>() {}.type)
        arrayList.add(id)
        BaseApp.context.defaultSharedPreferences.editor {
            putString(key, Gson().toJson(arrayList))
            commit()
        }
    }

    fun getLastModifyTime(): Long =
        BaseApp.context.defaultSharedPreferences.getLong(TODO_LAST_MODIFY_TIME, 0L)

    fun getLastSyncTime(): Long =
        BaseApp.context.defaultSharedPreferences.getLong(TODO_LAST_SYNC_TIME, 0L)

    fun setLastModifyTime(modifyTime: Long) = BaseApp.context.defaultSharedPreferences.editor {
        putLong(TODO_LAST_MODIFY_TIME, modifyTime)
        commit()
    }

    fun setLastSyncTime(syncTime: Long) = BaseApp.context.defaultSharedPreferences.editor {
        putLong(TODO_LAST_SYNC_TIME, syncTime)
        commit()
    }

    fun queryByIsDone(isDone: Int, onSuccess: (todoList: List<Todo>) -> Unit) {
        TodoDatabase.INSTANCE.todoDao()
            .queryTodoByWeatherDone(isDone)
            .toObservable()
            .setSchedulers()
            .safeSubscribeBy {
                onSuccess(it)
            }
    }

    private fun getFromNet(
        onSuccess: (todoList: List<Todo>) -> Unit,
        onError: () -> Unit,
        onConflict: (() -> Unit)? = null
    ) {
        val lastModifyTime = getLastModifyTime()
        val lastSyncTime = getLastModifyTime()
        apiGenerator.getLastSyncTime()
            .setSchedulers()
            .safeSubscribeBy(
                onNext = {
                    val remoteSyncTime = it.data.syncTime
                    if (lastSyncTime == remoteSyncTime) {
                        //两端数据同步，不需要拉取，可以使用本地数据
                        TodoDatabase.INSTANCE.todoDao()
                            .queryAllTodo()
                            .toObservable()
                            .setSchedulers()
                            .subscribe(
                                ExecuteOnceObserver(
                                    onExecuteOnceNext = { list ->
                                        onSuccess(list)
                                    }
                                )
                            )

                        //这时需要比较是否存在本地修改，如果存在本地修改，则需要将本地修改重传
                        if (lastModifyTime != lastSyncTime) {
                            resendModifyList(TodoListPushWrapper.NONE_FORCE)
                        }
                    } else {
                        //两端数据不同步，需要判断是否存在本地修改
                        if (lastModifyTime == lastSyncTime) {
                            //不存在本地修改，以远程为主
                            apiGenerator.queryChangedTodo(lastSyncTime)
                                .setSchedulers()
                                .safeSubscribeBy(
                                    onNext = { wrapper ->
                                        //更新本地
                                        Observable.just(wrapper.data.todoList)
                                            .map { list ->
                                                TodoDatabase.INSTANCE
                                                    .todoDao().updateTodoList(list)
                                            }.setSchedulers().safeSubscribeBy { }

                                        //删除本地
                                        Observable.fromArray(wrapper.data.delTodoArray)
                                            .map { idList ->
                                                for (id in idList) {
                                                    TodoDatabase.INSTANCE.todoDao()
                                                        .deleteTodoById(id)
                                                }
                                            }.setSchedulers().safeSubscribeBy { }

                                        //两个时间记录同步处理
                                        setLastSyncTime(wrapper.data.syncTime)
                                        setLastModifyTime(wrapper.data.syncTime)
                                    },

                                    onError = {

                                    }
                                )
                        } else {
                            //冲突了
                            onConflict?.invoke()
                        }
                    }
                },
                onError = {
                    onError.invoke()
                }
            )
    }

    private fun getAllTodoFromNet(onSuccess: (todoList: List<Todo>) -> Unit) {
        apiGenerator
            .queryAllTodo()
            .setSchedulers()
            .safeSubscribeBy(
                onNext = {
                    setLastSyncTime(it.data.syncTime)
                    //本地数据库全部覆盖
                    Observable.just(it.data.todoArray)
                        .map { list ->
                            TodoDatabase.INSTANCE
                                .todoDao()
                                .insertTodoList(list)
                        }.setSchedulers()
                        .safeSubscribeBy { }
                    todoList.clear()
                    todoList.addAll(it.data.todoArray)
                    //本地需要做一次排序
                    todoList.sort()
                    onSuccess.invoke(todoList.toList())
                },

                onError = {
                    BaseApp.context.toast("拉取数据失败，请检查网络状况")
                }
            )
    }

    private fun resendModifyList(isForce: Int) {
        val delList = getOfflineModifyTodo(DEL)
        val changedIdList = getOfflineModifyTodo(CHANGE).toString()
        val syncTime = getLastSyncTime()
        val isFirstPush = if (syncTime == 0L) 1 else 0
        val rawQuery = SimpleSQLiteQuery("SELECT * FROM todo_list WHERE todoId in $changedIdList")
        TodoDatabase.INSTANCE
            .todoDao().queryTodoByIdList(rawQuery)
            .toObservable().setSchedulers()
            .safeSubscribeBy {
                //构建重传PUSH
                val pushWrapper = TodoListPushWrapper(
                    todoList = it,
                    force = isForce,
                    firsPush = isFirstPush,
                    syncTime = syncTime
                )

                apiGenerator.pushTodo(pushWrapper)
                    .setSchedulers()
                    .safeSubscribeBy(
                        onNext = {
                            setLastSyncTime(it.data.syncTime)
                            setLastModifyTime(it.data.syncTime)
                            //制空离线修改
                            addOffLineModifyTodo(type = DEL)
                            addOffLineModifyTodo(type = CHANGE)
                        },
                        onError = {
                            BaseApp.context.toast("本地修改重传失败")
                        }
                    )
            }
    }
}