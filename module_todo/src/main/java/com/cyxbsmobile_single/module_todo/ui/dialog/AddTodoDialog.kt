package com.cyxbsmobile_single.module_todo.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aigestudio.wheelpicker.WheelPicker
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.RepeatTimeRvAdapter
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.util.addWithoutRepeat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.lib.utils.extensions.toast

/**
 * description: 增加todo的Dialog
 * author: sanhuzhen
 * date: 2024/8/13 15:19
 */
class AddTodoDialog(context: Context, val onAddTodo: (Todo) -> Unit) :
    BottomSheetDialog(context, R.style.BottomSheetDialogTheme) {


    // 是否显示重复
    private var isRepeatUiVisible = false
    private var isCategoryUiVisible = false


    private val selectRepeatTimeList = mutableListOf<String>()

    private val repeatTimeAdapter1 by lazy { RepeatTimeRvAdapter() }
    private val repeatTimeAdapter2 by lazy { RepeatTimeRvAdapter() }

    private val todo by lazy { Todo.generateEmptyTodo() }

    private val tvCancel by lazy { findViewById<TextView>(R.id.todo_tv_addtodo_cancel)!! }
    private val tvSave by lazy { findViewById<TextView>(R.id.todo_tv_addtodo_save)!! }
    private val etAddTodo by lazy { findViewById<AppCompatEditText>(R.id.todo_et_addtodo_title)!! }
    private val tvAddNoticeTime by lazy { findViewById<TextView>(R.id.todo_tv_addtodo_notice)!! }
    private val tvDeleteTime by lazy { findViewById<TextView>(R.id.todo_tv_addtodo_notice_time_delete)!! }
    private val tvAddRepeat by lazy { findViewById<TextView>(R.id.todo_tv_addtodo_repeat)!! }
    private val rvRepeatTime by lazy { findViewById<RecyclerView>(R.id.todo_rv_addtodo_repeat)!! }
    private val rvSelectRepeatTime by lazy { findViewById<RecyclerView>(R.id.todo_rv_addtodo_repeat_list)!! }
    private val rlSelectRepeatTime by lazy { findViewById<RelativeLayout>(R.id.todo_rl_addtodo_repeat)!! }
    private val wpRepeatMode by lazy { findViewById<WheelPicker>(R.id.todo_wp_addtodo_repeat_mode)!! }
    private val wpRepeatTime by lazy { findViewById<WheelPicker>(R.id.todo_wp_addtodo_repeat_time)!! }
    private val btnAddRepeatTime by lazy { findViewById<AppCompatButton>(R.id.todo_btn_addtodo_repeat_add)!! }
    private val tvCategory by lazy { findViewById<TextView>(R.id.todo_tv_addtodo_category_text)!! }
    private val llCategory by lazy { findViewById<LinearLayout>(R.id.todo_ll_addtodo_category)!! }
    private val llCategoryList by lazy { findViewById<LinearLayout>(R.id.todo_ll_addtodo_category_list)!! }
    private val wpCategory by lazy { findViewById<WheelPicker>(R.id.todo_wp_addtodo_category_list)!! }
    private val btnAddtodoBt by lazy { findViewById<AppCompatButton>(R.id.todo_btn_confirm_addtodo)!! }
    private val btnAddtodoBtCancel by lazy { findViewById<AppCompatButton>(R.id.todo_btn_cancel_addtodo)!! }

    init {
        val dialogView =
            LayoutInflater.from(context)
                .inflate(R.layout.todo_dialog_bottom_sheet_addtodo, null, false)
        setContentView(dialogView)

        window?.apply {
            val view = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val bottomSheet = BottomSheetBehavior.from(view)
            val dm = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(dm)
            //保证展示完全
            bottomSheet.peekHeight = dm.heightPixels
        }
        dialogView.apply {
            initClick()
        }
    }

    private fun initClick() {


        tvCancel.setOnClickListener {
            dismiss()
        }
        tvSave.setOnClickListener {
            if (etAddTodo.text?.isNotEmpty() == true) {
                todo.title = etAddTodo.text.toString()
                onAddTodo(todo)
            } else {
                toast("掌友，标题不能为空哦!")
            }
        }
        etAddTodo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                updateTextColor(s)
            }

            override fun afterTextChanged(s: Editable) {
                updateTextColor(s)
            }
        })
        tvAddNoticeTime.setOnClickListener {
            showCalendarDialog()
        }
        tvDeleteTime.setOnClickListener {
            tvAddNoticeTime.apply {
                text = "设置截止时间"
                setTextColor(context.resources.getColor(R.color.todo_addtodo_save_text_none_color))
            }
            tvDeleteTime.visibility = View.GONE
            todo.remindMode.notifyDateTime = ""
        }
        tvAddRepeat.setOnClickListener {
            if (!isCategoryUiVisible) {
                showSelectRepeatTime()
            }
        }
        llCategory.setOnClickListener {
            if (!isCategoryUiVisible) {
                showCategoryUI()
            }
        }
        btnAddtodoBt.setOnClickListener {
            if (isRepeatUiVisible) {
                selectRepeatTime()
            } else {
                selectCategory()
            }
            hideUI()
        }
        btnAddtodoBtCancel.setOnClickListener {
            selectRepeatTimeList.clear()
            hideUI()
        }
        btnAddRepeatTime.setOnClickListener {
            addRepeatTime()
        }

        rvSelectRepeatTime.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = repeatTimeAdapter1.apply {
                setOnItemClick { position ->
                    if (position in selectRepeatTimeList.indices) {
                        if (todo.remindMode.repeatMode == RemindMode.WEEK) {
                            todo.remindMode.week.removeAt(position)
                        } else if (todo.remindMode.repeatMode == RemindMode.MONTH) {
                            todo.remindMode.day.removeAt(position)
                        }
                        selectRepeatTimeList.removeAt(position)
                        // 提交新列表，并通知Adapter项已被删除
                        repeatTimeAdapter1.submitList(selectRepeatTimeList.toList())
                        repeatTimeAdapter2.submitList(selectRepeatTimeList.toList())
                        repeatTimeAdapter1.notifyItemRemoved(position)
                        repeatTimeAdapter2.notifyItemRemoved(position)
                    }
                }
            }
        }

        rvRepeatTime.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = repeatTimeAdapter2.apply {
                setOnItemClick { position ->
                    if (position in selectRepeatTimeList.indices) {
                        if (todo.remindMode.repeatMode == RemindMode.WEEK) {
                            todo.remindMode.week.removeAt(position)
                        } else if (todo.remindMode.repeatMode == RemindMode.MONTH) {
                            todo.remindMode.day.removeAt(position)
                        }
                        selectRepeatTimeList.removeAt(position)
                        // 提交新列表，并通知Adapter项已被删除
                        repeatTimeAdapter1.submitList(selectRepeatTimeList.toList())
                        repeatTimeAdapter2.submitList(selectRepeatTimeList.toList())
                        repeatTimeAdapter1.notifyItemRemoved(position)
                        repeatTimeAdapter2.notifyItemRemoved(position)
                        if (selectRepeatTimeList.isEmpty()) {
                            tvAddRepeat.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun selectCategory() {
        tvCategory.text = wpCategory.data.get(wpCategory.currentItemPosition).toString()
        todo.type = tvCategory.text.toString()
    }

    private fun selectRepeatTime() {
        repeatTimeAdapter2.submitList(selectRepeatTimeList)
    }

    //显示日历
    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun showCalendarDialog() {
        CalendarDialog(context) { year, month, day, hour, minute ->
            tvAddNoticeTime.apply {
                text = when {
                    hour < 24 -> {
                        val time = "${year}年${month}月${day}日 ${
                            String.format(
                                "%02d",
                                hour
                            )
                        }:${String.format("%02d", minute)}"
                        todo.remindMode.notifyDateTime = time.replace(" ", "")
                        time
                    }

                    else -> {
                        todo.remindMode.notifyDateTime = "${year}年${month}月${day}日00:00"
                        "${year}年${month}月${day}日"
                    }
                }
                setTextColor(context.resources.getColor(R.color.todo_addtodo_inner_text_color))
            }
            tvDeleteTime.visibility = View.VISIBLE
        }.show()
    }

    //更新颜色
    private fun updateTextColor(s: CharSequence) {
        if (s.isEmpty()) {
            tvSave.setTextColor(context.resources.getColor(R.color.todo_addtodo_save_text_none_color))
        } else {
            tvSave.setTextColor(context.resources.getColor(R.color.todo_addtodo_save_text_color))
        }
    }

    //显示选择重复时间的UI
    private fun showSelectRepeatTime() {
        isRepeatUiVisible = true
        rlSelectRepeatTime.visibility = View.VISIBLE
        btnAddtodoBt.visibility = View.VISIBLE
        btnAddtodoBtCancel.visibility = View.VISIBLE
        rvRepeatTime.visibility = View.VISIBLE
        wpRepeatMode.data = listOf("每天", "每周", "每月", "", "", "", "")
        wpRepeatMode.setSelectedItemPosition(0)
        initWpData()
    }

    private fun initWpData() {
        when (wpRepeatMode.currentItemPosition) {
            0 -> {
                wpRepeatTime.data = emptyList<String>()
            }

            1 -> {
                wpRepeatTime.data =
                    listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
                wpRepeatTime.setSelectedItemPosition(0)
            }

            2 -> {
                wpRepeatTime.data = (1..31).toList()
                wpRepeatTime.setSelectedItemPosition(0)
            }
        }
        wpRepeatMode.setOnItemSelectedListener { _, _, position ->
            when (position) {
                0 -> {
                    wpRepeatTime.data = emptyList<String>()
                }

                1 -> {
                    wpRepeatTime.data =
                        listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
                    wpRepeatTime.setSelectedItemPosition(0)
                }

                2 -> {
                    wpRepeatTime.data = (1..31).toList()
                    wpRepeatTime.setSelectedItemPosition(0)
                }
            }
        }
    }

    private fun showCategoryUI() {
        isCategoryUiVisible = true
        llCategoryList.visibility = View.VISIBLE
        btnAddtodoBt.visibility = View.VISIBLE
        btnAddtodoBtCancel.visibility = View.VISIBLE

        wpCategory.data = listOf("学习", "生活", "其他")
    }

    private fun hideUI() {
        isRepeatUiVisible = false
        isCategoryUiVisible = false
        rlSelectRepeatTime.visibility = View.GONE
        llCategoryList.visibility = View.GONE
        btnAddtodoBt.visibility = View.GONE
        btnAddtodoBtCancel.visibility = View.GONE
        if (selectRepeatTimeList.isNotEmpty()) {
            tvAddRepeat.visibility = View.GONE
        }
    }

    //添加重复时间
    private fun addRepeatTime() {

        if (selectRepeatTimeList.isEmpty()) {
            todo.remindMode.repeatMode = RemindMode.NONE
        }

        val currentRepeatMode = wpRepeatMode.currentItemPosition.plus(1)

        if (currentRepeatMode == todo.remindMode.repeatMode || todo.remindMode.repeatMode == RemindMode.NONE) {
            todo.remindMode.repeatMode = currentRepeatMode
            // 获取当前选中的重复时间
            val currentRepeatTime = when (todo.remindMode.repeatMode) {
                RemindMode.DAY -> "每天"
                RemindMode.WEEK -> {
                    todo.remindMode.week.addWithoutRepeat(0, wpRepeatTime.currentItemPosition + 1)
                    wpRepeatTime.data[wpRepeatTime.currentItemPosition].toString()
                }

                RemindMode.MONTH -> {
                    val day =
                        wpRepeatTime.data.get(wpRepeatTime.currentItemPosition).toString()
                    todo.remindMode.day.addWithoutRepeat(0, wpRepeatTime.currentItemPosition + 1)
                    "每月${day}日"
                }

                else -> null
            }

            // 确保当前选中的重复时间不为空且不在列表中
            if (!selectRepeatTimeList.contains(currentRepeatTime)) {
                // 将新项插入到列表的开头
                currentRepeatTime?.let { selectRepeatTimeList.add(0, it) }
                repeatTimeAdapter1.submitList(ArrayList(selectRepeatTimeList)) {
                    // 确保RecyclerView显示在第一个项目
                    rvSelectRepeatTime.layoutManager?.scrollToPosition(0)
                }  // 刷新适配器
            }
        } else {
            toast("只能选择一种重复模式哦！")
        }


    }
}