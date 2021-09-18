package com.cyxbsmobile_single.module_todo.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.ChooseYearAdapter
import com.cyxbsmobile_single.module_todo.adapter.RepeatTimeAdapter
import com.cyxbsmobile_single.module_todo.model.bean.DateBeen
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.ui.AddItemDialog.CurOperate.*
import com.cyxbsmobile_single.module_todo.util.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.toast
import kotlinx.android.synthetic.main.todo_inner_add_thing_dialog.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Author: RayleighZ
 * Time: 2021-09-18 8:50
 * 旧版本的dialog写的时候脑子不清醒
 * 故重构
 */
class AddItemDialog(context: Context, val onConfirm: (Todo) -> Unit) :
    BottomSheetDialog(context, R.style.BottomSheetDialogTheme) {

    private val dateBeenList: ArrayList<ArrayList<DateBeen>>
            by lazy { getYearDateSting() }

    private val todo by lazy { Todo.generateEmptyTodo() }

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

    //是否来自详情界面
    private var isFromDetail = false

    private var curOperate = NONE

    enum class CurOperate {
        REPEAT,
        NOTIFY,
        NONE
    }

    override fun setContentView(view: View) {
        val viewGroup = LayoutInflater.from(context).inflate(
            R.layout.todo_dialog_bottom_sheet_container,
            window?.decorView as ViewGroup,
            false
        ) as FrameLayout
        viewGroup.addView(view)
        super.setContentView(viewGroup)
    }

    init {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.todo_inner_add_thing_dialog, null, false)
        setContentView(dialogView)

        dialogView?.apply {
            //首先，添加点击事件
            initClick()
        }
    }


    private fun initClick() {
        //设置添加提醒日期的点击事件
        todo_tv_set_notify_time.setOnClickListener {
            if (curOperate == NOTIFY) {
                //如果当前已经在设置提醒日期，就不做操作
                return@setOnClickListener
            }
            //配置提醒日期
            showNotifyDatePicker()
            curOperate = NOTIFY
        }

        //设置重复时间的点击事件
        todo_tv_set_repeat_time.setOnClickListener {
            whenStatusDifDoAndChangeStatus(NOTIFY) { showNotifyDatePicker() }
        }

        todo_tv_set_repeat_time.setOnClickListener {
            whenStatusDifDoAndChangeStatus(REPEAT) { showRepeatDatePicker() }
        }

        //添加的点击事件
        todo_iv_add_repeat.setOnClickListener { addRepeat() }

        //confirm的点击事件
        todo_inner_add_thing_repeat_time_cancel.setOnClickListener { onConfirmClick() }

    }

    //当从detail界面过来复用的时候，提供一个接口来重置todo的提醒模式
    fun resetNotifyTime(todo: Todo) {
        this.todo.remindMode = todo.remindMode
        isFromDetail = true
    }

    private fun onConfirmClick(){
        when(curOperate){
            NOTIFY -> { addNotify() }//判定为在添加提醒
            REPEAT -> {
                if (isFromDetail){
                    onConfirm(todo)
                    hide()
                    return
                }
            }//因为已经添加了提醒，这里就直接隐藏轮子
            else -> { hideWheel() }
        }
        hideWheel()
    }

    fun showNotifyDatePicker() {
        //调整可见性
        todo_iv_add_repeat.visibility = View.GONE
        todo_iv_inner_add_thing_repeat_time_index.visibility = View.VISIBLE
        todo_inner_add_thing_first.visibility = View.VISIBLE
        todo_inner_add_thing_second.visibility = View.VISIBLE
        todo_inner_add_thing_third.visibility = View.VISIBLE
        todo_inner_add_thing_repeat_time_cancel.visibility = View.VISIBLE
        todo_inner_add_thing_repeat_time_confirm.visibility = View.VISIBLE

        //配置年份选择Adapter
        todo_inner_add_rv_thing_repeat_list.adapter = ChooseYearAdapter(
            ArrayList(getNextForYears())
        ) {
            //配置第一个日期时间选择器
            todo_inner_add_thing_first.data = dateBeenList[it].map { dateBeen ->
                "${numToString(dateBeen.month)}月${numToString(dateBeen.day)}日 周${weekStringList[dateBeen.week - 1]}"
            }
        }
        //配置仨时间选择器（默认展示今年）
        todo_inner_add_thing_first.data = dateBeenList[0]
        todo_inner_add_thing_second.data = listOf(24).map { it + 1 }
        todo_inner_add_thing_third.data = listOf(60).map { it + 1 }
    }

    fun showRepeatDatePicker() {
        todo_iv_add_repeat.visibility = View.VISIBLE
        todo_iv_inner_add_thing_repeat_time_index.visibility = View.VISIBLE
        todo_inner_add_thing_repeat_time_cancel.visibility = View.VISIBLE
        todo_inner_add_thing_repeat_time_confirm.visibility = View.VISIBLE
        todo_inner_add_thing_first
        todo_inner_add_thing_first.apply {
            data = listOf(
                "每天",
                "每周",
                "每月",
                "每年"
            )

            visibility = View.VISIBLE
        }
        initRepeatWheelListener()
    }

    private fun initRepeatWheelListener() {
        if (curOperate != REPEAT) {
            return
        }

        //监听选择的重复类型
        todo_inner_add_thing_second.setOnItemSelectedListener { _, data, _ ->
            when (data) {
                "每天" -> changeRepeatType2EveryDay()
                "每周" -> changeRepeatType2EveryWeek()
                "每月" -> changeRepeatType2EveryMonth()
                "每年" -> changeRepeatType2EveryYear()
            }
        }
    }

    private fun changeRepeatType2EveryDay() {
        todo_inner_add_thing_second.data = emptyList<String>()
        todo_inner_add_thing_third.data = emptyList<String>()
    }

    private fun changeRepeatType2EveryWeek() {
        todo_inner_add_thing_second.data = weekStringList
        todo_inner_add_thing_third.data = emptyList<String>()
    }

    private fun changeRepeatType2EveryMonth() {
        todo_inner_add_thing_second.data = listOf(31).map { it + 1 }
        todo_inner_add_thing_third.data = emptyList<String>()
    }

    private fun changeRepeatType2EveryYear() {
        todo_inner_add_thing_second.data = listOf(12).map { it + 1 }
        todo_inner_add_thing_second.setOnItemSelectedListener { _, data, _ ->
            //date就是月份，这里就根据月份获得这个月的天数
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, data as Int)
            todo_inner_add_thing_third.data =
                listOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).map { it + 1 }
        }
    }

    private fun addNotify() {

    }

    private fun addRepeat() {
        if (curOperate != REPEAT) {
            return
        }

        //首先判断重复类型是否冲突
        if (
            todo_inner_add_thing_first.currentItemPosition + 1 == todo.remindMode.repeatMode ||
            todo.remindMode.repeatMode == RemindMode.NONE
        ) {
            //判断为类型不冲突
            todo.remindMode.repeatMode = todo_inner_add_thing_first.currentItemPosition
            when (todo.remindMode.repeatMode) {
                RemindMode.DAY -> {//每天
                    //不需要进行啥操作
                }
                RemindMode.WEEK -> {//周
                    //添加新的周
                    todo.remindMode.week.addWithoutRepeat(
                        0,
                        todo_inner_add_thing_second.currentItemPosition + 1
                    )
                }
                RemindMode.MONTH -> {
                    todo.remindMode.day.addWithoutRepeat(
                        0,
                        todo_inner_add_thing_second.currentItemPosition + 1
                    )
                }
                RemindMode.YEAR -> {
                    todo.remindMode.date.addWithoutRepeat(
                        0,
                        "${
                            todo_inner_add_thing_second.currentItemPosition
                        }.${
                            todo_inner_add_thing_third.currentItemPosition
                        }"
                    )
                }
            }
        } else {
            BaseApp.context.toast("掌友，只能选择一种重复模式哦！")
        }
    }

    private fun hideWheel() {
        todo_iv_add_repeat.visibility = View.GONE
        todo_iv_inner_add_thing_repeat_time_index.visibility = View.GONE
        todo_inner_add_thing_first.visibility = View.GONE
        todo_inner_add_thing_second.visibility = View.GONE
        todo_inner_add_thing_third.visibility = View.GONE
        todo_inner_add_thing_repeat_time_cancel.visibility = View.GONE
        todo_inner_add_thing_repeat_time_confirm.visibility = View.GONE
        curOperate = NONE
        todo_tv_set_notify_time.isClickable = true
        todo_tv_set_repeat_time.isClickable = true
        todo_inner_add_thing_repeat_time_cancel.visibility = View.GONE
        todo_inner_add_thing_repeat_time_confirm.visibility = View.GONE
    }

    //设置为只有单个选择（重复或者提醒时间）
    fun setAsSinglePicker() {
        todo_inner_add_thing_header.visibility = View.GONE
        todo_et_todo_title.visibility = View.GONE
        todo_iv_add_bell.visibility = View.GONE
        todo_ll_notify_time.visibility = View.GONE
        todo_tv_set_repeat_time.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.todo_check_line_color
            )
        )
    }


    //状态机
    private fun whenStatusDifDoAndChangeStatus(targetStatus: CurOperate, onDiff: () -> Unit) {
        if (curOperate == targetStatus) {
            return
        }
        onDiff()
        curOperate = targetStatus
    }

    fun resetAllRepeatMode(todo: Todo) {
        if (todo.remindMode.repeatMode == RemindMode.NONE) {
            repeatTimeAdapter.resetAll(emptyList())
        } else {
            repeatTimeAdapter.resetAll(remindMode2RemindList(todo.remindMode).map {
                if (todo.remindMode.repeatMode == RemindMode.WEEK) {
                    //如果你要问这里是为什么
                    //视觉图无脑每周一和周一切换我也没办法
                    return@map it.subSequence(1, it.length).toString()
                } else {
                    return@map it
                }
            })
        }
        this.todo.remindMode = todo.remindMode
        isFromDetail = true
    }
}