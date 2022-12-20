package com.cyxbsmobile_single.module_todo.service

import android.content.Context
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.api.todo.ITodoService
import com.mredrock.cyxbs.api.todo.TODO_SERVICE
import com.mredrock.cyxbs.common.config.DISCOVER_TODO_FEED

/**
 * Author: RayleighZ
 * Time: 2021-08-09 15:58
 * Describe: 提供Feed的类
 */
@Route(path = TODO_SERVICE, name = TODO_SERVICE)
class TodoService : ITodoService {
    override fun getTodoFeed(): Fragment =
        ARouter.getInstance().build(DISCOVER_TODO_FEED).navigation() as Fragment

    override fun init(context: Context?) {

    }
}