package com.cyxbsmobile_single.module_todo.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper
import com.mredrock.cyxbs.common.config.TODO_DETAIL_ACTIVITY
import kotlinx.android.synthetic.main.activity_todo_inner_main.*

@Route(path = TODO_DETAIL_ACTIVITY)
class TodoInnerMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_inner_main)
//        todo_inner_home_rv.adapter = adapter
//        todo_inner_home_rv.layoutManager = LinearLayoutManager(this)
    }
}