package com.cyxbsmobile_single.module_todo.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter
import com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter.ShowType.NORMAL
import com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback
import com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog
import com.cyxbsmobile_single.module_todo.util.setMargin
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.DISCOVER_TODO_MAIN
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.dip
import kotlinx.android.synthetic.main.todo_activity_inner_main.*
import kotlinx.android.synthetic.main.todo_rv_item_todo.view.*

@Route(path = DISCOVER_TODO_MAIN)
class TodoInnerMainActivity : BaseViewModelActivity<TodoViewModel>() {

    companion object var CHANGED_FLAG = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_inner_main)
        CHANGED_FLAG = false
        viewModel.initDataList {
            onDateLoaded()
        }
    }

    override fun onResume() {
        super.onResume()
        if (CHANGED_FLAG){
            //私以为都在子线程进行，不会ANR
            viewModel.initDataList {
                onDateLoaded()
            }
        }
    }

    private fun changeItemToChecked(itemView: View) {
        itemView.apply {
            todo_tv_todo_title.setTextColor(Color.parseColor("#6615315B"))
            todo_iv_check.visibility = View.VISIBLE
        }
    }

    private fun onDateLoaded() {
        LogUtils.d("RayJoe", "${viewModel.wrapperList}")
        val adapter =
            DoubleListFoldRvAdapter(viewModel.wrapperList, NORMAL, R.layout.todo_rv_item_todo_inner)
        val callback = SlideCallback()

        todo_inner_home_bar_add.setOnClickListener {
            AddItemDialog(this){
                adapter.addTodo(it)
            }.show()
        }

        todo_inner_home_bar_back.setOnClickListener {
            finish()
        }
        adapter.onBindView = { view, pos, viewType, wrapper ->
            when (viewType) {
                DoubleListFoldRvAdapter.TITLE -> {
                    if (pos != 0) {//确定是已完成事项的title
                        view.setOnClickListener {
                            adapter.changeFoldStatus()
                        }
                    } else {
                        view.isClickable = false
                    }
                }

                DoubleListFoldRvAdapter.TODO -> {
                    view.apply {
                        todo_fl_todo_back.setOnClickListener {
                            wrapper.todo?.let {
                                TodoDetailActivity.startActivity(it, this@TodoInnerMainActivity)
                                CHANGED_FLAG = true
                            }
                        }
                        todo_tv_todo_title.setOnClickListener{
                            wrapper.todo?.let {
                                TodoDetailActivity.startActivity(it, this@TodoInnerMainActivity)
                                CHANGED_FLAG = true
                            }
                        }
                        todo_fl_del.visibility = View.VISIBLE
                        todo_fl_del.setOnClickListener {
                            if (todo_cl_item_main.translationX != 0f){
                                adapter.delItem(wrapper)
                                LogUtils.d("RayJoe", "position = $pos")
                            }
                        }
                        todo_cl_item_main.setBackgroundColor(Color.WHITE)

                        if (wrapper.todo?.remindMode?.notifyDateTime == "") {
                            //判断为没有设置提醒，也没有设置重复
                            //调整上下宽高
                            setMargin(todo_tv_todo_title, top = BaseApp.context.dip(29), bottom = BaseApp.context.dip(29))
                        } else {
                            setMargin(todo_tv_todo_title, top = BaseApp.context.dip(18), bottom = BaseApp.context.dip(40))
                        }

                        todo_iv_todo_item.setOnClickListener {
                            wrapper.todo?.apply {
                                if (!isChecked) {
                                    todo_iv_todo_item.setStatusWithAnime(true) {
                                        adapter.checkItemAndSwap(wrapper)
                                        changeItemToChecked(view)
                                    }
                                } else {
                                    todo_iv_todo_item.isClickable = false
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
}