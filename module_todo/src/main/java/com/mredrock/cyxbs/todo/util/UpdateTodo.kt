package com.mredrock.cyxbs.todo.util
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import com.mredrock.cyxbs.todo.adapter.SwipeDeleteRecyclerView
import com.mredrock.cyxbs.todo.adapter.TodoAllAdapter
import com.mredrock.cyxbs.todo.model.bean.DelPushWrapper
import com.mredrock.cyxbs.todo.model.bean.Todo
import com.mredrock.cyxbs.todo.model.bean.TodoListPushWrapper
import com.mredrock.cyxbs.todo.viewmodel.TodoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList

/**
 * @Project: CyxbsMobile_Android
 * @File: UpdateTodo
 * @Author: 86199
 * @Date: 2024/9/15
 * @Description: 用于计算提醒时间的帮助类
 */
object TodoHelper {

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日HH:mm")

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTodoItem(
        todoItem: Todo,
        appContext: Context,
        viewModel: TodoViewModel,
        todoAllAdapter: TodoAllAdapter,
        recyclerView: SwipeDeleteRecyclerView,
        lifecycleOwner: LifecycleOwner // 添加 LifecycleOwner 参数
    ) {

        if (todoItem.endTime == todoItem.remindMode.notifyDateTime && todoItem.endTime != "") {
            val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
            viewModel.delTodo(DelPushWrapper(listOf(todoItem.todoId), syncTime, 1))
            val currentList = todoAllAdapter.currentList.toMutableList()
            currentList.remove(todoItem)
            todoAllAdapter.submitList(currentList)
        } else if (todoItem.endTime == "" && todoItem.remindMode.repeatMode != 0) {
            var currentSystemTime=LocalDateTime.now()
            if (todoItem.remindMode.notifyDateTime!=""){
                 currentSystemTime =
                    todoItem.remindMode.notifyDateTime?.let { parseDateTime(it) } ?: LocalDateTime.now()
            }

            lifecycleOwner.lifecycleScope.launch {
                val nextRemindTime = withContext(Dispatchers.Default) {
                    when (todoItem.remindMode.repeatMode) {
                        1 -> NocalculateNextDailyRemindTime(currentSystemTime)
                        2 -> NocalculateNextWeeklyRemindTime(
                            currentSystemTime,
                            todoItem.remindMode.week
                        )

                        3 -> NocalculateNextMonthlyRemindTime(
                            currentSystemTime,
                            todoItem.remindMode.day
                        )

                        else -> currentSystemTime
                    }
                }
                handleNextRemindTime(
                    nextRemindTime,
                    todoItem,
                    viewModel,
                    todoAllAdapter,
                    recyclerView
                )
            }
        } else {
            var currentSystemTime=LocalDateTime.now()
            if (todoItem.remindMode.notifyDateTime!=""){
                currentSystemTime =
                    todoItem.remindMode.notifyDateTime?.let { parseDateTime(it) } ?: LocalDateTime.now()
            }
            val endTime = todoItem.endTime?.let { parseDateTime(it) }
            lifecycleOwner.lifecycleScope.launch {
                val nextRemindTime = withContext(Dispatchers.Default) {
                    when (todoItem.remindMode.repeatMode) {
                        0 -> endTime
                        1 -> endTime?.let { calculateNextDailyRemindTime(currentSystemTime, it) }
                        2 -> endTime?.let {
                            calculateNextWeeklyRemindTime(
                                currentSystemTime,
                                todoItem.remindMode.week,
                                it
                            )
                        }

                        3 -> endTime?.let {
                            calculateNextMonthlyRemindTime(
                                currentSystemTime,
                                todoItem.remindMode.day,
                                it
                            )
                        }

                        else -> currentSystemTime
                    }
                }
                handleNextRemindTime(
                    nextRemindTime,
                    todoItem,
                    viewModel,
                    todoAllAdapter,
                    recyclerView
                )
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleNextRemindTime(
        nextRemindTime: LocalDateTime?,
        todoItem: Todo,
        viewModel: TodoViewModel,
        todoAllAdapter: TodoAllAdapter,
        recyclerView: SwipeDeleteRecyclerView
    ) {
        nextRemindTime?.let {
            if (todoItem.remindMode.notifyDateTime == "") {
                todoItem.remindMode.notifyDateTime = formatDateTime(it)
                val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
                viewModel.pushTodo(TodoListPushWrapper(listOf(todoItem), syncTime, 1, 0))
                todoAllAdapter.notifyDataSetChanged()
            } else {
                todoItem.remindMode.notifyDateTime = formatDateTime(it)
                val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
                viewModel.pushTodo(TodoListPushWrapper(listOf(todoItem), syncTime, 1, 0))
                val currentList = todoAllAdapter.currentList.toMutableList()
                currentList.remove(todoItem)
                todoAllAdapter.submitList(currentList) {
                    recyclerView.scrollToPosition(getTopItems(todoAllAdapter))
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextDailyRemindTime(
        currentRemindTime: LocalDateTime,
        endTime: LocalDateTime? = null
    ): LocalDateTime? {
        var nextRemindTime =
            currentRemindTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
        if (endTime != null && nextRemindTime.isAfter(endTime)) {
            return endTime
        }
        return nextRemindTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextWeeklyRemindTime(
        currentRemindTime: LocalDateTime,
        weekDays: List<Int>,
        endTime: LocalDateTime? = null
    ): LocalDateTime? {
        var nextRemindTime = currentRemindTime.withHour(0).withMinute(0).withSecond(0).withNano(0)
        val validWeekDays = weekDays.filter { it in 1..7 }
        if (validWeekDays.isEmpty()) {
            return null
        }
        nextRemindTime = nextRemindTime.plusDays(1)
        while (nextRemindTime.dayOfWeek.value !in validWeekDays) {
            nextRemindTime = nextRemindTime.plusDays(1)
            if (endTime != null && nextRemindTime.isAfter(endTime)) {
                return endTime
            }
        }
        return nextRemindTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextMonthlyRemindTime(
        currentRemindTime: LocalDateTime,
        days: List<Int>,
        endTime: LocalDateTime? = null
    ): LocalDateTime? {
        var nextRemindTime = currentRemindTime
        val sortedDays = days.sorted()
        while (true) {
            var found = false
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
                    break
                }
            }
            if (found && (endTime == null || nextRemindTime.isBefore(endTime))) {
                return nextRemindTime
            }
            nextRemindTime = nextRemindTime.plusMonths(1).withDayOfMonth(sortedDays[0])
            if (endTime != null && nextRemindTime.isAfter(endTime)) {
                return endTime
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun NocalculateNextMonthlyRemindTime(
        currentRemindTime: LocalDateTime,
        days: List<Int>,
     ): LocalDateTime? {
        var nextRemindTime = currentRemindTime
        val sortedDays = days.sorted() // 确保 days 是排序的

        // 循环直到找到下一个合适的提醒时间
        while (true) {
            var found = false

            // 遍历 days 列表中的每一天
            for (day in sortedDays) {
                // 尝试将提醒时间设置为当前月的 day 号
                val possibleRemindTime = nextRemindTime
                    .withDayOfMonth(day)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0)

                // 如果找到的时间比当前时间晚，则返回该时间
                if (possibleRemindTime.isAfter(currentRemindTime)) {
                    nextRemindTime = possibleRemindTime
                    found = true
                    break // 找到合适的提醒时间，跳出循环
                }
            }

            // 如果找到合适的提醒时间，跳出 while 循环
            if (found) {
                return nextRemindTime
            }

            // 如果没有找到，增加一个月并重置为下个月第一个指定的日期
            nextRemindTime = nextRemindTime.plusMonths(1).withDayOfMonth(sortedDays[0])
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun NocalculateNextWeeklyRemindTime(
        currentSystemTime: LocalDateTime?,
        week: ArrayList<Int>
    ): LocalDateTime? {
        var nextRemindTime = currentSystemTime?.withHour(0)?.withMinute(0)?.withSecond(0)?.withNano(0)
        val validWeekDays = week.filter { it in 1..7 }
        if (validWeekDays.isEmpty()) {
            return null
        }
        nextRemindTime = nextRemindTime?.plusDays(1)
        while (nextRemindTime?.dayOfWeek?.value !in validWeekDays) {
            nextRemindTime = nextRemindTime?.plusDays(1)
        }
        return nextRemindTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun NocalculateNextDailyRemindTime(currentSystemTime: LocalDateTime?): LocalDateTime? {
        var nextRemindTime =
            currentSystemTime?.plusDays(1)?.withHour(0)?.withMinute(0)?.withSecond(0)?.withNano(0)
        return nextRemindTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDateTime(dateTimeStr: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeStr, dateTimeFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(dateTimeFormatter)
    }

    fun getTopItems(todoAllAdapter: TodoAllAdapter): Int {
        return todoAllAdapter.currentList.count { it.isPinned == 1 }
    }
}