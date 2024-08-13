/**
 * @Project: CyxbsMobile_Android
 * @File: TodoData
 * @Author: 86199
 * @Date: 2024/8/12
 * @Description: todo的数据类
 */
data class TodoData(
    val status: Int,
    val info: String,
    val data: TodoDataDetails
)

data class TodoDataDetails(
    val changed_todo_array: List<TodoItem>,
    val sync_time: Long
)

data class TodoItem(
    val todo_id: Int,
    val title: String,
    val remind_mode: RemindMode,
    val detail: String,
    val last_modify_time: Long,
    val is_done: Int,
    val type: String
)

data class RemindMode(
    val repeat_mode: Int,
    val notify_datetime: String,
    val date: List<String>,  // 这里的类型根据实际情况调整。如果有明确的格式可以考虑使用其他类型。
    val week: List<Int>,
    val day: List<Int>
)
