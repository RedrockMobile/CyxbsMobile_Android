package com.cyxbsmobile_single.module_todo.service

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.room.RoomDatabase
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cyxbsmobile_single.api_todo.ITodoService
import com.cyxbsmobile_single.module_todo.model.database.TodoDatabase
import com.mredrock.cyxbs.common.config.DISCOVER_TODO_FEED
import com.mredrock.cyxbs.common.config.TODO_SERVICE

/**
 * Author: RayleighZ
 * Time: 2021-08-09 15:58
 */
@Route(path = TODO_SERVICE, name = TODO_SERVICE)
class TodoService : ITodoService {
    override fun getTodoFeed(): Fragment = ARouter.getInstance().build(DISCOVER_TODO_FEED).navigation() as Fragment
    override fun init(context: Context?) {

    }
}