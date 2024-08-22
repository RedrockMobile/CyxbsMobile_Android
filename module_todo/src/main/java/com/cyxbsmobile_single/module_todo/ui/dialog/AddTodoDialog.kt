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


    private var SelectRepeatTimeList = ArrayList<String>()
    private val repeatTimeAdapter by lazy { RepeatTimeRvAdapter() }
    private val todo by lazy { Todo.generateEmptyTodo() }
    private val tvCancel by lazy { findViewById<TextView>(R.id.todo_tv_addtodo_cancel)!! }
    private val tvSave by lazy { findViewById<TextView>(R.id.todo_tv_addtodo_save)!! }
    private val etAddTodo by lazy { findViewById<AppCompatEditText>(R.id.todo_et_addtodo_title)!! }
    private val tvAddNoticeTime by lazy { findViewById<TextView>(R.id.todo_tv_addtodo_notice)!! }
    private val tvDeleteTime by lazy { findViewById<TextView>(R.id.todo_tv_addtodo_notice_time_delete)!! }
    private val tvAddRepeat by lazy { findViewById<TextView>(R.id.todo_tv_addtodo_repeat)!! }
    private val rvRepeatTime by lazy { findViewById<RecyclerView>(R.id.todo_rv_addtodo_repeat)!! }
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
                todo.todoId = System.currentTimeMillis() / 1000
                onAddTodo(todo)
                dismiss()
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
            showSelectRepeatDialog()
        }
        llCategory.setOnClickListener {
            showCategoryUI()
        }
        btnAddtodoBt.setOnClickListener {
            selectCategory()
            hideUI()
        }
        btnAddtodoBtCancel.setOnClickListener {
            hideUI()
        }
        rvRepeatTime.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = repeatTimeAdapter.apply {
                setOnItemClick { position ->
                    if (position in SelectRepeatTimeList.indices) {
                        val updatedList = SelectRepeatTimeList.toMutableList()
                        updatedList.removeAt(position)
                        repeatTimeAdapter.submitList(updatedList)
                        SelectRepeatTimeList = updatedList as ArrayList<String> // 更新数据源
                        if (todo.remindMode.repeatMode == RemindMode.WEEK) {
                            todo.remindMode.week.removeAt(position)
                        } else if (todo.remindMode.repeatMode == RemindMode.MONTH) {
                            todo.remindMode.day.removeAt(position)
                        }
                    }
                    if (SelectRepeatTimeList.isEmpty()){
                        rvRepeatTime.visibility = View.GONE
                        tvAddRepeat.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun showSelectRepeatDialog() {
        SelectRepeatDialog(context) { selectRepeatTimeListIndex, selectRepeatTimeList, repeatMode ->
            todo.remindMode.repeatMode = repeatMode
            if (repeatMode == RemindMode.WEEK) {
                todo.remindMode.week = selectRepeatTimeListIndex as ArrayList<Int>
            } else {
                todo.remindMode.day = selectRepeatTimeListIndex as ArrayList
            }
            SelectRepeatTimeList = selectRepeatTimeList as ArrayList<String>
            repeatTimeAdapter.submitList(SelectRepeatTimeList)
            if (SelectRepeatTimeList.isNotEmpty()){
                rvRepeatTime.visibility = View.VISIBLE
                tvAddRepeat.visibility = View.GONE
            }
        }.show()
    }

    private fun selectCategory() {
        tvCategory.text = wpCategory.data.get(wpCategory.currentItemPosition).toString()
        todo.type = tvCategory.text.toString()
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

    private fun showCategoryUI() {
        llCategoryList.visibility = View.VISIBLE
        btnAddtodoBt.visibility = View.VISIBLE
        btnAddtodoBtCancel.visibility = View.VISIBLE
        wpCategory.data = listOf("学习", "生活", "其他")
    }

    private fun hideUI() {
        llCategoryList.visibility = View.GONE
        btnAddtodoBt.visibility = View.GONE
        btnAddtodoBtCancel.visibility = View.GONE
    }


}