package com.cyxbsmobile_single.module_todo.ui.fragment

import DragAndDropCallback
import TodoAllAdapter
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.SwipeDeleteRecyclerView
import com.cyxbsmobile_single.module_todo.model.bean.DelPushWrapper
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.model.bean.TodoListPushWrapper
import com.cyxbsmobile_single.module_todo.model.bean.TodoPinData
import com.cyxbsmobile_single.module_todo.ui.activity.TodoDetailActivity
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * description ：清单下面四个页面之一
 * author :TaiE
 * email : 1607869392@qq.com
 * date : 2024/8/11 20:16
 * version: 1.0
 */
class TodoLifeFragment : BaseFragment(), TodoAllAdapter.OnItemClickListener {
    private lateinit var todoAllAdapter: TodoAllAdapter
    private val mRecyclerView by R.id.todo_liferv.view<SwipeDeleteRecyclerView>()
    private val emptyview by R.id.empty_view.view<View>()
    private val emptyBottom by R.id.todo_bottom_action_layout_life.view<LinearLayoutCompat>()
    private val acDeleteButton by R.id.button_bottom_right_life.view<FrameLayout>()
    private val acTopButton by R.id.button_bottom_left_life.view<FrameLayout>()
    private val checkall by R.id.todo_bottom_check_al_life.view<CheckBox>()
    private val mViewModel: TodoViewModel by activityViewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateTimeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy年M月d日HH:mm")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.todo_fragment_life, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

        todoAllAdapter = TodoAllAdapter(this)
        mRecyclerView.adapter = todoAllAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        initList()
        val callback = DragAndDropCallback(mRecyclerView, todoAllAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(mRecyclerView)
        initClick()
        checkIfEmpty()
        ifClick()
        todoAllAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkIfEmpty()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                checkIfEmpty()
            }
        })

    }

    private fun initClick() {


        acDeleteButton.setOnClickListener {
            DeleteTodoDialog.Builder(requireContext())
                .setPositiveClick {
                    // 移除指定位置的 item
                    todoAllAdapter.deleteSelectedItems()
                    val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
                    mViewModel.delTodo(
                        DelPushWrapper(
                            todoAllAdapter.selectItems.map { it.todoId },
                            syncTime
                        )

                    )
                    for (item in todoAllAdapter.selectItems) {
                        Log.d("SwipeDeleteRecyclerView", "deletePinning item: ${item.todoId}")
                    }
                    todoAllAdapter.selectItems.clear()
                    dismiss()
                }.setNegativeClick {
                    dismiss()
                }.show()

        }
        acTopButton.setOnClickListener {
            val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
            Log.d("SwipeDeleteRecyclerView", "Pinning item: ${todoAllAdapter.selectItems}")

            for (item in todoAllAdapter.selectItems) {
                mViewModel.pinTodo(TodoPinData(1, 1, syncTime.toInt(), item.todoId.toInt()))
            }
            todoAllAdapter.topSelectedItems()
        }
        checkall.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                todoAllAdapter.selectedall()
            } else {
                todoAllAdapter.toSelectedall()
            }
        }
    }

    private fun ifClick() {
        mViewModel.isEnabled.observe(viewLifecycleOwner) {
            Log.d("TodoAllFragment", "isEnabled changed: $it")
            if (it) {
                showBatchManagementLayout()
            } else {
                hideBatchManagementLayout()
            }
        }


    }

    private fun hideBatchManagementLayout() {
        todoAllAdapter.updateEnabled(false)
        emptyBottom.visibility = View.GONE

    }

    private fun showBatchManagementLayout() {
        emptyBottom.visibility = View.VISIBLE
        todoAllAdapter.updateEnabled(true)
    }

    private fun checkIfEmpty() {
        Log.d("TodoAllFragment", "Checking if empty. Item count: ${todoAllAdapter.itemCount}")
        if (todoAllAdapter.itemCount == 0) {
            mRecyclerView.visibility = View.GONE
            emptyview.visibility = View.VISIBLE
        } else {
            mRecyclerView.visibility = View.VISIBLE
            emptyview.visibility = View.GONE
        }
    }

    private fun initList() {

        mViewModel.categoryTodoLife.observe(viewLifecycleOwner) {
            todoAllAdapter.submitList(it.todoArray) {
                checkIfEmpty()
            }
        }

    }

    //处理点击事件
    override fun onItemClick(item: Todo) {
        TodoDetailActivity.startActivity(item, requireContext())
    }

    override fun onListtextClick(item: Todo) {
        TodoDetailActivity.startActivity(item, requireContext())

    }

    @SuppressLint("MissingInflatedId")
    override fun ondeleteButtonClick(item: Todo, position: Int) {
        val currentList = todoAllAdapter.currentList.toMutableList()

        // 检查索引是否在当前列表的有效范围内
        if (position >= 0 && position < currentList.size) {
            DeleteTodoDialog.Builder(requireContext())
                .setPositiveClick {
                    val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
                    mViewModel.delTodo(DelPushWrapper(listOf(item.todoId), syncTime))
                    // 移除指定位置的 item
                    currentList.removeAt(position)
                    // 提交更新后的列表
                    todoAllAdapter.submitList(currentList)
                    dismiss()
                }.setNegativeClick {
                    dismiss()
                }.show()


        } else {
            // 如果索引无效，记录错误日志
            Log.e("TodoAllFragment", "Invalid index: $position, Size: ${currentList.size}")

        }
    }

    override fun ontopButtonClick(item: Todo, position: Int) {
        val currentList = todoAllAdapter.currentList.toMutableList()
        // 移除当前项
        currentList.removeAt(position)

        // 将项添加到列表的顶部
        currentList.add(0, item)

        val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)

        mViewModel.pinTodo(TodoPinData(1, 1, syncTime.toInt(), item.todoId.toInt()))
        // 提交更新后的列表
        todoAllAdapter.submitList(currentList) {
            // 滚动到顶部以显示置顶的项
            mRecyclerView.scrollToPosition(0)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTodoItem(todoItem: Todo) {
        if (todoItem.endTime==todoItem.remindMode.notifyDateTime){
            val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
            todoItem.isChecked = 1
            mViewModel.pushTodo(TodoListPushWrapper(listOf(todoItem), syncTime, 1, 0))
        }else{
            // 获取系统当前时间
            var currentSystemTime = LocalDateTime.now()
            if (todoItem.remindMode.notifyDateTime!=""){
                currentSystemTime = todoItem.remindMode.notifyDateTime?.let { parseDateTime(it) }
            }

            // 使用截止时间
            val endTime = todoItem.endTime?.let { parseDateTime(it) } // 截止时间

            // 如果没有初始提醒时间，则使用当前系统时间
            val initialRemindTime = currentSystemTime

            // 根据重复模式计算下一个提醒时间
            val nextRemindTime = when (todoItem.remindMode.repeatMode) {
                1 -> endTime?.let {
                    calculateNextDailyRemindTime(
                        initialRemindTime,
                        it
                    )
                }               // 每天提醒
                2 -> endTime?.let {
                    calculateNextWeeklyRemindTime(
                        initialRemindTime, todoItem.remindMode.week,
                        it
                    )
                } // 每周提醒
                3 -> endTime?.let {
                    calculateNextMonthlyRemindTime(
                        initialRemindTime, todoItem.remindMode.day,
                        it
                    )
                } // 每月提醒

                else -> initialRemindTime
            }
            Log.d("current", "updateTodoItem: $nextRemindTime")
            if (nextRemindTime != null) {
                // 创建新的待办事项
                todoItem.remindMode.notifyDateTime = formatDateTime(nextRemindTime)
                // 将新的待办事项添加到列表顶部
                val currentList = todoAllAdapter.currentList.toMutableList()
                currentList.remove(todoItem)

                currentList.add(getTopItems(), todoItem)
                todoAllAdapter.submitList(currentList) {
                    todoAllAdapter.notifyItemChanged(0)
                }
                val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
                mViewModel.pushTodo(TodoListPushWrapper(listOf(todoItem), syncTime, 1, 0))
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextDailyRemindTime(
        currentRemindTime: LocalDateTime,
        endTime: LocalDateTime
    ): LocalDateTime? {
        var nextRemindTime =
            currentRemindTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
        if (nextRemindTime.isAfter(endTime)) {
            return endTime
        }
        return nextRemindTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextWeeklyRemindTime(
        currentRemindTime: LocalDateTime,
        weekDays: List<Int>,
        endTime: LocalDateTime
    ): LocalDateTime? {
        var nextRemindTime = currentRemindTime.withHour(0).withMinute(0).withSecond(0).withNano(0)

        // 确保 weekDays 列表包含合法的值
        val validWeekDays = weekDays.filter { it in 1..7 }
        if (validWeekDays.isEmpty()) {
            Log.w("ReminderCalculation", "No valid week days provided")
            return null
        }

        // 找到下一个符合条件的周一
        while (nextRemindTime.dayOfWeek.value !in validWeekDays) {
            nextRemindTime = nextRemindTime.plusDays(1)
            if (nextRemindTime.isAfter(endTime)) {
                Log.d("ReminderCalculation", "Next remind time is after end time")
                return endTime // 超过截止时间，不再提醒
            }
        }

        return nextRemindTime
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextMonthlyRemindTime(
        currentRemindTime: LocalDateTime,
        days: List<Int>,
        endTime: LocalDateTime
    ): LocalDateTime? {
        var nextRemindTime = currentRemindTime
        val sortedDays = days.sorted() // 确保days是排序的
        do {
            // 遍历days中的每一天
            for (day in sortedDays) {
                nextRemindTime = nextRemindTime
                    .withDayOfMonth(day)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0)

                if (nextRemindTime.isAfter(currentRemindTime)) {
                    break // 找到下一个提醒时间，跳出循环
                }
            }

            // 如果没有找到合适的日期，就增加一个月
            if (nextRemindTime.isBefore(currentRemindTime) || nextRemindTime.isEqual(currentRemindTime)) {
                nextRemindTime = nextRemindTime.plusMonths(1).withDayOfMonth(sortedDays[0])
            }

        } while (nextRemindTime.isBefore(endTime))

        if (nextRemindTime.isAfter(endTime)) {
            return endTime // 超过截止时间
        }
        return nextRemindTime
    }

    private fun getTopItems(): Int {
        var topitems = 0;
        for (todo in todoAllAdapter.currentList) {
            // 对每个 todo 对象执行操作
            if (todo.isPinned == 1) {
                topitems++
            }
        }
        return topitems
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDateTime(dateTimeStr: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeStr, dateTimeFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(dateTimeFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onFinishCheck(item: Todo) {
        if (item.remindMode.repeatMode != 0 && item.remindMode.notifyDateTime != "") {
            updateTodoItem(item)
        } else {
            val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
            item.isChecked = 1
            mViewModel.pushTodo(TodoListPushWrapper(listOf(item), syncTime, 1, 0))
        }
    }

    override fun onResume() {
        mViewModel.getAllTodo()
        super.onResume()
    }

}