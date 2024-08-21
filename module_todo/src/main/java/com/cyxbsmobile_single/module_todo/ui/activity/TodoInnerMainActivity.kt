package com.cyxbsmobile_single.module_todo.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.ui.dialog.AddTodoDialog
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.mredrock.cyxbs.config.route.DISCOVER_TODO_MAIN
import com.mredrock.cyxbs.lib.base.ui.BaseActivity

/**
 * description:
 * author: sanhuzhen
 * date: 2024/8/20 17:31
 */
@Route(path = DISCOVER_TODO_MAIN)
class TodoInnerMainActivity: BaseActivity() {

    private val mViewModel by lazy {
        ViewModelProvider(this)[TodoViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_main)
        findViewById<Button>(R.id.add_button).setOnClickListener {
            mViewModel.apply {
                allTodo.observe(this@TodoInnerMainActivity){
                    Log.d("TodoInnerMainActivity","${it}")
                }
            }
            AddTodoDialog(this){

            }.show()
        }
    }
}