package com.cyxbsmobile_single.module_todo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import kotlinx.android.synthetic.main.todo_fragment_feed.view.*

/**
 * Author: RayleighZ
 * Time: 2021-08-02 11:18
 */
class TodoFeedAdapter(private val curShowTodoList: List<TodoItemWrapper>) :
    BaseFeedFragment.Adapter() {

    lateinit var feedView: View

    override fun onCreateView(context: Context, parent: ViewGroup): View {
        feedView = LayoutInflater.from(context).inflate(R.layout.todo_fragment_feed, parent, false)
        refresh()
        return feedView
    }

    private fun refresh() {
        feedView.apply {
            if (curShowTodoList.isNullOrEmpty()) {
                todo_rv_todo_list.visibility = View.GONE
                todo_tv_feed_empty_notify.visibility = View.VISIBLE
            } else {
                todo_rv_todo_list.visibility = View.VISIBLE
                todo_tv_feed_empty_notify.visibility = View.GONE
                val adapter = DoubleListFoldRvAdapter(ArrayList(curShowTodoList))
                adapter.onBindView = { view, pos, viewType, wrapper ->

                }
                todo_rv_todo_list.adapter = adapter
                todo_rv_todo_list.layoutManager = LinearLayoutManager(context)
            }
        }
    }
}