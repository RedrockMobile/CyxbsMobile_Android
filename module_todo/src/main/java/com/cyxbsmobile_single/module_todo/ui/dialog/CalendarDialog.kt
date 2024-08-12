package com.cyxbsmobile_single.module_todo.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.util.weekStringList
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Calendar

/**
 * description: 选择日期的Dialog
 * author: sanhuzhen
 * date: 2024/8/11 22:45
 */
class CalendarDialog(context: Context, val onCalendarSelected: (Int, Int, Int) -> Unit) :
    BottomSheetDialog(context, com.mredrock.cyxbs.common.R.style.BottomSheetDialogTheme) {


    //设置一个选中状态的TextView，用于将背景销毁
    private var selectedDayView: TextView? = null

    private val calendar = Calendar.getInstance()
    private val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    private val currentMonth = calendar.get(Calendar.MONTH)
    private val currentYear = calendar.get(Calendar.YEAR)


    private val tvCalendarHeader by lazy { findViewById<TextView>(R.id.todo_tv_header_calendar) }
    private val ivPreMonth by lazy { findViewById<ImageView>(R.id.todo_iv_pre_month) }
    private val ivNextMonth by lazy { findViewById<ImageView>(R.id.todo_iv_next_month) }
    private val glWeekCalendar by lazy { findViewById<GridLayout>(R.id.todo_gl_week) }
    private val glCalendar by lazy { findViewById<GridLayout>(R.id.todo_gl_calendar) }
    private val rlCalendar by lazy { findViewById<RelativeLayout>(R.id.todo_rl_calendar) }
    private val btnConfirm by lazy { findViewById<AppCompatButton>(R.id.todo_btn_confirm_calendar) }
    private val btnCancel by lazy { findViewById<AppCompatButton>(R.id.todo_btn_cancel_calendar) }
    private val tvSelectTime by lazy { findViewById<TextView>(R.id.todo_tv_time_calendar) }

    init {
        val dialogView =
            LayoutInflater.from(context)
                .inflate(R.layout.todo_dialog_bottom_sheet_calendar, null, false)
        setContentView(dialogView)

        dialogView?.apply {
            //设置日历
            setCalendar()
            initClick()
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun initClick() {
        ivPreMonth?.setOnClickListener {
            changeMonth(-1)
        }
        ivNextMonth?.setOnClickListener {
            changeMonth(1)
        }

        rlCalendar?.setOnClickListener {
            TimeSelectDialog(context) { hour, minute ->
                tvSelectTime?.text = "${
                    String.format(
                        "%02d",
                        hour
                    )
                }:${
                    String.format(
                        "%02d",
                        minute
                    )
                }"
            }.show()
        }

        btnConfirm?.setOnClickListener {
            //回传选择的日期
            onCalendarSelected(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dismiss()
        }
        btnCancel?.setOnClickListener {
            dismiss()
        }
    }

    /**
     * 设置日历
     */
    private fun setCalendar() {
        setWeekOfCalendar()
        setDayOfCalendar()
    }

    private fun setWeekOfCalendar() {
        //填充星期
        for (i in 0..6) {
            val weekView = TextView(context).apply {
                textSize = 16f
                gravity = Gravity.CENTER
                setTextColor(Color.GRAY)
                text = weekStringList[i]
            }
            glWeekCalendar?.addView(weekView, createGridLayoutParam())
        }
    }

    private fun setDayOfCalendar() {
        glCalendar?.removeAllViews()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 //获取当前月份第一天是星期几

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) //获取当前月份的天数

        //填充之前的空白天数
        for (i in 1 until firstDayOfWeek) {
            val emptyView = TextView(context)
            glCalendar?.addView(emptyView, createGridLayoutParam())
        }

        //填充当前月份的天数
        for (day in 1..daysInMonth) {
            val dayView = createDayTextView(day)
            dayView.setOnClickListener {
                if (calendar.get(Calendar.YEAR) > currentYear || (calendar.get(Calendar.YEAR) == currentYear && calendar.get(
                        Calendar.MONTH
                    ) > currentMonth) || (calendar.get(Calendar.YEAR) == currentYear && calendar.get(
                        Calendar.MONTH
                    ) == currentMonth && day >= currentDay)
                ) {
                    //用户点击日期时触发
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    selectedDayView?.background = null
                    dayView.background =
                        ContextCompat.getDrawable(context, R.drawable.todo_shape_bg_day_select) //根据需要设置背景
                    selectedDayView = dayView
                }
            }
            glCalendar?.addView(dayView, createGridLayoutParam())
        }
    }

    //创建日期TextView
    @SuppressLint("ResourceAsColor")
    private fun createDayTextView(day: Int): TextView {
        return TextView(context).apply {
            text = day.toString()
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)
            if (calendar.get(Calendar.YEAR) > currentYear) {
                setTextColor(Color.BLACK)
            } else if (calendar.get(Calendar.YEAR) == currentYear) {
                if (calendar.get(Calendar.MONTH) < currentMonth) {
                    setTextColor(Color.GRAY)
                } else if (calendar.get(Calendar.MONTH) == currentMonth) {
                    if (day == currentDay) {
                        setTextColor(R.color.todo_inner_add_save_text_color)
                        if (selectedDayView == null) {
                            selectedDayView = this
                            background =
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.todo_shape_bg_day_select
                                ) //根据需要设置背景
                        }
                    } else if (day > currentDay) {
                        setTextColor(Color.BLACK)
                    } else {
                        setTextColor(Color.GRAY)
                    }
                } else {
                    setTextColor(Color.BLACK)
                }
            } else {
                setTextColor(Color.GRAY)
            }
        }
    }

    //创建GridLayout的布局参数
    private fun createGridLayoutParam(): GridLayout.LayoutParams {
        return GridLayout.LayoutParams().apply {
            width = 0
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL)
            setMargins(8, 8, 8, 8)
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun changeMonth(amount: Int) {
        calendar.add(Calendar.MONTH, amount)
        setDayOfCalendar() //刷新日历${calendar.get(Calendar.MONTH) + 1}月
        tvCalendarHeader?.text = "${calendar.get(Calendar.YEAR)}/${
            String.format(
                "%02d",
                calendar.get(Calendar.MONTH) + 1
            )
        }"
    }
}