package com.cyxbsmobile_single.module_todo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import kotlinx.android.synthetic.main.todo_fragment_feed.view.*

/**
 * Author: RayleighZ
 * Time: 2021-08-02 11:18
 */
class TodoFeedAdapter(/*private val curShowTodoList: List<Todo>*/): BaseFeedFragment.Adapter() {
    override fun onCreateView(context: Context, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.todo_fragment_feed, parent, false)
        refresh()
        return view
    }

    private fun refresh(){
        view?.apply {
//            todo_rv_todo_list.adapter =
        }
    }
}