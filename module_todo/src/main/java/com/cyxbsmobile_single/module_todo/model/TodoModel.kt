package com.cyxbsmobile_single.module_todo.model

import android.util.Log
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
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.*
import io.reactivex.Observable

/**
 * Author: RayleighZ
 * Time: 2021-08-29 0:08
 * Describe: Todo模块封装的用于维持本地和远程数据库同步的module
 */
class TodoModel {
    companion object {
        val INSTANCE by lazy { TodoModel() }
        val apiGenerator: Api by lazy { ApiGenerator.getApiService(Api::class.java) }
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

    fun getTodoById(todoId: Long, onSuccess: (todo: Todo) -> Unit, onError: () -> Unit){
        TodoDatabase.INSTANCE.todoDao()
                .queryTodoById(todoId)
                .toObservable()
                .setSchedulers()
                .safeSubscribeBy(
                        onNext = onSuccess,
                        onError = {
                            onError.invoke()
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
                        onNext = { syncTime ->
                            setLastSyncTime(syncTime.data.syncTime)
                            setLastModifyTime(syncTime.data.syncTime)
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

    private fun getOfflineModifyTodo(type: ModifyType): List<Long> {
        val key = if (type == CHANGE) TODO_OFFLINE_MODIFY_LIST else TODO_OFFLINE_DEL_LIST
        val json =
            BaseApp.context.defaultSharedPreferences.getString(key, "[]")
        return Gson().fromJson(json, object : TypeToken<List<Long>>() {}.type)
    }

    /**
     * 用于记录离线修改的方法
     * @param id: 被操作todo的id，这里约定，如果id为-1，则表示对type类型进行删除
     * @param type: 操作的类型，DEL表示删除类型，CHANGE表示修改对应类型
     */
    private fun addOffLineModifyTodo(id: Long = -1, type: ModifyType) {
        //如果是离线删除，还需要查找离线修改的id数组里是否有已经离线删除的todo
        //如果就，需要在离线修改的todo列表中删除对应的id
        if (type == DEL){
            //首先获取本地的离线修改todo列表
            val localChangeArray = Gson()
                .fromJson<ArrayList<Long>>(
                    BaseApp.context.defaultSharedPreferences.getString(TODO_OFFLINE_MODIFY_LIST, "[]"),
                    object : TypeToken<ArrayList<Long>>() {}.type
                )
            localChangeArray.remove(id)
            BaseApp.context.defaultSharedPreferences.editor {
                putString(TODO_OFFLINE_DEL_LIST,
                    Gson().toJson(localChangeArray)
                )
            }
        }
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
        //更新本地修改时间
        setLastModifyTime(System.currentTimeMillis() / 1000)
    }

    private fun getLastModifyTime(): Long =
        BaseApp.context.defaultSharedPreferences.getLong(TODO_LAST_MODIFY_TIME, 0L)

    private fun getLastSyncTime(): Long =
        BaseApp.context.defaultSharedPreferences.getLong(TODO_LAST_SYNC_TIME, 0L)

    private fun setLastModifyTime(modifyTime: Long) =
        BaseApp.context.defaultSharedPreferences.editor {
            putLong(TODO_LAST_MODIFY_TIME, modifyTime)
            commit()
        }

    private fun setLastSyncTime(syncTime: Long) = BaseApp.context.defaultSharedPreferences.editor {
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
        val lastSyncTime = getLastSyncTime()
        apiGenerator.getLastSyncTime()
            .setSchedulers()
            .safeSubscribeBy(
                onNext = {
                    val remoteSyncTime = it.data.syncTime
                    if (lastSyncTime == 0L) {
                        //本地数据库没有记录
                        //无条件信任远程数据库
                        apiGenerator.queryAllTodo()
                            .setSchedulers()
                            .safeSubscribeBy(
                                onNext = { inner ->
                                    //首先同步本地syncTime
                                    setLastModifyTime(inner.data.syncTime)
                                    setLastSyncTime(inner.data.syncTime)
                                    if (inner.data.todoArray.isNullOrEmpty()) {
                                        //如果拿到的是空的，会直接执行onError
                                        onError.invoke()
                                    } else {
                                        //排一下序
                                        inner.data.todoArray?.let { todoArray ->
                                            inner.data.todoArray = todoArray.sorted()
                                            onSuccess(todoArray)
                                            //Insert到本地数据库
                                            Observable.just(todoArray)
                                                .map { list ->
                                                    TodoDatabase.INSTANCE
                                                        .todoDao()
                                                        .insertTodoList(list)
                                                }
                                                .setSchedulers()
                                                .safeSubscribeBy { }
                                        }
                                    }
                                }
                            )
                    } else {
                        //本地数据库有记录
                        if (lastSyncTime == remoteSyncTime) {
                            //本地数据库和远程同步时间相同，可以相信本地数据库
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
                            //如果存在本地修改，需要重传
                            if (lastModifyTime != lastSyncTime) {
                                resendModifyList(TodoListPushWrapper.NONE_FORCE)
                            }
                        } else {
                            //存在另一台设备的数据贡献
                            if (lastModifyTime == lastSyncTime) {
                                //不存在本地修改，以远程为主
                                getAllTodoFromNet { todoList ->
                                    onSuccess.invoke(todoList)
                                }
                                //获取修改，更新本地数据库
                                apiGenerator.queryChangedTodo(lastSyncTime)
                                    .setSchedulers()
                                    .safeSubscribeBy(
                                        onNext = { wrapper ->
                                            //更新本地
                                            Observable.just(wrapper.data.todoList)
                                                .map { list ->
                                                    TodoDatabase.INSTANCE
                                                        .todoDao().insertTodoList(list)
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
                                            onError.invoke()
                                        }
                                    )
                            } else {
                                onConflict?.invoke()
                            }
                        }
                    }
                },
                onError = {
                    onError.invoke()
                }
            )
    }

    private fun getAllTodoFromNet(
        withDatabaseSync: Boolean = false,
        onSuccess: (todoList: List<Todo>) -> Unit
    ) {
        apiGenerator
            .queryAllTodo()
            .setSchedulers()
            .safeSubscribeBy(
                onNext = {
                    setLastSyncTime(it.data.syncTime)
                    //本地数据库全部覆盖
                    if (withDatabaseSync) {
                        Observable.just(it.data.todoArray)
                            .map { list ->
                                TodoDatabase.INSTANCE
                                    .todoDao()
                                    .insertTodoList(list)
                            }.setSchedulers()
                            .safeSubscribeBy { }
                    }
                    todoList.clear()
                    it.data.todoArray?.let { list ->
                        todoList.addAll(list)
                    }
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
            .safeSubscribeBy(
                onNext = {
                    LogUtils.d("RayleighZ", "offline todo list = $it")
                    //构建重传PUSH
                    val pushWrapper = TodoListPushWrapper(
                        todoList = it,
                        force = isForce,
                        firsPush = isFirstPush,
                        syncTime = syncTime
                    )

                    apiGenerator.delTodo(
                        DelPushWrapper(
                            delList,
                            getLastSyncTime()
                        )
                    ).setSchedulers()
                        .safeSubscribeBy(
                            onNext = { syncTime ->
                                setLastSyncTime(syncTime.data.syncTime)
                                setLastModifyTime(syncTime.data.syncTime)
                                //制空离线修改
                                addOffLineModifyTodo(type = DEL)
                                addOffLineModifyTodo(type = CHANGE)
                            },
                            onError = {
                                BaseApp.context.toast("本地删除重传失败")
                            }
                        )

                    apiGenerator.pushTodo(pushWrapper)
                        .setSchedulers()
                        .safeSubscribeBy(
                            onNext = { syncTime ->
                                setLastSyncTime(syncTime.data.syncTime)
                                setLastModifyTime(syncTime.data.syncTime)
                                //制空离线修改
                                addOffLineModifyTodo(type = DEL)
                                addOffLineModifyTodo(type = CHANGE)
                            },
                            onError = {
                                BaseApp.context.toast("本地修改重传失败")
                            }
                        )
                },
                onError = {
                    LogUtils.d("RayleighZ", "resend error = $it")
                }
            )
    }
}