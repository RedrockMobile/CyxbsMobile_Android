package com.cyxbsmobile_single.module_todo.model

import androidx.room.RoomDatabase
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.database.TodoDatabase
import com.cyxbsmobile_single.module_todo.model.network.Api
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.BackupUrlStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.config.TODO_LAST_MODIFY_TIME
import com.mredrock.cyxbs.common.config.TODO_LAST_SYNC_TIME
import com.mredrock.cyxbs.common.config.TODO_OFFLINE_MODIFY_LIST
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.*

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

    fun getTodoList(onSuccess: (todoList :List<Todo>) -> Unit) {
        //首先试图从远程获取todo
    }

    fun getOfflineModifyTodo(): List<Long> {
        val json = BaseApp.context.defaultSharedPreferences.getString(TODO_OFFLINE_MODIFY_LIST,"[]")
        return Gson().fromJson(json, object : TypeToken<List<Long>>() {}.type)
    }

    //TODO: 逻辑有待优化
    fun addOffLineModifyTodo(id: Long = -1){
        if (id == -1L){
            BaseApp.context.defaultSharedPreferences.editor {
                putString(TODO_OFFLINE_MODIFY_LIST, "[]")
                commit()
            }
            return
        }
        val json = BaseApp.context.defaultSharedPreferences.getString(TODO_OFFLINE_MODIFY_LIST,"[]")
        val arrayList: ArrayList<Long> =  Gson().fromJson(json, object : TypeToken<ArrayList<Long>>() {}.type)
        arrayList.add(id)
        BaseApp.context.defaultSharedPreferences.editor {
            putString(TODO_OFFLINE_MODIFY_LIST, Gson().toJson(arrayList))
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

    fun getFromNet(onSuccess: (todoList :List<Todo>) -> Unit) {
        val lastModifyTime = getLastModifyTime()
        val lastSyncTime = getLastModifyTime()

        when {
            (lastModifyTime == 0L && lastSyncTime == 0L) -> {
                //判定为用户本地无任何数据，需要无条件信任远程数据
                getAllTodoFromNet(onSuccess)
            }

            (lastModifyTime == 0L && lastModifyTime != 0L) -> {
                //判定为用户并未和数据库同步，但是存在本地修改
            }
        }

        if (lastSyncTime == 0L) {
            //证明本地没有同步记录，需要从头同步

        }

    }

    private fun getChangedTodoAndUpdate(onSuccess: (todoList :List<Todo>) -> Unit){

    }

    private fun getAllTodoFromNet(onSuccess: (todoList :List<Todo>) -> Unit){
        apiGenerator
            .queryAllTodo()
            .setSchedulers()
            .safeSubscribeBy(
                onNext = {
                    setLastSyncTime(it.data.syncTime)
                    //本地数据库全部覆盖
                    TodoDatabase.INSTANCE
                        .todoDao()
                        .insertTodoList(it.data.todoArray)
                        .toObservable()
                        .setSchedulers()
                        .safeSubscribeBy {  }
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
}