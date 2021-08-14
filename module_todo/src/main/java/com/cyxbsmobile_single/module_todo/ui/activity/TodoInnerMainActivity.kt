package com.cyxbsmobile_single.module_todo.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter
import com.cyxbsmobile_single.module_todo.adapter.`interface`.ItemChangeInterface
import com.cyxbsmobile_single.module_todo.adapter.cnn.getFakeData
import com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.DISCOVER_TODO_MAIN
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import kotlinx.android.synthetic.main.todo_activity_inner_main.*

@Route(path = DISCOVER_TODO_MAIN)
class TodoInnerMainActivity : BaseViewModelActivity<TodoViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_inner_main)
        val arrayList = ArrayList<TodoItemWrapper>(
            getFakeData()
        )
        val adapter = DoubleListFoldRvAdapter(arrayList)
        val callback = SlideCallback()
        adapter.onBindView = { _,_,_ ->

        }
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(todo_inner_home_rv)
        todo_inner_home_rv.adapter = adapter
        todo_inner_home_rv.layoutManager = LinearLayoutManager(this)
    }
}