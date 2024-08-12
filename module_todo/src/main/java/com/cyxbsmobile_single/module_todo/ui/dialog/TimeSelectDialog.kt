package com.cyxbsmobile_single.module_todo.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.aigestudio.wheelpicker.WheelPicker
import com.cyxbsmobile_single.module_todo.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Calendar

/**
 * description: 选择时间的Dialog
 * author: sanhuzhen
 * date: 2024/8/12 19:48
 */
class TimeSelectDialog(context: Context, val onTimeSelected: (Int, Int) -> Unit) :
    BottomSheetDialog(context, com.mredrock.cyxbs.common.R.style.BottomSheetDialogTheme) {


    private val tvCancel by lazy { findViewById<TextView>(R.id.todo_tv_cancel_timeselector) }
    private val tvConfirm by lazy { findViewById<TextView>(R.id.todo_tv_confirm_timeselector) }
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
        tvCancel?.setOnClickListener {
            dismiss()
        }

        tvConfirm?.setOnClickListener {
            val selectedHour = hourWheelPicker?.currentItemPosition ?: 0
            val selectedMinute = minuteWheelPicker?.currentItemPosition ?: 0
            onTimeSelected(selectedHour, selectedMinute)
            dismiss()
        }
    }

    private fun setupWheelPickers() {
        // 设置小时选择器的值范围 0-23
        hourWheelPicker?.data = (0..23).toList()

        // 设置分钟选择器的值范围 0-59
        minuteWheelPicker?.data = (0..59).toList()

        // 设置默认值为当前时间
        val calendar = Calendar.getInstance()
        hourWheelPicker?.setSelectedItemPosition(calendar.get(Calendar.HOUR_OF_DAY))
        minuteWheelPicker?.setSelectedItemPosition(calendar.get(Calendar.MINUTE))
    }

}