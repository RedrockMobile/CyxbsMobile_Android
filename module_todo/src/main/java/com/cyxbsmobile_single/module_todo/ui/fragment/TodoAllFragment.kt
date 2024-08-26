package com.cyxbsmobile_single.module_todo.ui.fragment

import DragAndDropCallback
import TodoAllAdapter
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAdjusters

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
    private val emptyview by R.id.empty_view.view<View>()
    private val mViewModel: TodoViewModel by activityViewModels()
    private val emptyBottom by R.id.todo_bottom_action_layout_all.view<LinearLayoutCompat>()
    private val acDeleteButton by R.id.button_bottom_right_all.view<FrameLayout>()
    private val acTopButton by R.id.button_bottom_left_all.view<FrameLayout>()
    private val checkall by R.id.todo_bottom_check_al_all.view<CheckBox>()
    private val handler = Handler(Looper.getMainLooper())
    private var pendingUpdateTask: Runnable? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateTimeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy年M月d日HH:mm")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.todo_fragment_all, container, false)
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
//        todoAllAdapter.submitList(todoDataDetails.changed_todo_array)
        val callback = DragAndDropCallback(mRecyclerView, todoAllAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(mRecyclerView)
        initClick()
//        checkIfEmpty()
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

        mViewModel.allTodo.observe(viewLifecycleOwner) {
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
        // 更新isPinned状态并移除当前项
        item.isPinned = 1
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
        if (todoItem.endTime == todoItem.remindMode.notifyDateTime) {
            val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
            todoItem.isChecked = 1
            mViewModel.pushTodo(TodoListPushWrapper(listOf(todoItem), syncTime, 1, 0))
        } else {
            var currentSystemTime = LocalDateTime.now()
            if (todoItem.remindMode.notifyDateTime != "") {
                currentSystemTime = todoItem.remindMode.notifyDateTime?.let { parseDateTime(it) }
            }
            val endTime = todoItem.endTime?.let { parseDateTime(it) }
            val initialRemindTime = currentSystemTime

            viewLifecycleOwner.lifecycleScope.launch {
                val nextRemindTime = withContext(Dispatchers.Default) {
                    when (todoItem.remindMode.repeatMode) {
                        1 -> endTime?.let { calculateNextDailyRemindTime(initialRemindTime, it) }
                        2 -> endTime?.let {
                            calculateNextWeeklyRemindTime(
                                initialRemindTime,
                                todoItem.remindMode.week,
                                it
                            )
                        }

                        3 -> endTime?.let {
                            calculateNextMonthlyRemindTime(
                                initialRemindTime,
                                todoItem.remindMode.day,
                                it
                            )
                        }

                        else -> initialRemindTime
                    }
                }

                Log.d("current", "updateTodoItem: $nextRemindTime")
                nextRemindTime?.let {
                    todoItem.remindMode.notifyDateTime = formatDateTime(it)
                    val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
                    mViewModel.pushTodo(TodoListPushWrapper(listOf(todoItem), syncTime, 0, 0))
                    todoAllAdapter.notifyDataSetChanged()
                    val currentList = todoAllAdapter.currentList.toMutableList()
                    currentList.remove(todoItem)
                    currentList.add(getTopItems(), todoItem)
                    todoAllAdapter.submitList(currentList) {
                        mRecyclerView.scrollToPosition(getTopItems())
                    }
                }
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
        // 初始化为当前提醒时间并设置为当天的00:00:00
        var nextRemindTime = currentRemindTime.withHour(0).withMinute(0).withSecond(0).withNano(0)

        // 确保 weekDays 列表包含合法的值
        val validWeekDays = weekDays.filter { it in 1..7 }
        if (validWeekDays.isEmpty()) {
            Log.w("ReminderCalculation", "No valid week days provided")
            return null
        }

        // 找到下一个符合条件的周一
        // 首先将 nextRemindTime 移动到第二天，确保不是当前日期
        nextRemindTime = nextRemindTime.plusDays(1)

        // 循环直到找到符合条件的下一个日期
        while (nextRemindTime.dayOfWeek.value !in validWeekDays) {
            nextRemindTime = nextRemindTime.plusDays(1)
            if (nextRemindTime.isAfter(endTime)) {
                Log.d("ReminderCalculation", "Next remind time is after end time")
                return endTime // 超过截止时间，不再提醒
            }
        }

        Log.d("运算结果", "calculateNextWeeklyRemindTime: $nextRemindTime")
        return nextRemindTime
    }


    private fun calculateNextMonthlyRemindTime(
        currentRemindTime: LocalDateTime,
        days: List<Int>,
        endTime: LocalDateTime
    ): LocalDateTime? {
        var nextRemindTime = currentRemindTime
        val sortedDays = days.sorted() // 确保 days 是排序的

        while (true) {
            var found = false

            // 遍历 days 中的每一天
            for (day in sortedDays) {
                val possibleRemindTime = nextRemindTime
                    .withDayOfMonth(day)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0)

                if (possibleRemindTime.isAfter(currentRemindTime)) {
                    nextRemindTime = possibleRemindTime
                    found = true
                    break // 找到下一个提醒时间，跳出循环
                }
            }

            // 如果找到的时间在截止时间之前，则返回
            if (found && nextRemindTime.isBefore(endTime)) {
                Log.d("yue运算结果", "calculateNextMonthlyRemindTime: $nextRemindTime")
                return nextRemindTime
            }

            // 如果没有找到合适的日期，增加一个月并重置为第一个日期
            nextRemindTime = nextRemindTime.plusMonths(1).withDayOfMonth(sortedDays[0])

            // 如果超过截止时间，返回截止时间
            if (nextRemindTime.isAfter(endTime)) {
                Log.d("yue运算结果", "calculateNextMonthlyRemindTime: $endTime")
                return endTime
            }
        }
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
        if (item.remindMode.repeatMode != 0 && item.endTime != "") {

            // 取消任何已有任务
            pendingUpdateTask?.let { handler.removeCallbacks(it) }

            // 创建新的任务
            pendingUpdateTask = Runnable {
                updateTodoItem(item)
            }

            // 延迟 2 秒执行新的任务
            pendingUpdateTask?.let { handler.postDelayed(it, 2000) }
        } else {
            val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
            item.isChecked = 1
            mViewModel.pushTodo(TodoListPushWrapper(listOf(item), syncTime, 1, 1))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemnotify(item: Todo) {
        if (item.endTime != "") {
            updateTodoItem(item)
        }

    }

    override fun onResume() {
        mViewModel.getAllTodo()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 取消所有待执行任务
        pendingUpdateTask?.let { handler.removeCallbacks(it) }
    }
}