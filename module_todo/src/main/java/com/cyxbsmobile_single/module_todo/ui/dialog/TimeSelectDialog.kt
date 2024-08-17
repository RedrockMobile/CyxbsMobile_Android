package com.cyxbsmobile_single.module_todo.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.aigestudio.wheelpicker.WheelPicker
import com.cyxbsmobile_single.module_todo.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Calendar

/**
 * description: 选择时间的Dialog
 * author: sanhuzhen
 * date: 2024/8/12 19:48
 */
class TimeSelectDialog(
    context: Context,
    val selectedYear: Int,
    val selectedMonth: Int,
    val selectedDay: Int,
    val onTimeSelected: (Int, Int) -> Unit
): BottomSheetDialog(context, R.style.BottomSheetDialogTheme) {


    private val btCancel by lazy { findViewById<AppCompatButton>(R.id.todo_bt_cancel_timeselector) }
    private val btConfirm by lazy { findViewById<AppCompatButton>(R.id.todo_bt_confirm_timeselector) }
    private val hourWheelPicker by lazy { findViewById<WheelPicker>(R.id.todo_wheelpicker_hour_timeselector) }
    private val minuteWheelPicker by lazy { findViewById<WheelPicker>(R.id.todo_wheelpicker_minute_timeselector) }

    init {
        val dialogView =
            LayoutInflater.from(context)
                .inflate(R.layout.todo_dialog_bottom_sheet_timeselector, null, false)
        setContentView(dialogView)

        dialogView?.apply {
            setupWheelPickers()
            initClick()
        }
    }

    private fun initClick() {
        btCancel?.setOnClickListener {
            dismiss()
        }

        btConfirm?.setOnClickListener {
            val selectedHour = hourWheelPicker?.data?.get(hourWheelPicker?.currentItemPosition ?: 0) as Int
            val selectedMinute = minuteWheelPicker?.data?.get(minuteWheelPicker?.currentItemPosition ?: 0) as Int
            onTimeSelected(selectedHour, selectedMinute)
            dismiss()
        }
    }

    private fun setupWheelPickers() {
        val calendar = Calendar.getInstance()
        var startTimeHour = 0
        var startTimeMinute = 0

        if (isToday(selectedYear, selectedMonth, selectedDay)) {
            startTimeHour = calendar.get(Calendar.HOUR_OF_DAY)
            startTimeMinute = calendar.get(Calendar.MINUTE)
        }

        // 设置小时选择器的值范围
        hourWheelPicker?.data = (startTimeHour..23).toList()
        // 设置分钟选择器的值范围
        minuteWheelPicker?.data = (startTimeMinute..59).toList()

        // 如果时间选择的范围太小，取消滚动
        hourWheelPicker?.isCyclic = (hourWheelPicker?.data?.size ?: 0) > 7
        minuteWheelPicker?.isCyclic = (minuteWheelPicker?.data?.size ?: 0) > 7

        // 设置默认值为当天时间或初始值
        hourWheelPicker?.setSelectedItemPosition(0)
        minuteWheelPicker?.setSelectedItemPosition(0)
    }

    // 判断是否是今天
    private fun isToday(year: Int, month: Int, day: Int): Boolean {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR) == year &&
                calendar.get(Calendar.MONTH) == month &&
                calendar.get(Calendar.DAY_OF_MONTH) == day
    }

}