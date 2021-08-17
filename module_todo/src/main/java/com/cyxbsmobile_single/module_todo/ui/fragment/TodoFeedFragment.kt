package com.cyxbsmobile_single.module_todo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.TodoFeedAdapter
import com.cyxbsmobile_single.module_todo.adapter.cnn.getFakeData
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
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

    override fun onRefresh() {
        setTitle(R.string.todo_feed_title)
        setSubtitle("")
        setLeftIcon(R.drawable.todo_ic_add)
        viewModel.initDataList {
            todoAdapter.refresh()
        }
        onLeftIconClick {
            //TODO: 弹出增加dialog
        }
    }
}