package com.cyxbsmobile_single.module_todo.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.RepeatTimeAdapter
import com.cyxbsmobile_single.module_todo.model.bean.DateBeen
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog.CurOperate.*
import com.cyxbsmobile_single.module_todo.util.getThisYearDateSting
import com.cyxbsmobile_single.module_todo.util.hideKeyboard
import com.cyxbsmobile_single.module_todo.util.remindMode2RemindList
import com.cyxbsmobile_single.module_todo.util.weekStringList
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.super_rabbit.wheel_picker.OnValueChangeListener
import com.super_rabbit.wheel_picker.WheelAdapter
import com.super_rabbit.wheel_picker.WheelPicker
import kotlinx.android.synthetic.main.todo_inner_add_thing_dialog.*
import kotlinx.android.synthetic.main.todo_inner_add_thing_dialog.view.*
import java.util.*
import java.util.regex.Pattern

/**
 * @date 2021-08-18
 * @author Sca RayleighZ
 * 增加todo用的Dialog
 * 写出如此长的代码我很抱歉
 */
class AddItemDialog(context: Context, onConfirm: (Todo) -> Unit) :
    RedRockBottomSheetDialog(context) {

    enum class CurOperate {
        REPEAT,
        NOTIFY,
        NONE
    }

    private var curOperate: CurOperate = NONE
    private val repeatTimeAdapter by lazy {
        RepeatTimeAdapter(arrayListOf()) {
            when (todo.remindMode.repeatMode) {
                RemindMode.YEAR -> todo.remindMode.date.removeAt(it)
                RemindMode.WEEK -> todo.remindMode.week.removeAt(it)
                RemindMode.MONTH -> todo.remindMode.day.removeAt(it)
            }
        }
    }

    private var isFromDetail = false

    private val todo by lazy { Todo.generateEmptyTodo() }

    init {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.todo_inner_add_thing_dialog, null, false)
        setContentView(dialogView)

        dialogView?.apply {
            todo_tv_set_notify_time.setOnClickListener {
                hideKeyboard(context, this)
                showNotifyDatePicker()
                todo_tv_set_repeat_time.isClickable = false
            }

            todo_inner_add_thing_cancel.setOnClickListener {
                hide()
            }

            todo_inner_add_rv_thing_repeat_list.adapter = repeatTimeAdapter
            todo_inner_add_rv_thing_repeat_list.layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }

            todo_iv_add_repeat.setOnClickListener {
                if (curOperate == REPEAT) {
                    addRepeat()
                }
            }

            todo_tv_set_repeat_time.setOnClickListener {
                hideKeyboard(context, this)
                showRepeatModePicker()
                todo_tv_set_notify_time.isClickable = false
            }

            todo_inner_add_thing_repeat_time_cancle.setOnClickListener {
                if (isFromDetail) {
                    hide()
                    return@setOnClickListener
                }
                todo_tv_set_notify_time.isClickable = true
                todo_tv_set_repeat_time.isClickable = true
                //制空remindMode
                todo.remindMode = RemindMode.generateDefaultRemindMode()
                curOperate = NONE
            }

            todo_inner_add_thing_repeat_time_confirm.setOnClickListener {
                if (curOperate == NOTIFY) {
                    updateNotifyTime()
                }
                if (isFromDetail) {
                    onConfirm(todo)
                    hide()
                    return@setOnClickListener
                }
                hideWheel()
            }

            todo_thing_add_thing_save.setOnClickListener {
                todo.title = todo_et_todo_title.text.toString()
                if (todo.title == "") {
                    BaseApp.context.toast("标题不可为空哦")
                    return@setOnClickListener
                }
                onConfirm(todo)
                hide()
            }
        }
    }

    private fun addRepeat() {
        val repeatString = when (todo_inner_add_thing_first.getCurrentItem()) {
            "每周" -> {
                todo.remindMode.apply {
                    repeatMode = RemindMode.WEEK
                    week.add(
                        0,
                        weekStringList.indexOf(
                            todo_inner_add_thing_second.getCurrentItem()
                                .subSequence(1, 2)
                        ) + 1
                    )
                }
                todo_inner_add_thing_second.getCurrentItem()
            }
            "每月" -> {
                todo.remindMode.apply {
                    repeatMode = RemindMode.MONTH
                    day.add(0, Integer.parseInt(todo_inner_add_thing_second.getCurrentItem()))
                }
                "每月${todo_inner_add_thing_second.getCurrentItem()}日"
            }
            "每年" -> {
                todo.remindMode.apply {
                    repeatMode = RemindMode.YEAR
                    date.add(
                        0,
                        "${todo_inner_add_thing_second.getCurrentItem()}.${todo_inner_add_thing_third.getCurrentItem()}"
                    )
                }
                "每年${todo_inner_add_thing_second.getCurrentItem()}月${todo_inner_add_thing_third.getCurrentItem()}日"
            }

            "每天" -> {
                todo.remindMode.repeatMode = RemindMode.DAY
                ""
            }

            else -> {
                todo.remindMode.repeatMode = RemindMode.NONE
                ""
            }
        }
        if (repeatString != "") {
            repeatTimeAdapter.addString(repeatString)
            todo_inner_add_rv_thing_repeat_list.scrollToPosition(0)
        }
    }

    private fun hideWheel() {
        todo_iv_add_repeat.visibility = View.GONE
        todo_iv_inner_add_thing_repeat_time_index.visibility = View.GONE
        todo_inner_add_thing_first.visibility = View.GONE
        todo_inner_add_thing_second.visibility = View.GONE
        todo_inner_add_thing_third.visibility = View.GONE
        todo_inner_add_thing_repeat_time_cancle.visibility = View.GONE
        todo_inner_add_thing_repeat_time_confirm.visibility = View.GONE
        curOperate = NONE
        todo_tv_set_notify_time.isClickable = true
        todo_tv_set_repeat_time.isClickable = true
    }

    private fun updateNotifyTime() {
        if (curOperate == NOTIFY) {
            val curDateAndWeek = todo_inner_add_thing_first.getCurrentItem()
            val dateWithoutWeek = if (curDateAndWeek == "今天") "${
                Calendar.getInstance().get(Calendar.MONTH) + 1
            }月${
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            }日" else curDateAndWeek.subSequence(
                0,
                curDateAndWeek.length - 3
            )
            todo.remindMode.notifyDateTime =
                "${
                    Calendar.getInstance().get(Calendar.YEAR)
                }年${dateWithoutWeek}${todo_inner_add_thing_second.getCurrentItem()}:${todo_inner_add_thing_third.getCurrentItem()}"
            val date =
                "$dateWithoutWeek ${todo_inner_add_thing_second.getCurrentItem()}:${todo_inner_add_thing_third.getCurrentItem()}"
            todo_tv_set_notify_time.text = date
        }
    }

    fun showNotifyDatePicker() {
        curOperate = NOTIFY
        todo_iv_add_repeat.visibility = View.GONE
        todo_iv_inner_add_thing_repeat_time_index.visibility = View.VISIBLE
        todo_inner_add_thing_first.visibility = View.VISIBLE
        todo_inner_add_thing_second.visibility = View.VISIBLE
        todo_inner_add_thing_third.visibility = View.VISIBLE
        todo_inner_add_thing_repeat_time_cancle.visibility = View.VISIBLE
        todo_inner_add_thing_repeat_time_confirm.visibility = View.VISIBLE
        val dateBeenList = getThisYearDateSting()
        //配置第一个日期时间选择器
        initWheelPicker(todo_inner_add_thing_first, DateAdapter(dateBeenList))
        //配置第二个小时时间选择器
        initWheelPicker(todo_inner_add_thing_second, SubOneNumberAdapter(24))
        //配置第三个分钟时间选择器
        initWheelPicker(todo_inner_add_thing_third, SubOneNumberAdapter(60))
    }

    fun resetNotifyTime(todo: Todo) {
        this.todo.remindMode = todo.remindMode
        isFromDetail = true
    }

    fun resetAllRepeatMode(todo: Todo) {
        repeatTimeAdapter.resetAll(remindMode2RemindList(todo.remindMode))
        this.todo.remindMode = todo.remindMode
        isFromDetail = true
    }

    //设置为只有单个选择（重复或者提醒时间）
    fun setAsSinglePicker() {
        todo_inner_add_thing_header.visibility = View.GONE
        todo_et_todo_title.visibility = View.GONE
        todo_iv_add_bell.visibility = View.GONE
        todo_ll_notify_time.visibility = View.GONE
        todo_tv_set_repeat_time.setTextColor(Color.parseColor("#15315b"))
    }

    fun showRepeatModePicker() {
        curOperate = REPEAT
        todo_iv_add_repeat.visibility = View.VISIBLE
        todo_iv_inner_add_thing_repeat_time_index.visibility = View.VISIBLE
        todo_inner_add_thing_repeat_time_cancle.visibility = View.VISIBLE
        todo_inner_add_thing_repeat_time_confirm.visibility = View.VISIBLE
        initWheelPicker(todo_inner_add_thing_first, RepeatTypeAdapter())
        todo_inner_add_thing_first.visibility = View.VISIBLE
        todo_inner_add_thing_third.apply { visibility = View.GONE; setAdapter(null) }
        todo_inner_add_thing_second.apply { visibility = View.GONE; setAdapter(null) }
        todo_inner_add_thing_first.setWheelItemCount(5)
        todo_inner_add_thing_first.setOnValueChangeListener(
            object : OnValueChangeListener {
                override fun onValueChange(
                    picker: WheelPicker,
                    oldVal: String,
                    newVal: String
                ) {
                    repeatTimeAdapter.removeAll()
                    val remindDate = todo.remindMode.notifyDateTime
                    todo.remindMode = RemindMode.generateDefaultRemindMode()
                    todo.remindMode.notifyDateTime = remindDate
                    when (newVal) {
                        "每天" -> {
                            todo_inner_add_thing_second.apply {
                                visibility = View.GONE
                                setAdapter(null)
                            }
                        }

                        "每周" -> {
                            initWheelPicker(todo_inner_add_thing_second, WeekAdapter())
                            todo_inner_add_thing_second.visibility = View.VISIBLE
                            todo_inner_add_thing_third.apply {
                                visibility = View.GONE
                                setAdapter(null)
                            }
                        }

                        "每月" -> {
                            initWheelPicker(todo_inner_add_thing_second, NumAdapter(31))
                            todo_inner_add_thing_second.visibility = View.VISIBLE
                            todo_inner_add_thing_third.apply {
                                visibility = View.GONE
                                setAdapter(null)
                            }
                        }

                        "每年" -> {
                            initWheelPicker(todo_inner_add_thing_second, NumAdapter(12))
                            todo_inner_add_thing_second.visibility = View.VISIBLE
                            todo_inner_add_thing_third.visibility = View.VISIBLE
                            //先加载一下(因为pos不会清零)
                            val pattern: Pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*")
                            if (pattern.matcher(todo_inner_add_thing_second.getCurrentItem())
                                    .matches()
                            ) {
                                val month =
                                    Integer.parseInt(todo_inner_add_thing_second.getCurrentItem())
                                val calendar = Calendar.getInstance()
                                LogUtils.d("RayleighZ", "$month")
                                calendar.set(Calendar.MONTH, month - 1)
                                initWheelPicker(
                                    todo_inner_add_thing_third,
                                    NumAdapter(calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                                )
                            }
                            todo_inner_add_thing_second.setOnValueChangeListener(
                                object : OnValueChangeListener {
                                    override fun onValueChange(
                                        picker: WheelPicker,
                                        oldVal: String,
                                        newVal: String
                                    ) {
                                        if (pattern.matcher(newVal).matches()) {
                                            val innerMonth = Integer.parseInt(newVal)
                                            val innerCalendar = Calendar.getInstance()
                                            innerCalendar.set(Calendar.MONTH, innerMonth - 1)
                                            initWheelPicker(
                                                todo_inner_add_thing_third,
                                                NumAdapter(innerCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                                            )
                                        }
                                    }
                                }
                            )
                        }

                        else -> {
                            if (curOperate == REPEAT) {
                                todo_inner_add_thing_second.apply {
                                    visibility = View.GONE; setAdapter(null)
                                }
                                todo_inner_add_thing_third.apply {
                                    visibility = View.GONE; setAdapter(null)
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    abstract class NormalWheelAdapter(private val size: Int) : WheelAdapter {
        override fun getMaxIndex(): Int = size - 1

        override fun getMinIndex(): Int = 0

        override fun getPosition(vale: String): Int = 0

        override fun getValue(position: Int): String {
            if (position < 0) {
                return getPositivePosition((size + position) % size)
            }
            return getPositivePosition(position % size)
        }

        abstract fun getPositivePosition(position: Int): String
    }

    private fun initWheelPicker(wheelPicker: WheelPicker, adapter: WheelAdapter) {
        wheelPicker.setAdapter(adapter)
        wheelPicker.setSelectorRoundedWrapPreferred(true)
        handleTouchConflict(wheelPicker)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun handleTouchConflict(view: View) {
        //解决与bottomSheet的滑动冲突
        view.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
    }

    //日期（带星期数）的适配器
    class DateAdapter(private val dateBeenList: List<DateBeen>) :
        NormalWheelAdapter(dateBeenList.size) {
        override fun getTextWithMaximumLength(): String = "12月31日 周天"

        override fun getValue(position: Int): String {
            if (position < 0) {
                return ""
            }
            return getPositivePosition(position % dateBeenList.size)
        }

        override fun getPositivePosition(position: Int): String {
            if (position == 0) {
                return "今天"
            }
            val curDateBeen = dateBeenList[(position)]
            return "${curDateBeen.month}月${curDateBeen.day}日 周${weekStringList[curDateBeen.week - 1]}"
        }
    }

    //数字适配器
    class NumAdapter(size: Int) : NormalWheelAdapter(size) {
        override fun getTextWithMaximumLength(): String = "24"
        override fun getPositivePosition(position: Int): String = "${position + 1}"
    }

    //选择重复类型的适配器
    class RepeatTypeAdapter : NormalWheelAdapter(6) {
        private val dataList = listOf(
            "每天",
            "每周",
            "每月",
            "每年"
        )

        override fun getTextWithMaximumLength(): String = "每年"

        override fun getValue(position: Int): String = when (position) {
            in 0..3 -> {
                getPositivePosition(position % 4)
            }
            else -> {
                ""
            }
        }

        override fun getPositivePosition(position: Int): String = dataList[position]
    }

    class SubOneNumberAdapter(size: Int) : NormalWheelAdapter(size) {
        override fun getTextWithMaximumLength(): String = "24"
        override fun getPositivePosition(position: Int): String = "$position"
    }

    //星期
    class WeekAdapter : NormalWheelAdapter(7) {
        override fun getTextWithMaximumLength(): String = "周天"
        override fun getPositivePosition(position: Int): String = "周" + weekStringList[position]
    }
}