package com.cyxbsmobile_single.module_todo.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.BaseWheelAdapter
import com.cyxbsmobile_single.module_todo.adapter.RepeatTimeAdapter
import com.cyxbsmobile_single.module_todo.model.bean.DateBeen
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog.CurOperate.*
import com.cyxbsmobile_single.module_todo.util.*
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
                RemindMode.YEAR -> {
                    todo.remindMode.date.removeAt(it)
                    if (todo.remindMode.date.isEmpty()) {
                        todo.remindMode.repeatMode = RemindMode.NONE
                    }
                }
                RemindMode.WEEK -> {
                    todo.remindMode.week.removeAt(it)
                    if (todo.remindMode.week.isEmpty()) {
                        todo.remindMode.repeatMode = RemindMode.NONE
                    }
                }
                RemindMode.MONTH -> {
                    todo.remindMode.day.removeAt(it)
                    if (todo.remindMode.day.isEmpty()) {
                        todo.remindMode.repeatMode = RemindMode.NONE
                    }
                }
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
            }

            todo_inner_add_thing_repeat_time_cancle.setOnClickListener {
                if (isFromDetail) {
                    hide()
                    return@setOnClickListener
                }
                //制空remindMode
                todo.remindMode = RemindMode.generateDefaultRemindMode()
                curOperate = NONE
                hideWheel()
            }

            todo_inner_add_thing_repeat_time_confirm.setOnClickListener {
                if (curOperate == NOTIFY) {
                    updateNotifyTime()
                }
                if (isFromDetail) {
                    LogUtils.d("RayleighZ", "on Confirm, todo = $todo")
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
                    if (repeatMode != RemindMode.WEEK && repeatMode != RemindMode.NONE) {
                        //不允许添加两种以上的重复提醒
                        BaseApp.context.toast("掌友，只能选择一种重复模式哦！")
                        return
                    }
                    repeatMode = RemindMode.WEEK
                    week.addWithoutRepeat(
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
                    if (repeatMode != RemindMode.MONTH && repeatMode != RemindMode.NONE) {
                        //不允许添加两种以上的重复提醒
                        BaseApp.context.toast("掌友，只能选择一种重复模式哦！")
                        return
                    }
                    repeatMode = RemindMode.MONTH
                    day.addWithoutRepeat(0, Integer.parseInt(todo_inner_add_thing_second.getCurrentItem()))
                }
                "每月${todo_inner_add_thing_second.getCurrentItem()}日"
            }
            "每年" -> {
                todo.remindMode.apply {
                    if (repeatMode != RemindMode.YEAR && repeatMode != RemindMode.NONE) {
                        //不允许添加两种以上的重复提醒
                        BaseApp.context.toast("掌友，只能选择一种重复模式哦！")
                        return
                    }
                    repeatMode = RemindMode.YEAR
                    date.addWithoutRepeat(
                        0,
                        "${todo_inner_add_thing_second.getCurrentItem()}.${todo_inner_add_thing_third.getCurrentItem()}"
                    )
                }
                "每年${todo_inner_add_thing_second.getCurrentItem()}月${todo_inner_add_thing_third.getCurrentItem()}日"
            }

            "每天" -> {
                if (todo.remindMode.repeatMode != RemindMode.DAY && todo.remindMode.repeatMode != RemindMode.NONE) {
                    //不允许添加两种以上的重复提醒
                    BaseApp.context.toast("掌友，只能选择一种重复模式哦！")
                    return
                }
                todo.remindMode.repeatMode = RemindMode.DAY
                "每天"
            }

            else -> {
                if (todo.remindMode.repeatMode != RemindMode.WEEK && todo.remindMode.repeatMode != RemindMode.NONE) {
                    //不允许添加两种以上的重复提醒
                    BaseApp.context.toast("掌友，只能选择一种重复模式哦！")
                    return
                }
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
        todo_inner_add_thing_repeat_time_cancle.visibility = View.GONE
        todo_inner_add_thing_repeat_time_confirm.visibility = View.GONE
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
                }年${dateWithoutWeek}${
                    numToString(todo_inner_add_thing_second.getCurrentItem())
                }:${
                    numToString(todo_inner_add_thing_third.getCurrentItem())
                }"
            val date =
                "$dateWithoutWeek ${numToString(todo_inner_add_thing_second.getCurrentItem())}:${numToString(todo_inner_add_thing_third.getCurrentItem())}"
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
        if (todo.remindMode.repeatMode == RemindMode.NONE){
            repeatTimeAdapter.resetAll(emptyList())
        } else {
            repeatTimeAdapter.resetAll(remindMode2RemindList(todo.remindMode).map {
                //如果你要问这里是为什么
                //视觉图无脑每周一和周一切换我也没办法啊
                it.subSequence(1, it.length).toString()
            })
        }
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
        BaseWheelAdapter(dateBeenList.size) {
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
    class NumAdapter(size: Int) : BaseWheelAdapter(size) {
        override fun getTextWithMaximumLength(): String = "24"
        override fun getPositivePosition(position: Int): String = "${position + 1}"
    }

    //选择重复类型的适配器
    class RepeatTypeAdapter : BaseWheelAdapter(6) {
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

    class SubOneNumberAdapter(size: Int) : BaseWheelAdapter(size) {
        override fun getTextWithMaximumLength(): String = "24"
        override fun getPositivePosition(position: Int): String = "$position"
    }

    //星期
    class WeekAdapter : BaseWheelAdapter(7) {
        override fun getTextWithMaximumLength(): String = "周天"
        override fun getPositivePosition(position: Int): String = "周" + weekStringList[position]
    }
}