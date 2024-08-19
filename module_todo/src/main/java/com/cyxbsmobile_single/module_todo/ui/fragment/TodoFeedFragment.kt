package com.cyxbsmobile_single.module_todo.ui.fragment

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.TodoConflictAdapter
import com.cyxbsmobile_single.module_todo.adapter.TodoFeedAdapter
import com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog
import com.cyxbsmobile_single.module_todo.ui.widget.TodoWidget
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.mredrock.cyxbs.common.config.DISCOVER_TODO_FEED
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.lib.base.ui.BaseFragment

/**
 * Author: RayleighZ
 * Time: 2021-08-02 10:15
 */
@Route(path = DISCOVER_TODO_FEED)
class TodoFeedFragment : BaseFragment() {
val viewModel by  viewModels<TodoViewModel>()
    private val todoAdapter by lazy { TodoFeedAdapter(viewModel, requireActivity()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setAdapter(todoAdapter)
    }

    private fun refresh() {
        todoAdapter.refresh()
        //发送广播通知小组件更新
        requireContext().sendBroadcast(Intent("cyxbs.widget.todo.refresh").apply {
            component = ComponentName(requireContext(), TodoWidget::class.java)
        })
    }

//    override fun onRefresh() {
//        setTitle(R.string.todo_feed_title)
//        setSubtitle("")
//        setLeftIcon(R.drawable.todo_ic_add)
//        viewModel.initDataList(
//            onLoadSuccess = {
//                refresh()
//            },
//            onConflict = { r, l ->
//                setAdapter(TodoConflictAdapter(r,l){
//                    //当冲突解决了之后应当如何处理
//                    setAdapter(todoAdapter)
//                })
//            }
//        )
//        onLeftIconClick {
//            AddItemDialog(requireContext()) { todo ->
//                viewModel.addTodo(todo) {
//                    refresh()
//                }
//            }.show()
//        }
//    }
}