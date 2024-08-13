package com.cyxbsmobile_single.module_todo.ui.fragment

import RemindMode
import TodoAllAdapter
import TodoDataDetails
import TodoItem
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.cyxbsmobile_single.module_todo.R
import com.mredrock.cyxbs.lib.base.ui.BaseFragment

/**
 * description ：清单下面四个模块之一
 * author :TaiE
 * email : 1607869392@qq.com
 * date : 2024/8/11 20:16
 * version: 1.0
 */
class TodoAllFragment : BaseFragment() {
    private lateinit var todoAllAdapter: TodoAllAdapter
    private val mRecyclerView by R.id.todo_allrv.view<RecyclerView>()
    private lateinit var todoDataDetails:TodoDataDetails
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.todo_fragment_all, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initList()
        todoAllAdapter = TodoAllAdapter()
        mRecyclerView.adapter = todoAllAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        Log.d("rvdatas",todoDataDetails.changed_todo_array.toString())
        todoAllAdapter.submitList(todoDataDetails.changed_todo_array)

    }
//测试用的数据类
    private fun initList() {
        val remindMode1 = RemindMode(
            repeat_mode = 0,
            notify_datetime = "2021年8月24日08:32",
            date = listOf(), // 假设为空
            week = listOf(), // 假设为空
            day = listOf()   // 假设为空
        )

        val remindMode2 = RemindMode(
            repeat_mode = 2,
            notify_datetime = "",
            date = listOf(), // 假设为空
            week = listOf(1, 2),
            day = listOf()   // 假设为空
        )

        // 创建 TodoItem 对象
        val todoItem1 = TodoItem(
            todo_id = 1,
            title = "吃饭电影一条龙",
            remind_mode = remindMode1,
            detail = "和女朋友吃饭",
            last_modify_time = 1561561561L,
            is_done = 0,
            type = "Academics"
        )

        val todoItem2 = TodoItem(
            todo_id = 2,
            title = "通宵打游戏",
            remind_mode = remindMode2,
            detail = "跟男朋友一起通宵打游戏",
            last_modify_time = 1561561561L,
            is_done = 0,
            type = "Academics"
        )

        // 创建 TodoDataDetails 对象
         todoDataDetails = TodoDataDetails(
            changed_todo_array = listOf(todoItem1, todoItem2),
            sync_time = 1561561561L
        )
    }


}