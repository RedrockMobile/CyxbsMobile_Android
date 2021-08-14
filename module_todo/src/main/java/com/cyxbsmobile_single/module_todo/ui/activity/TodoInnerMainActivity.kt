package com.cyxbsmobile_single.module_todo.ui.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
import kotlinx.android.synthetic.main.todo_rv_item_todo.view.*

@Route(path = DISCOVER_TODO_MAIN)
class TodoInnerMainActivity : BaseViewModelActivity<TodoViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_inner_main)
        viewModel.initDataList()
        val adapter = DoubleListFoldRvAdapter(viewModel.wrapperList)
        val callback = SlideCallback()
        adapter.onBindView = { view, pos, viewType, wrapper ->
            if (viewType == DoubleListFoldRvAdapter.TITLE && pos != 0) {
                view.setOnClickListener {
                    adapter.changeFoldStatus()
                }
            } else {
                if (viewType == DoubleListFoldRvAdapter.TODO) {

                    view.todo_fl_del.visibility = View.VISIBLE
                    view.todo_fl_del.setOnClickListener {
                        adapter.delItem(pos)
                    }
                    view.todo_cl_item_main.setBackgroundColor(Color.WHITE)
                    view.todo_clv_todo_item.setOnClickListener {
                        wrapper.todo?.apply {
                            if (!isChecked) {
                                view.todo_clv_todo_item.setStatusWithAnime(true) {
                                    adapter.checkItem(pos)
                                    changeItemToChecked(view)
                                }
                            }
                        }
                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(todo_inner_home_rv)
        todo_inner_home_rv.adapter = adapter
        todo_inner_home_rv.layoutManager = LinearLayoutManager(this)
    }

    private fun changeItemToChecked(itemView: View){
        itemView.apply {
            todo_tv_todo_title.setTextColor(Color.parseColor("#6615315B"))
            todo_iv_check.visibility = View.VISIBLE
        }
    }
}