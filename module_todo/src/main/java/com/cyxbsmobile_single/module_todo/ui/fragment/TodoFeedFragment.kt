package com.cyxbsmobile_single.module_todo.ui.fragment

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.TodoFeedAdapter
import com.cyxbsmobile_single.module_todo.adapter.cnn.getFakeData
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.mredrock.cyxbs.common.config.DISCOVER_TODO_FEED
import com.mredrock.cyxbs.common.ui.BaseFeedFragment

/**
 * Author: RayleighZ
 * Time: 2021-08-02 10:15
 */
@Route(path = DISCOVER_TODO_FEED)
class TodoFeedFragment: BaseFeedFragment<TodoViewModel>() {
    override var hasTopSplitLine = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onRefresh() {
        init()
    }

    private fun init(){
        viewModel.initDataList{
            setAdapter(
                TodoFeedAdapter(
                    when(viewModel.uncheckTodoList.size){
                        0 -> {
                            emptyList()
                        }

                        in 1..2 -> {
                            //数目不不够三条，截取到size就好了
                            viewModel.wrapperList.subList(1, viewModel.uncheckTodoList.size)
                        }

                        else -> {
                            viewModel.wrapperList.subList(1, 4)
                        }
                    }
                )
            )
        }
        setTitle(R.string.todo_feed_title)
        setSubtitle("")
        setLeftIcon(R.drawable.todo_ic_add)
        onLeftIconClick {
            //TODO: 弹出增加dialog
        }
    }
}