package com.mredrock.cyxbs.todo.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.todo.R
import com.mredrock.cyxbs.todo.util.getColor
import com.mredrock.cyxbs.todo.util.weekStringList
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.visible
import java.util.Calendar

/**
 * description: 选择日期的Dialog
 * author: sanhuzhen
 * date: 2024/8/11 22:45
 */
class CalendarDialog(
    context: Context,
    style: Int,
    private val lineVisibility: Int,
    val onCalendarSelected: (Int, Int, Int, Int, Int) -> Unit
) :
    BottomSheetDialog(context, style) {


    //设置一个选中状态的TextView，用于将背景销毁
    private var selectedDayView: TextView? = null

    //选择的时间
    private var selectHour = 24
    private var selectMinute = 60
    private val calendar = Calendar.getInstance()
    private val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    private val currentMonth = calendar.get(Calendar.MONTH)
    private val currentYear = calendar.get(Calendar.YEAR)


    private lateinit var tvCalendarHeader: TextView
    private lateinit var ivPreMonth: ImageView
    private lateinit var ivNextMonth: ImageView
    private lateinit var glWeekCalendar: GridLayout
    private lateinit var glCalendar: GridLayout
    private lateinit var rlCalendar: RelativeLayout
    private lateinit var btnConfirm: AppCompatTextView
    private lateinit var btnCancel: AppCompatTextView
    private lateinit var tvSelectTime: TextView
    private lateinit var line: View
    private lateinit var ivTime: ImageView

    init {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.todo_dialog_bottom_sheet_calendar, null, false)
        setContentView(dialogView)

        initView(this)
        dialogView?.apply {
            //设置日历
            setCalendar()
            if (lineVisibility == 1) line.visible()
            else line.gone()
            initClick()
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun initClick() {
        tvCalendarHeader.text =
            "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月"

        ivPreMonth.setOnClickListener {
            changeMonth(-1)
        }
        ivNextMonth.setOnClickListener {
            changeMonth(1)
        }

        rlCalendar.setOnClickListener {

            //传递选中的时间，判断是不是当前这一天
            TimeSelectDialog(
                context,
                R.style.BottomSheetDialogTheme,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                lineVisibility
            ) { hour, minute ->
                selectHour = hour
                selectMinute = minute
                tvSelectTime.text = "${
                    String.format(
                        "%02d", selectHour
                    )
                }:${
                    String.format(
                        "%02d", selectMinute
                    )
                }"
                ivTime.setImageResource(R.drawable.todo_ic_addtodo_selecttime2)
            }.show()
        }

        btnConfirm.setOnClickListener {
            //回传选择的日期
            onCalendarSelected(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                selectHour,
                selectMinute
            )
            dismiss()
        }
        btnCancel.setOnClickListener {
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
                textSize = 12f
                gravity = Gravity.CENTER
                setTextColor(getColor(R.color.todo_calendar_week_text_color))
                text = weekStringList[i]
            }
            glWeekCalendar.addView(weekView, createGridLayoutParam())
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setDayOfCalendar() {
        glCalendar.removeAllViews()


        calendar.set(Calendar.DAY_OF_MONTH, 1)
        var firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 //获取当前月份第一天是星期几
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            firstDayOfWeek = 7
        }


        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) //获取当前月份的天数


        //填充之前的空白天数
        for (i in 1 until firstDayOfWeek) {
            val emptyView = TextView(context)
            glCalendar.addView(emptyView, createGridLayoutParam())
        }

        //填充当前月份的天数
        for (day in 1..daysInMonth) {
            val dayView = createDayTextView(day)
            selectDay(dayView, day)
            glCalendar.addView(dayView, createGridLayoutParam())
        }

    }

    private fun selectDay(dayView: TextView, day: Int) {
        val isCurrentYear = calendar.get(Calendar.YEAR) == currentYear
        val isAfterCurrentYear = calendar.get(Calendar.YEAR) > currentYear
        val isCurrentMonth = calendar.get(Calendar.MONTH) == currentMonth
        val afterCurrentMonth = calendar.get(Calendar.MONTH) > currentMonth
        val isFirstDay = day == 1

        // 检查是否选中
        val isSelected = ((isCurrentYear && isCurrentMonth && day == currentDay) ||
                (isAfterCurrentYear && isFirstDay) ||
                (isCurrentYear && afterCurrentMonth && isFirstDay))

        if (isSelected) {
            calendar.set(Calendar.DAY_OF_MONTH, day)
            applySelectedStyle(dayView)
        }

        dayView.setOnClickListener {
            if (isDateSelectable(day)) {
                calendar.set(Calendar.DAY_OF_MONTH, day)
                deselectCurrentDay()
                applySelectedStyle(dayView)
            }
        }
    }

    private fun applySelectedStyle(dayView: TextView) {
        dayView.apply {
            selectedDayView?.let {
                it.background = null
                it.setTextColor(
                    if (calendar.get(Calendar.YEAR) == currentYear &&
                        calendar.get(Calendar.MONTH) == currentMonth &&
                        it.text.toString().toInt() == currentDay
                    ) getColor(R.color.todo_calendar_current_day_tv_color) else getColor(com.mredrock.cyxbs.config.R.color.config_level_three_font_color)
                )
            }
            selectedDayView = this
            background = ContextCompat.getDrawable(context, R.drawable.todo_shape_bg_day_select)
            setTextColor(Color.WHITE)
        }
    }

    private fun deselectCurrentDay() {
        selectedDayView?.apply {
            background = null
            setTextColor(
                if (calendar.get(Calendar.YEAR) == currentYear &&
                    calendar.get(Calendar.MONTH) == currentMonth &&
                    text.toString().toInt() == currentDay
                ) getColor(R.color.todo_calendar_current_day_tv_color) else getColor(com.mredrock.cyxbs.config.R.color.config_level_three_font_color)
            )
        }
    }

    //判断日期是否可选
    private fun isDateSelectable(day: Int): Boolean {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        return year > currentYear ||
                (year == currentYear && month > currentMonth) ||
                (year == currentYear && month == currentMonth && day >= currentDay)
    }

    //创建日期TextView
    @SuppressLint("ResourceAsColor")
    private fun createDayTextView(day: Int): TextView {
        return TextView(context).apply {
            text = day.toString()
            textSize = 4.6f.spToPx(context)
            gravity = Gravity.CENTER
            setPadding(20, 20, 20, 20)
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayColor = when {
                year > currentYear -> getColor(com.mredrock.cyxbs.config.R.color.config_level_three_font_color)
                year < currentYear -> getColor(R.color.todo_calendar_ago_day_color)
                month < currentMonth -> getColor(R.color.todo_calendar_ago_day_color)
                month > currentMonth -> getColor(com.mredrock.cyxbs.config.R.color.config_level_three_font_color)
                day > currentDay -> getColor(com.mredrock.cyxbs.config.R.color.config_level_three_font_color)
                day < currentDay -> getColor(R.color.todo_calendar_ago_day_color)
                else -> {
                    if (selectedDayView == null) {
                        selectedDayView = this
                        background =
                            ContextCompat.getDrawable(context, R.drawable.todo_shape_bg_day_select)
                        Color.WHITE
                    } else {
                        getColor(R.color.todo_calendar_current_day_tv_color)
                    }

                }
            }

            setTextColor(dayColor)
        }
    }

    // 创建 GridLayout 的布局参数，使单元格等宽高
    private fun createGridLayoutParam(): GridLayout.LayoutParams {
        return GridLayout.LayoutParams().apply {
            width = 0
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(20, 10, 20, 10) // 设置单元格间的间距
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun changeMonth(amount: Int) {
        calendar.add(Calendar.MONTH, amount)
        setDayOfCalendar() //刷新日历${calendar.get(Calendar.MONTH) + 1}月
        tvCalendarHeader.text =
            "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月"
        if (calendar.get(Calendar.MONTH) > currentMonth || calendar.get(Calendar.YEAR) > currentYear){
            ivPreMonth.setImageResource(R.drawable.todo_ic_pre_month_light)
        }else{
            ivPreMonth.setImageResource(R.drawable.todo_ic_pre_month)
        }
    }

    private fun initView(dialog: Dialog) {
        tvCalendarHeader = dialog.findViewById(R.id.todo_tv_header_calendar)
        ivPreMonth = dialog.findViewById(R.id.todo_iv_pre_month)
        ivNextMonth = dialog.findViewById(R.id.todo_iv_next_month)
        glWeekCalendar = dialog.findViewById(R.id.todo_gl_week)
        glCalendar = dialog.findViewById(R.id.todo_gl_calendar)
        rlCalendar = dialog.findViewById(R.id.todo_rl_calendar)
        btnConfirm = dialog.findViewById(R.id.todo_btn_confirm_calendar)
        btnCancel = dialog.findViewById(R.id.todo_btn_cancel_calendar)
        tvSelectTime = dialog.findViewById(R.id.todo_tv_time_calendar)
        line = dialog.findViewById(R.id.todo_view_calendar_line)
        ivTime = dialog.findViewById(R.id.todo_iv_time_select)
    }

    // 扩展函数用于将sp转换为px
    fun Float.spToPx(context: Context): Float {
        return this * context.resources.displayMetrics.scaledDensity
    }
}