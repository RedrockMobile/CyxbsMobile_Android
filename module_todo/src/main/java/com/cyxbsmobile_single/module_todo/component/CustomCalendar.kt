package com.cyxbsmobile_single.module_todo.component

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import java.util.*

class CustomCalendar(context: Context) : GridLayout(context) {

    private var calendar: Calendar = Calendar.getInstance()
    private var selectedDayView: TextView? = null

    init {
        rowCount = 7  // 1 for week days and 6 for dates
        columnCount = 7
        initUI()
    }

    private fun initUI() {
        // 添加星期标题
        addWeekDays()
        // 添加日期
        updateCalendar()
    }

    // 添加星期标题
    private fun addWeekDays() {
        val weekDays = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        for (day in weekDays) {
            val textView = TextView(context)
            textView.text = day
            textView.gravity = Gravity.CENTER
            addView(textView)
        }
    }

    // 更新日历视图
    private fun updateCalendar() {
        removeViews(7, childCount - 7) // 保留第一行星期标题，移除之前的日期
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val today = Calendar.getInstance()

        // 添加日期
        for (i in 0 until firstDayOfWeek) {
            addEmptyView()
        }

        for (day in 1..daysInMonth) {
            val textView = TextView(context)
            textView.gravity = Gravity.CENTER
            textView.text = day.toString()

            val isBeforeToday = calendar.before(today)
            val isToday = day == today.get(Calendar.DAY_OF_MONTH) &&
                    calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)

            // 设置日期颜色
            when {
                isBeforeToday -> {
                    textView.setTextColor(Color.GRAY)
                    textView.isEnabled = false
                }
                isToday -> {
                    textView.setTextColor(Color.BLUE)
                    selectDate(textView)
                }
                else -> {
                    textView.setTextColor(Color.BLACK)
                }
            }

            textView.setOnClickListener {
                if (textView.isEnabled) {
                    selectDate(textView)
                }
            }

            addView(textView)
        }
    }

    // 添加空白视图
    private fun addEmptyView() {
        val emptyView = View(context)
        addView(emptyView)
    }

    // 选中日期
    private fun selectDate(selectedView: TextView) {
        selectedDayView?.setBackgroundColor(Color.TRANSPARENT) // 清除之前选中的效果
        selectedDayView?.setTextColor(Color.BLACK) // 恢复原始字体颜色

        selectedView.setBackgroundColor(Color.BLUE) // 设置选中背景为蓝色
        selectedView.setTextColor(Color.WHITE) // 设置选中文字颜色为白色
        selectedDayView = selectedView
    }

    // 切换月份
    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        updateCalendar()
    }

    fun previousMonth() {
        calendar.add(Calendar.MONTH, -1)
        updateCalendar()
    }
}