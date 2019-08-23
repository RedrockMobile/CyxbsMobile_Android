package com.mredrock.cyxbs.qa.pages.quiz.ui.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.NumberPicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.utils.toFormatString
import kotlinx.android.synthetic.main.qa_dialog_quiz_time_pick.*
import java.util.*

/**
 * Created By jay68 on 2018/11/12.
 */
class TimePickDialog(context: Context) : BottomSheetDialog(context) {
    companion object {
        @JvmStatic
        val HOURS_DISPLAY_NAME = Array(24) { String.format("%02d时", it) }

        @JvmStatic
        val MINUTES_DISPLAY_NAME = Array(60) { String.format("%02d分", it) }

        const val MAX_DAY = 30
        //最小消失时间间隔(:小时)
        const val MIN_GAP_HOUR = 1
    }

    private val daysDisplayName: Array<String> by lazy { initDayDisplayName() }

    private val showToday: Boolean
    private val todayHourStartIndex: Int
    private val todayMinuteStartIndex: Int

    var onNextButtonClickListener: (String) -> Unit = {}

    init {
        setContentView(R.layout.qa_dialog_quiz_time_pick)
        tv_quiz_dialog_time_pick_next.setOnClickListener {
            var day = daysDisplayName[tv_quiz_dialog_date_pick.value]
            val hour = HOURS_DISPLAY_NAME[tv_quiz_dialog_hour_pick.value]
            val minute = MINUTES_DISPLAY_NAME[tv_quiz_dialog_minute_pick.value]
            if (day == "今天") {
                day = GregorianCalendar().time.toFormatString("yyyy年MM月dd日")
            }
            onNextButtonClickListener("$day $hour$minute")
        }

        tv_quiz_dialog_time_pick_cancel.setOnClickListener { dismiss() }

        val now = GregorianCalendar()
        val default = GregorianCalendar().apply {
            add(Calendar.HOUR_OF_DAY, MIN_GAP_HOUR)
        }
        //当今天的时间不足MIN_GAP_HOUR时不给出“今天”的候选
        showToday = now.get(Calendar.DAY_OF_MONTH) == default.get(Calendar.DAY_OF_MONTH)

        val defaultHour = String.format("%02d时", default.get(Calendar.HOUR_OF_DAY))
        val defaultMinute = String.format("%02d分", default.get(Calendar.MINUTE))
        todayHourStartIndex = HOURS_DISPLAY_NAME.binarySearch(defaultHour)
        todayMinuteStartIndex = MINUTES_DISPLAY_NAME.binarySearch(defaultMinute)

        initPickerView()
    }

    private fun initDayDisplayName(): Array<String> {
        val now = GregorianCalendar()
        return Array(MAX_DAY) {
            if (it == 0) {
                if (showToday) {
                    "今天"
                } else {
                    now.apply { add(Calendar.DAY_OF_MONTH, 1) }
                            .time
                            .toFormatString("yyyy年MM月dd日")
                }
            } else {
                now.apply { add(Calendar.DAY_OF_MONTH, 1) }
                        .time
                        .toFormatString("yyyy年MM月dd日")
            }
        }
    }

    private fun initPickerView() {
        tv_quiz_dialog_date_pick.apply {
            minValue = 0
            maxValue = daysDisplayName.size - 1
            displayedValues = daysDisplayName
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            initDividerColor()
        }
        tv_quiz_dialog_hour_pick.apply {
            minValue = 0
            maxValue = HOURS_DISPLAY_NAME.size - 1
            displayedValues = HOURS_DISPLAY_NAME
            value = todayHourStartIndex
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            initDividerColor()
        }
        tv_quiz_dialog_minute_pick.apply {
            minValue = 0
            maxValue = MINUTES_DISPLAY_NAME.size - 1
            displayedValues = MINUTES_DISPLAY_NAME
            value = todayMinuteStartIndex
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            initDividerColor()
        }
    }

    private fun NumberPicker.initDividerColor() {
        val clazz = NumberPicker::class.java
        clazz.getDeclaredField("mSelectionDivider").apply {
            isAccessible = true
            set(this@initDividerColor, ColorDrawable(Color.TRANSPARENT))
        }
    }
}