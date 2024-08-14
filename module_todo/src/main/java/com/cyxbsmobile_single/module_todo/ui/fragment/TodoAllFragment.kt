package com.cyxbsmobile_single.module_todo.ui.fragment

import RemindMode
import SwipeCallback
import TodoAllAdapter
import TodoDataDetails
import TodoItem
import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.SwipeDeleteRecyclerView
import com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback
import com.mredrock.cyxbs.lib.base.ui.BaseFragment

/**
 * description ：清单下面四个页面之一
 * author :TaiE
 * email : 1607869392@qq.com
 * date : 2024/8/11 20:16
 * version: 1.0
 */
class TodoAllFragment : BaseFragment(), TodoAllAdapter.OnItemClickListener {
    private lateinit var todoAllAdapter: TodoAllAdapter
    private val mRecyclerView by R.id.todo_allrv.view<SwipeDeleteRecyclerView>()
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
        todoAllAdapter = TodoAllAdapter(this)
        mRecyclerView.adapter = todoAllAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(context)
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
    //处理点击事件
    override fun onItemClick(item: TodoItem) {
        Toast.makeText(context,"不好意思，多模块还没做",Toast.LENGTH_SHORT).show()
        Log.d("click","点击事件触发")
    }

    override fun ondeleteButtonClick(item: TodoItem,position: Int) {
        val currentList = todoAllAdapter.currentList.toMutableList()

        // 检查索引是否在当前列表的有效范围内
        if (position >= 0 && position < currentList.size) {
            // 移除指定位置的 item
            currentList.removeAt(position)

            // 提交更新后的列表
            todoAllAdapter.submitList(currentList)
        } else {
            // 如果索引无效，记录错误日志
            Log.e("TodoAllFragment", "Invalid index: $position, Size: ${currentList.size}")
            // 可选：你可以显示一个 Toast 或其他 UI 提示来通知用户
        }
    }

    override fun ontopButtonClick(item: TodoItem,position: Int) {
        val currentList = todoAllAdapter.currentList.toMutableList()

        // 移除当前项
        currentList.removeAt(position)

        // 将项添加到列表的顶部
        currentList.add(0, item)

        // 提交更新后的列表
        todoAllAdapter.submitList(currentList) {
            // 滚动到顶部以显示置顶的项
            mRecyclerView.scrollToPosition(0)
        }
    }


}