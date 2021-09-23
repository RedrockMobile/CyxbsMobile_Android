package com.cyxbsmobile_single.module_todo.ui.fragment

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.TodoFeedAdapter
import com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog
import com.cyxbsmobile_single.module_todo.ui.widget.TodoWidget
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.DISCOVER_TODO_FEED
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.common.utils.LogUtils

/**
 * Author: RayleighZ
 * Time: 2021-08-02 10:15
 */
@Route(path = DISCOVER_TODO_FEED)
class TodoFeedFragment: BaseFeedFragment<TodoViewModel>() {
    override var hasTopSplitLine = false

    private val todoAdapter by lazy { TodoFeedAdapter(viewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter(todoAdapter)
    }

    fun refresh(){
        todoAdapter.refresh()
        //发送广播通知小组件更新
        requireContext().sendBroadcast(Intent("cyxbs.widget.todo.refresh").apply {
            component = ComponentName(BaseApp.context, TodoWidget::class.java)
        })
    }

    override fun onRefresh() {
        setTitle(R.string.todo_feed_title)
        setSubtitle("")
        setLeftIcon(R.drawable.todo_ic_add)
        viewModel.initDataList {
            refresh()
        }
        onLeftIconClick {
            AddItemDialog(requireContext()){ todo ->
                viewModel.addTodo(todo){
                    LogUtils.d("Gibson", "on feed refresh")
                    refresh()
                }
            }.show()
        }
    }
}