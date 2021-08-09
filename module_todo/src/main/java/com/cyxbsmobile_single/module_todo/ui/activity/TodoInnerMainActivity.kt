package com.cyxbsmobile_single.module_todo.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.DISCOVER_TODO_MAIN
import com.mredrock.cyxbs.common.ui.BaseActivity

@Route(path = DISCOVER_TODO_MAIN)
class TodoInnerMainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_inner_main)
//        todo_inner_home_rv.adapter = adapter
//        todo_inner_home_rv.layoutManager = LinearLayoutManager(this)
    }
}