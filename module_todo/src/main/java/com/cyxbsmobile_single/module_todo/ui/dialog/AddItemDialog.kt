package com.cyxbsmobile_single.module_todo.ui.dialog

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aigestudio.wheelpicker.WheelPicker
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.ChooseYearAdapter
import com.cyxbsmobile_single.module_todo.adapter.RepeatTimeAdapter
import com.cyxbsmobile_single.module_todo.model.bean.DateBeen
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog.CurOperate.*
import com.cyxbsmobile_single.module_todo.util.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.common.utils.extensions.dip
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.extensions.toast
import java.util.*
import kotlin.collections.ArrayList

/**
 * Author: RayleighZ
 * Time: 2021-09-18 8:50
 * 增加todo用dialog
 */
class AddItemDialog(context: Context, val onConfirm: (Todo) -> Unit) :
    BottomSheetDialog(context, com.mredrock.cyxbs.common.R.style.BottomSheetDialogTheme) {

    private val dateBeenList: ArrayList<ArrayList<DateBeen>>
            by lazy { getYearDateSting() }

    private val todo by lazy { Todo.generateEmptyTodo() }

    private val dateBeenStringList by lazy { ArrayList<List<String>>() }
    private val todo_tv_set_notify_time by lazy { findViewById<TextView>(R.id.todo_tv_set_notify_time)!! }
    private val todo_inner_add_thing_second by lazy { findViewById<WheelPicker>(R.id.todo_inner_add_thing_second)!! }
    private val todo_inner_add_thing_third by lazy { findViewById<WheelPicker>(R.id.todo_inner_add_thing_third)!! }
    private val todo_inner_add_thing_first by lazy { findViewById<WheelPicker>(R.id.todo_inner_add_thing_first)!! }
    private val todo_inner_add_rv_thing_repeat_list by lazy { findViewById<RecyclerView>(R.id.todo_inner_add_rv_thing_repeat_list)!! }
    private val todo_tv_set_repeat_time by lazy { findViewById<TextView>(R.id.todo_tv_set_repeat_time)!! }
    private val todo_iv_add_repeat by lazy { findViewById<AppCompatImageView>(R.id.todo_iv_add_repeat)!! }
    private val todo_inner_add_thing_repeat_time_confirm by lazy { findViewById<AppCompatTextView>(R.id.todo_inner_add_thing_repeat_time_confirm)!! }
    private val todo_inner_add_thing_repeat_time_cancel by lazy { findViewById<AppCompatTextView>(R.id.todo_inner_add_thing_repeat_time_cancel)!! }
    private val todo_tv_del_notify_time by lazy { findViewById<AppCompatTextView>(R.id.todo_tv_del_notify_time)!! }
    private val todo_tv_add_thing_save by lazy { findViewById<TextView>(R.id.todo_tv_add_thing_save)!! }
    private val todo_et_todo_title by lazy { findViewById<AppCompatEditText>(R.id.todo_et_todo_title)!! }
    private val todo_iv_inner_add_thing_repeat_time_index by lazy { findViewById<ImageView>(R.id.todo_iv_inner_add_thing_repeat_time_index)!! }
    private val todo_tv_add_thing_cancel by lazy { findViewById<TextView>(R.id.todo_tv_add_thing_cancel)!! }
    private val todo_inner_add_thing_header by lazy { findViewById<FrameLayout>(R.id.todo_inner_add_thing_header)!! }
    private val todo_iv_add_bell by lazy { findViewById<ImageView>(R.id.todo_iv_add_bell)!! }
    private val todo_ll_notify_time by lazy { findViewById<LinearLayoutCompat>(R.id.todo_ll_notify_time)!! }


    private val chooseYearAdapter by lazy {
        ChooseYearAdapter(
            ArrayList(getNextFourYears())
        ) { currentYearOffset ->
            if (currentYearOffset != 0) {

                todo.remindMode.notifyDateTime =
                    "${currentYearOffset + Calendar.getInstance().get(Calendar.YEAR)}年1月1日00:00"
                todo_tv_set_notify_time.text = "1月1日00:00"
                val cacheList = ArrayList(dateBeenStringList[currentYearOffset])
                for (i in dateBeenStringList[currentYearOffset].indices) {
                    cacheList[i] =
                        dateBeenStringList[currentYearOffset][(i + 1) % dateBeenStringList[currentYearOffset].size]
                }
                todo_inner_add_thing_second.data = IntArray(24) { (it + 1) % 24 }.toList()
                todo_inner_add_thing_third.data = IntArray(60) { (it + 1) % 60 }.toList()
                todo_inner_add_thing_first.data = cacheList
            } else {
                val calendar = Calendar.getInstance().apply {
                    add(Calendar.MINUTE, 5)
                }

                val curHour = calendar.get(Calendar.HOUR_OF_DAY)
                val curMin = calendar.get(Calendar.MINUTE)

                val notifyString = "${
                    calendar.get(Calendar.MONTH) + 1
                }月${
                    calendar.get(Calendar.DAY_OF_MONTH)
                }日${
                    numToString(curHour)
                }:${
                    numToString(curMin)
                }"
                //重置时间选择器为5分钟之后
                setToNext5Min()
                todo.remindMode.notifyDateTime = "${
                    calendar.get(Calendar.YEAR)
                }年$notifyString"
                todo_tv_set_notify_time.text = notifyString
            }
        }
    }

    private fun setToNext5Min(yearOffset: Int = 0) {
        todo_inner_add_thing_first.data = emptyList<String>()
        todo_inner_add_thing_second.data = emptyList<String>()
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 5)
        }

        val curHour = calendar.get(Calendar.HOUR_OF_DAY)
        val curMin = calendar.get(Calendar.MINUTE)

        val cacheList = ArrayList(dateBeenStringList[yearOffset])
        for (i in dateBeenStringList[yearOffset].indices) {
            cacheList[i] = dateBeenStringList[0][(i + 1) % dateBeenStringList[0].size]
        }
        todo_inner_add_thing_first.data = cacheList
        todo_inner_add_thing_second.data = IntArray(24) { (it + curHour + 1) % 24 }.toList()
        todo_inner_add_thing_third.data = IntArray(60) { (it + curMin + 1) % 60 }.toList()
    }

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
                RemindMode.DAY -> {
                    todo.remindMode.repeatMode = RemindMode.NONE
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
            LayoutInflater.from(context).inflate(R.layout.todo_dialog_add_todo, null, false)
        setContentView(dialogView)

        dialogView?.apply {
            //首先，添加点击事件
            initClick()
        }
    }


    private fun initClick() {
        window?.apply {
            val view = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val bottomSheet = BottomSheetBehavior.from(view)
            val dm = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(dm)
            //保证展示完全
            bottomSheet.peekHeight = dm.heightPixels
        }

        //先配置manager
        todo_inner_add_rv_thing_repeat_list.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }

        //设置添加提醒日期的点击事件
        todo_tv_set_notify_time.setOnClickListener {
            whenStatusDifDoAndChangeStatus(NOTIFY) { showNotifyDatePicker() }
        }

        //设置重复时间的点击事件
        todo_tv_set_repeat_time.setOnClickListener {
            whenStatusDifDoAndChangeStatus(REPEAT) { showRepeatDatePicker() }
        }

        //添加的点击事件
        todo_iv_add_repeat.setOnClickListener { addRepeat() }
        //confirm的点击事件
        todo_inner_add_thing_repeat_time_confirm.setOnClickListener { onConfirmClick() }
        //cancel的点击事件
        todo_inner_add_thing_repeat_time_cancel.setOnClickListener { onCancelClick() }
        //删除提醒时间的点击事件
        todo_tv_del_notify_time.setOnClickListener { onDelClick() }
        //保存逻辑
        todo_tv_add_thing_save.setOnClickListener {
            //如果没输入标题，就ban掉
            if (todo_et_todo_title.text.toString().isEmpty()) {
                "掌友，标题不能为空哦".toast()
                return@setOnClickListener
            }
            todo.title = todo_et_todo_title.text.toString()
            onConfirm(todo)
            hide()
        }
        //取消逻辑
        todo_tv_add_thing_cancel.setOnClickListener { hide() }
    }

    //当从detail界面过来复用的时候，提供一个接口来重置todo的提醒模式
    fun resetNotifyTime(todo: Todo) {
        this.todo.remindMode = todo.remindMode
        isFromDetail = true
    }

    private fun onConfirmClick() {
        if (curOperate == NOTIFY) {
            addNotify()
            repeatTimeAdapter.removeAll()
            todo_inner_add_rv_thing_repeat_list.adapter = repeatTimeAdapter
        }
        if (isFromDetail) {
            onConfirm(todo)
            hide()
            return
        }
        hideWheel()
    }

    private fun onCancelClick() {
        when (curOperate) {
            NOTIFY -> {
                todo.remindMode.notifyDateTime = ""
                todo_tv_set_notify_time.text = getString(R.string.todo_inner_add_thing_nf_text)
                todo_inner_add_rv_thing_repeat_list.adapter = ChooseYearAdapter(ArrayList()){  }
            }//判定为在添加提醒
            REPEAT -> {
                val remindDate = todo.remindMode.notifyDateTime
                todo.remindMode = RemindMode.generateDefaultRemindMode()
                todo.remindMode.notifyDateTime = remindDate
            }//因为已经添加了提醒，这里就直接隐藏轮子
            else -> {
                hideWheel()
            }
        }
        hideWheel()
    }

    private fun onDelClick() {
        todo.remindMode.notifyDateTime = ""
        todo_tv_set_notify_time.text = "设置提醒时间"
    }

    fun showNotifyDatePicker() {
        //调整可见性
        todo_iv_add_repeat.visibility = View.GONE
        todo_iv_inner_add_thing_repeat_time_index.visibility = View.VISIBLE
        todo_inner_add_thing_first.visible()
        todo_inner_add_thing_second.visible()
        todo_inner_add_thing_third.visible()
        todo_inner_add_thing_repeat_time_cancel.visibility = View.VISIBLE
        todo_inner_add_thing_repeat_time_confirm.visibility = View.VISIBLE

        //配置年份选择Adapter
        todo_inner_add_rv_thing_repeat_list.adapter = chooseYearAdapter
        //默认提醒时间设置为当前时间的五分钟后
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 5)
        }

        val curHour = calendar.get(Calendar.HOUR_OF_DAY)
        val curMin = calendar.get(Calendar.MINUTE)
        val curMonth = calendar.get(Calendar.MONTH) + 1
        val curDay = calendar.get(Calendar.DAY_OF_MONTH)
        val notifyString = "${curMonth}月${curDay}日${numToString(curHour)}:${numToString(curMin)}"

        todo_tv_set_notify_time.text = notifyString

        todo.remindMode.notifyDateTime = "${
            calendar.get(Calendar.YEAR)
        }年$notifyString"

        dateBeenList[0][0].type = DateBeen.TODAY
        for (yearIndex in 0..3) {
            //配置仨时间选择器（默认展示今年）
            for (i in 0..2) {
                //增加占位
                dateBeenList[yearIndex].add(DateBeen(0, 0, 1, DateBeen.EMPTY))
            }
            dateBeenStringList.add(dateBeenList[yearIndex].map { dateBeen ->
                when (dateBeen.type) {
                    DateBeen.EMPTY -> {
                        ""
                    }
                    DateBeen.TODAY -> {
                        "今天"
                    }
                    else -> {
                        "${dateBeen.month}月${
                            numToString(dateBeen.day)
                        }日 周${
                            weekStringList[dateBeen.week - 1]
                        }"
                    }
                }
            })
        }
        todo_inner_add_thing_first.setOnItemSelectedListener { _, data, _ ->
            if (data == "") {
                todo_inner_add_thing_second.data = emptyList<String>()
                todo_inner_add_thing_third.data = emptyList<String>()
            } else {
                todo_inner_add_thing_second.data = IntArray(24) { (it + 1) % 24 }.toList()
                todo_inner_add_thing_third.data = IntArray(60) { (it + 1) % 60 }.toList()
            }
        }
        //设置时间选择器为5分钟之后
        setToNext5Min()
    }

    fun showRepeatDatePicker() {
        todo_iv_add_repeat.visibility = View.VISIBLE
        todo_iv_inner_add_thing_repeat_time_index.visibility = View.VISIBLE
        todo_inner_add_thing_repeat_time_cancel.visibility = View.VISIBLE
        todo_inner_add_thing_repeat_time_confirm.visibility = View.VISIBLE
        todo_inner_add_thing_first.apply {
            visibility = View.VISIBLE
            data = listOf(
                "每天",
                "每周",
                "每月",
                "每年",
                "",
                "",
                ""
            )
        }
        todo_inner_add_thing_second.visible()
        todo_inner_add_thing_third.visible()
        initRepeatWheelListener()
    }

    private fun initRepeatWheelListener() {
        if (curOperate != REPEAT) {
            return
        }

        //更换Adapter
        todo_inner_add_rv_thing_repeat_list.adapter = repeatTimeAdapter

        //监听选择的重复类型
        todo_inner_add_thing_first.setOnItemSelectedListener { _, data, _ ->
            when (data) {
                "每天" -> changeRepeatType2EveryDay()
                "每周" -> changeRepeatType2EveryWeek()
                "每月" -> changeRepeatType2EveryMonth()
                "每年" -> changeRepeatType2EveryYear()
                "" -> changeRepeatType2EveryDay()
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
        todo_inner_add_thing_second.data = IntArray(31) { it + 1 }.toList()
        todo_inner_add_thing_third.data = emptyList<String>()
    }

    private fun changeRepeatType2EveryYear() {
        todo_inner_add_thing_second.data = IntArray(12) { it + 1 }.toList()
        todo_inner_add_thing_third.data = IntArray(31) { it + 1 }.toList()
        todo_inner_add_thing_second.setOnItemSelectedListener { _, data, _ ->
            //暴力处理String转Integer的错乱问题
            for (c in data.toString()) {
                if (!c.isDigit()){
                    return@setOnItemSelectedListener
                }
            }
            //date就是月份，这里就根据月份获得这个月的天数
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, Integer.parseInt(data.toString()) - 1)
            todo_inner_add_thing_third.data =
                IntArray(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) { it + 1 }.toList()
        }
    }

    //增加提醒日期
    private fun addNotify() {
        val curSelectYear = chooseYearAdapter.stringArray[chooseYearAdapter.curSelPosition]
        var date =
            todo_inner_add_thing_first.data[todo_inner_add_thing_first.curPos()].toString()
        date = when (date) {
            "" -> {
                //如果是用来占位的，就不增加进去
                return
            }
            "今天" -> {
                //如果是今天，就一定是dateBeenList此年的第一个date
                val curDate = dateBeenList[0][0]
                "${curDate.month}月${curDate.day}日"
            }
            else -> {
                date.subSequence(0, date.length - 3).toString()
            }
        }
        val hour = todo_inner_add_thing_second.data[todo_inner_add_thing_second.curPos()].toString()
        val min = todo_inner_add_thing_third.data[todo_inner_add_thing_third.curPos()].toString()
        todo.remindMode.notifyDateTime =
            "${curSelectYear}年${date}${
                numToString(hour)
            }:${
                numToString(min)
            }"
        val dateWithoutWeek =
            "${date}${numToString(hour)}:${numToString(min)}"
        todo_tv_set_notify_time.text = dateWithoutWeek
    }

    private fun addRepeat() {
        if (curOperate != REPEAT) {
            return
        }

        //首先判断重复类型是否冲突
        val repeatType = repeatString2Num(
            todo_inner_add_thing_first
                .data[todo_inner_add_thing_first.curPos()].toString()
        )
        if (
            repeatType == todo.remindMode.repeatMode ||
            todo.remindMode.repeatMode == RemindMode.NONE
        ) {
            //判断为类型不冲突
            todo.remindMode.repeatMode = repeatType
            val secondPos = todo_inner_add_thing_second.curPos() + 1
            val thirdPos = todo_inner_add_thing_third.curPos() + 1
            val repeatString =
                when (todo.remindMode.repeatMode) {
                    RemindMode.DAY -> {//每天
                        //不需要进行啥操作
                        "每天"
                    }
                    RemindMode.WEEK -> {//周
                        //添加新的周
                        todo.remindMode.week.addWithoutRepeat(
                            0,
                            secondPos
                        )
                        "周${weekStringList[todo_inner_add_thing_second.curPos() % 7]}"
                    }
                    RemindMode.MONTH -> {
                        todo.remindMode.day.addWithoutRepeat(
                            0,
                            secondPos
                        )
                        "每月${secondPos}日"
                    }
                    RemindMode.YEAR -> {
                        todo.remindMode.date.addWithoutRepeat(
                            0,
                            "${secondPos}.${thirdPos}"
                        )
                        "每年${secondPos}月${thirdPos}日"
                    }
                    else -> ""
                }
            if (repeatString != "") {
                repeatTimeAdapter.addString(repeatString)
                todo_inner_add_rv_thing_repeat_list.scrollToPosition(0)
            }
        } else {
            toast("掌友，只能选择一种重复模式哦！")
        }
    }

    private fun hideWheel() {
        todo_iv_add_repeat.visibility = View.GONE
        todo_iv_inner_add_thing_repeat_time_index.visibility = View.GONE
        todo_inner_add_thing_first.gone()
        todo_inner_add_thing_second.gone()
        todo_inner_add_thing_third.gone()
        todo_inner_add_thing_repeat_time_cancel.visibility = View.GONE
        todo_inner_add_thing_repeat_time_confirm.visibility = View.GONE
        curOperate = NONE
        todo_tv_set_notify_time.isClickable = true
        todo_tv_set_repeat_time.isClickable = true
        todo_inner_add_thing_repeat_time_cancel.visibility = View.GONE
        todo_inner_add_thing_repeat_time_confirm.visibility = View.GONE
    }

    //设置为只有单个选择（重复或者提醒时间）
    fun setAsSinglePicker(curOperate: CurOperate) {
        this.curOperate = curOperate
        setMargin(todo_tv_set_repeat_time, left = context.dip(15))
        when (curOperate) {
            REPEAT -> {
                todo_tv_set_repeat_time.text = "设置重复提醒"
                todo_inner_add_rv_thing_repeat_list.adapter = repeatTimeAdapter
                repeatTimeAdapter.notifyDataSetChanged()
            }
            NOTIFY -> {
                todo_tv_set_repeat_time.text = "设置提醒时间"
                todo_inner_add_rv_thing_repeat_list.adapter = chooseYearAdapter
            }
            else -> {
                todo_tv_set_repeat_time.text = "设置重复提醒"
                todo_inner_add_rv_thing_repeat_list.adapter = repeatTimeAdapter
                repeatTimeAdapter.notifyDataSetChanged()
            }
        }
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
        curOperate = targetStatus
        hideKeyboard(context, todo_et_todo_title)
        onDiff()
    }

    fun resetAllRepeatMode(todo: Todo) {
        if (todo.remindMode.repeatMode == RemindMode.NONE) {
            repeatTimeAdapter.resetAll(emptyList())
        } else {
            repeatTimeAdapter.resetAll(remindMode2RemindList(todo.remindMode).map {
                if (todo.remindMode.repeatMode == RemindMode.WEEK) {
                    //如果你要问这里是为什么
                    //视觉图每周一和周一切换我也没办法
                    return@map it.subSequence(1, it.length).toString()
                } else {
                    return@map it
                }
            })
        }
        this.todo.remindMode = todo.remindMode
        isFromDetail = true
    }

    private fun WheelPicker.gone() {
        data = emptyList<Int>()
        visibility = View.GONE
    }

    private fun WheelPicker.visible() {
        data = emptyList<Int>()
        visibility = View.VISIBLE
    }

    private fun WheelPicker.curPos(): Int {
        return when {
            data.size == 0 -> {
                0
            }
            currentItemPosition <= 0 -> {
                (data.size + currentItemPosition) % (data.size)
            }
            else -> {
                currentItemPosition % (data.size)
            }
        }
    }
}