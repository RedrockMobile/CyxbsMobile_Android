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
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

/**
 * Author: RayleighZ
 * Time: 2021-08-29 0:08
 * Describe: Todo模块封装的用于维持本地和远程数据库同步的module
 * 多设备同步逻辑->
 * 最初功能设计的时候，提出了多设备同步的问题，主体处理逻辑如下
 * do 拉取todo
 *      if 本地同步时间和远程修改时间不相等
 *          if 本地存在离线修改
 *              多设备存档冲突，需要向用户展示，让用户选择冲突存档中的某一个
 *          else 本地不存在离线修改
 *              下载远程修改，同步到本地数据库，并更新本地的同步时间
 *      else 修改时间相同
 *          两端数据完全同步，不需要做出任何拉取
 * done
 */
class TodoModel {
    companion object {
        val INSTANCE by lazy { TodoModel() }
        val apiGenerator: Api by lazy { ApiGenerator.getApiService(Api::class.java) }
    }

    private val todoList by lazy { ArrayList<Todo>() }

    //获取todolist
    fun getTodoList(
        onSuccess: (todoList: List<Todo>) -> Unit,
        onConflict: ((remoteSyncTime: Long, localSyncTime: Long) -> Unit)? = null
    ) {
        //首先试图从远程获取todo
        getFromNet(
            onSuccess, onError = {
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
            },
            onConflict = onConflict
        )
    }

    fun updateTodo(todo: Todo, onSuccess: (() -> Unit)? = null) {
        val syncTime = getLastSyncTime()
        Observable.just(todo)
            .map {
                TodoDatabase.INSTANCE.todoDao()
                    .updateTodo(todo)
            }.setSchedulers().unsafeSubscribeBy {
                onSuccess?.invoke()
            }
        apiGenerator.pushTodo(
            TodoListPushWrapper(
                todoList = listOf(todo),
                syncTime = syncTime,
                force = TodoListPushWrapper.NONE_FORCE,
                firsPush = if (syncTime == 0L) 1 else 0
            )
        ).setSchedulers()
            .unsafeSubscribeBy(
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
            }.setSchedulers().unsafeSubscribeBy {
                onSuccess.invoke()
            }
        apiGenerator.delTodo(
            DelPushWrapper(
                delTodoList = listOf(todoId),
                syncTime = syncTime,
                force = TodoListPushWrapper.NONE_FORCE
            )
        ).setSchedulers()
            .unsafeSubscribeBy(
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

    fun getTodoById(todoId: Long, onSuccess: (todo: Todo) -> Unit, onError: () -> Unit) {
        TodoDatabase.INSTANCE.todoDao()
            .queryTodoById(todoId)
            .toObservable()
            .setSchedulers()
            .unsafeSubscribeBy(
                onNext = onSuccess,
                onError = {
                    onError.invoke()
                }
            )
    }

    fun getTodoByIdList(idList: List<Long>, onSuccess: (todo: List<Todo>) -> Unit){
        val rawQuery = SimpleSQLiteQuery("SELECT * FROM todo_list WHERE todoId in (${idList.toString().subSequence(1, idList.toString().length - 1)})")
        TodoDatabase.INSTANCE
                .todoDao().queryTodoByIdList(rawQuery)
                .toObservable().setSchedulers()
                .unsafeSubscribeBy {
                    onSuccess(it)
                }
    }

    fun addTodo(todo: Todo, onSuccess: (todoId: Long) -> Unit) {
        val syncTime = getLastSyncTime()
        TodoDatabase.INSTANCE.todoDao()
            .insertTodo(todo)
            .toObservable()
            .setSchedulers()
            .unsafeSubscribeBy {
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
                    .unsafeSubscribeBy(
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
            BaseApp.appContext.defaultSharedPreferences.getString(key, "[]")
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
        if (type == DEL) {
            //首先获取本地的离线修改todo列表
            val localChangeArray = Gson()
                .fromJson<ArrayList<Long>>(
                    BaseApp.appContext.defaultSharedPreferences.getString(
                        TODO_OFFLINE_MODIFY_LIST,
                        "[]"
                    ),
                    object : TypeToken<ArrayList<Long>>() {}.type
                )
            localChangeArray.remove(id)
            BaseApp.appContext.defaultSharedPreferences.editor {
                putString(
                    TODO_OFFLINE_DEL_LIST,
                    Gson().toJson(localChangeArray)
                )
            }
        }
        val key = if (type == CHANGE) TODO_OFFLINE_MODIFY_LIST else TODO_OFFLINE_DEL_LIST
        if (id == -1L) {
            BaseApp.appContext.defaultSharedPreferences.editor {
                putString(key, "[]")
                commit()
            }
            return
        }
        val json =
            BaseApp.appContext.defaultSharedPreferences.getString(key, "[]")
        val arrayList: ArrayList<Long> =
            Gson().fromJson(json, object : TypeToken<ArrayList<Long>>() {}.type)
        arrayList.add(id)
        BaseApp.appContext.defaultSharedPreferences.editor {
            putString(key, Gson().toJson(arrayList))
            commit()
        }
        //更新本地修改时间
        setLastModifyTime(System.currentTimeMillis() / 1000)
    }

    private fun getLastModifyTime(): Long =
        BaseApp.appContext.defaultSharedPreferences.getLong(TODO_LAST_MODIFY_TIME, 0L)

    private fun getLastSyncTime(): Long =
        BaseApp.appContext.defaultSharedPreferences.getLong(TODO_LAST_SYNC_TIME, 0L)

    private fun setLastModifyTime(modifyTime: Long) =
        BaseApp.appContext.defaultSharedPreferences.editor {
            putLong(TODO_LAST_MODIFY_TIME, modifyTime)
            commit()
        }

    private fun setLastSyncTime(syncTime: Long) = BaseApp.appContext.defaultSharedPreferences.editor {
        putLong(TODO_LAST_SYNC_TIME, syncTime)
        commit()
    }

    fun queryByIsDone(isDone: Int, onSuccess: (todoList: List<Todo>) -> Unit) {
        //粘性事件处理
        var firstTimeGet = true
        TodoDatabase.INSTANCE.todoDao()
            .queryTodoByWeatherDone(isDone)
            .toObservable()
            .setSchedulers()
            .unsafeSubscribeBy {
                if (firstTimeGet) {
                    onSuccess(it)
                    firstTimeGet = false
                }
            }
    }

    private fun getFromNet(
        onSuccess: (todoList: List<Todo>) -> Unit,
        onError: () -> Unit,
        onConflict: ((remoteTime: Long, localTime: Long) -> Unit)? = null
    ) {
        val lastModifyTime = getLastModifyTime()
        val lastSyncTime = getLastSyncTime()
        apiGenerator.getLastSyncTime(lastSyncTime)
            .setSchedulers()
            .unsafeSubscribeBy(
                onNext = {
                    LogUtils.d("RayleighZ", "Sync Success")
                    val remoteSyncTime = it.data.syncTime
                    if (lastSyncTime == 0L) {
                        //本地数据库没有记录
                        //无条件信任远程数据库
                        apiGenerator.queryAllTodo()
                            .setSchedulers()
                            .unsafeSubscribeBy(
                                onNext = { inner ->
                                    //同步本地syncTime
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
                                                .unsafeSubscribeBy { }
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
                                resendModifyList()
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
                                    .unsafeSubscribeBy(
                                        onNext = { wrapper ->
                                            //更新本地
                                            Observable.just(wrapper.data.todoList)
                                                .map { list ->
                                                    TodoDatabase.INSTANCE
                                                        .todoDao().insertTodoList(list)
                                                }.setSchedulers().unsafeSubscribeBy { }
                                            //删除本地
                                            Observable.fromArray(wrapper.data.delTodoArray)
                                                .map { idList ->
                                                    for (id in idList) {
                                                        TodoDatabase.INSTANCE.todoDao()
                                                            .deleteTodoById(id)
                                                    }
                                                }.setSchedulers().unsafeSubscribeBy { }

                                            //两个时间记录同步处理
                                            setLastSyncTime(wrapper.data.syncTime)
                                            setLastModifyTime(wrapper.data.syncTime)
                                        },

                                        onError = {
                                            onError.invoke()
                                        }
                                    )
                            } else {
                                //此时说明存在冲突，需要展示
                                onConflict?.invoke(remoteSyncTime, lastSyncTime)
                            }
                        }
                    }
                },
                onError = {
                    LogUtils.d("RayleighZ", it.toString())
                    onError.invoke()
                }
            )
    }

    fun getAllTodoFromNet(
        withDatabaseSync: Boolean = false,
        onSuccess: (todoList: List<Todo>) -> Unit
    ) {
        apiGenerator
            .queryAllTodo()
            .setSchedulers()
            .unsafeSubscribeBy(
                onNext = {
                    setLastSyncTime(it.data.syncTime)
                    //本地数据库全部覆盖
                    if (withDatabaseSync) {
                        Observable.just(it.data.todoArray ?: emptyList())
                            .map { list ->

                                TodoDatabase.INSTANCE.todoDao()
                                    .deleteAllTodo()

                                TodoDatabase.INSTANCE
                                    .todoDao()
                                    .insertTodoList(list)
                            }.setSchedulers()
                            .unsafeSubscribeBy { }
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
                    BaseApp.appContext.toast("拉取数据失败，请检查网络状况")
                }
            )
    }

    private fun resendModifyList() {
        val delList = getOfflineModifyTodo(DEL)
        val changedIdList = getOfflineModifyTodo(CHANGE).toString()
        val syncTime = getLastSyncTime()
        val isFirstPush = if (syncTime == 0L) 1 else 0
        LogUtils.d("Slayer", "SQL = SELECT * FROM todo_list WHERE todoId in (${changedIdList.subSequence(1, changedIdList.length - 1)})")
        val rawQuery = SimpleSQLiteQuery("SELECT * FROM todo_list WHERE todoId in (${changedIdList.subSequence(1, changedIdList.length - 1)})")
        TodoDatabase.INSTANCE
            .todoDao().queryTodoByIdList(rawQuery)
            .toObservable().setSchedulers()
            .unsafeSubscribeBy(
                onNext = {
                    LogUtils.d("RayleighZ", "offline todo list = $it")
                    if (it.isNotEmpty()){
                        //构建重传PUSH
                        val pushWrapper = TodoListPushWrapper(
                                todoList = it,
                                force = TodoListPushWrapper.NONE_FORCE,
                                firsPush = isFirstPush,
                                syncTime = syncTime
                        )

                        apiGenerator.pushTodo(pushWrapper)
                                .setSchedulers()
                                .unsafeSubscribeBy(
                                        onNext = { syncTime ->
                                            setLastSyncTime(syncTime.data.syncTime)
                                            setLastModifyTime(syncTime.data.syncTime)
                                            //制空离线修改
                                            addOffLineModifyTodo(type = DEL)
                                            addOffLineModifyTodo(type = CHANGE)
                                        },
                                        onError = {
                                            BaseApp.appContext.toast("本地修改重传失败")
                                        }
                                )
                    }


                    if(delList.isNotEmpty()){
                        apiGenerator.delTodo(
                                DelPushWrapper(
                                        delList,
                                        getLastSyncTime()
                                )
                        ).setSchedulers()
                                .unsafeSubscribeBy(
                                        onNext = { syncTime ->
                                            setLastSyncTime(syncTime.data.syncTime)
                                            setLastModifyTime(syncTime.data.syncTime)
                                            //制空离线修改
                                            addOffLineModifyTodo(type = DEL)
                                            addOffLineModifyTodo(type = CHANGE)
                                        },
                                        onError = {
                                            BaseApp.appContext.toast("本地删除重传失败")
                                        }
                                )
                    }
                },
                onError = {
                    LogUtils.d("RayleighZ", "resend error = $it")
                }
            )
    }

    //强制重传所有的todo
    fun forcePush(onSuccess: () -> Unit) {
        //拿到本地的所有todo
        TodoDatabase.INSTANCE.todoDao()
            .queryAllTodo()
            .toObservable()
            .setSchedulers()
            .unsafeSubscribeBy {
                //重传所有todo
                apiGenerator.pushTodo(
                    TodoListPushWrapper(
                        it,
                        getLastSyncTime(),
                        TodoListPushWrapper.IS_FORCE,
                        1
                    )
                )
                    .setSchedulers()
                    .unsafeSubscribeBy(
                        onNext = { syncTime ->
                            onSuccess.invoke()
                            setLastModifyTime(syncTime.data.syncTime)
                            setLastSyncTime(syncTime.data.syncTime)
                        },
                        onError = {
                            //提示用户强制上传失败
                            BaseApp.appContext.toast("强制上传失败 :(")
                        }
                    )
            }
    }
    
    /**
     * [ExecuteOnceObserver] is used to only get one [onNext] Result.
     *
     * @param onExecuteOnceNext The concrete implement of the [onNext]
     * @param onExecuteOnceComplete The concrete implement of the [onComplete]
     * @param onExecuteOnceError The concrete implement of the [onError]
     * @param onExecuteOnFinal When everything is done,[onExecuteOnFinal] is called
     *
     * Created by anriku on 2018/9/18.
     */
    private class ExecuteOnceObserver<T: Any>(
        val onExecuteOnceNext: (T) -> Unit = {},
        val onExecuteOnceComplete: () -> Unit = {},
        val onExecuteOnceError: (Throwable) -> Unit = {},
        val onExecuteOnFinal: () -> Unit = {}
    ) : Observer<T> {
        
        private var mDisposable: Disposable? = null
        
        override fun onComplete() {
            onExecuteOnceComplete()
        }
        
        override fun onSubscribe(d: Disposable) {
            mDisposable = d
        }
        
        override fun onNext(t: T) {
            try {
                onExecuteOnceNext(t)
                this.onComplete()
            } catch (e: Throwable) {
                this.onError(e)
            } finally {
                onExecuteOnFinal()
                if (mDisposable != null && !mDisposable!!.isDisposed) {
                    mDisposable!!.dispose()
                }
            }
        }
        
        override fun onError(e: Throwable) {
            onExecuteOnceError(e)
        }
        
    }
}