package com.cyxbsmobile_single.module_todo.ui.activity

import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.database.TodoDatabase
import com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.TODO_ADD_TODO_BY_WIDGET
import com.mredrock.cyxbs.common.config.WIDGET_TODO_RAW
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers

//这里不使用BaseActivity的原因是需要设置成透明的theme
@Route(path = TODO_ADD_TODO_BY_WIDGET)
class WidgetAddTodoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_widget_add_todo)
        AddItemDialog(this){todo, addItemDialog ->
            TodoDatabase.INSTANCE.todoDao()
                .insertTodo(todo)
                .toObservable()
                .setSchedulers()
                .safeSubscribeBy {
                    todo.todoId = it
                    val todoList = ArrayList<Todo>()
                    val json = BaseApp.context.defaultSharedPreferences.getString(WIDGET_TODO_RAW, "")
                    if (json != "") todoList.addAll(
                        Gson().fromJson<List<Todo>>(json, object : TypeToken<List<Todo>>() {}.type)
                    )
                    todoList.add(todo)
                    BaseApp.context.defaultSharedPreferences.editor {
                        putString(WIDGET_TODO_RAW, Gson().toJson(todoList))
                        apply()
                    }
                    LogUtils.d("RayK", Gson().toJson(todoList))
                    this.sendBroadcast(
                        Intent("cyxbs.widget.todo.refresh")
                    )
                    addItemDialog.hide()
                    finish()
                }
        }.show()
    }
}