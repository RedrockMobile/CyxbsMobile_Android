package com.mredrock.cyxbs.todo.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aigestudio.wheelpicker.WheelPicker
import com.mredrock.cyxbs.todo.R
import com.mredrock.cyxbs.todo.adapter.RepeatTimeRvAdapter
import com.mredrock.cyxbs.todo.model.bean.RemindMode
import com.mredrock.cyxbs.todo.model.bean.Todo
import com.mredrock.cyxbs.todo.util.getColor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.extensions.visible

/**
 * description: 增加todo的Dialog
 * author: sanhuzhen
 * date: 2024/8/13 15:19
 */
class AddTodoDialog(context: Context, style: Int, val onAddTodo: (Todo) -> Unit) :
    BottomSheetDialog(context, style) {


    private var SelectRepeatTimeList = ArrayList<String>()
    private val repeatTimeAdapter by lazy { RepeatTimeRvAdapter(0) }
    private val todo by lazy { Todo.generateEmptyTodo() }
    private lateinit var tvCancel: TextView
    private lateinit var tvSave: TextView
    private lateinit var etAddTodo: AppCompatEditText
    private lateinit var tvAddNoticeTime: TextView
    private lateinit var tvDeleteTime: TextView
    private lateinit var tvAddRepeat: TextView
    private lateinit var rvRepeatTime: RecyclerView
    private lateinit var tvCategory: TextView
    private lateinit var llCategory: LinearLayout
    private lateinit var llCategoryList: LinearLayout
    private lateinit var wpCategory: WheelPicker
    private lateinit var btnAddtodoBt: AppCompatTextView
    private lateinit var btnAddtodoBtCancel: AppCompatTextView
    private lateinit var viewLine: View

    init {
        val dialogView =
            LayoutInflater.from(context)
                .inflate(R.layout.todo_dialog_bottom_sheet_addtodo, null, false)
        setContentView(dialogView)
        initView(this)
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
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.todo_addtodo_text_color
                    )
                )
            }
            tvDeleteTime.gone()
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
                        repeatTimeAdapter.submitList(updatedList.toList())
                        SelectRepeatTimeList = updatedList as ArrayList<String> // 更新数据源
                        if (todo.remindMode.repeatMode == RemindMode.WEEK) {
                            todo.remindMode.week.removeAt(position)
                        } else if (todo.remindMode.repeatMode == RemindMode.MONTH) {
                            todo.remindMode.day.removeAt(position)
                        }
                    }
                    if (SelectRepeatTimeList.isEmpty()) {
                        rvRepeatTime.gone()
                        tvAddRepeat.visible()
                    }
                }
            }
        }
    }

    private fun showSelectRepeatDialog() {
        SelectRepeatDialog(
            context,
            R.style.BottomSheetDialogTheme,
            1
        ) { selectRepeatTimeListIndex, selectRepeatTimeList, repeatMode ->
            todo.remindMode.repeatMode = repeatMode
            if (repeatMode == RemindMode.WEEK) {
                todo.remindMode.week = selectRepeatTimeListIndex as ArrayList<Int>
            } else {
                todo.remindMode.day = selectRepeatTimeListIndex as ArrayList
            }
            SelectRepeatTimeList = selectRepeatTimeList as ArrayList<String>
            repeatTimeAdapter.submitList(SelectRepeatTimeList)
            if (SelectRepeatTimeList.isNotEmpty()) {
                rvRepeatTime.visible()
                tvAddRepeat.gone()
            }
        }.show()
    }

    private fun selectCategory() {
        tvCategory.text = wpCategory.data.get(wpCategory.currentItemPosition).toString()
        todo.type = when (wpCategory.currentItemPosition) {
            0 -> "study"
            1 -> "life"
            else -> "other"
        }
    }

    //显示日历
    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun showCalendarDialog() {
        CalendarDialog(
            context,
            R.style.BottomSheetDialogTheme,
            1
        ) { year, month, day, hour, minute ->
            tvAddNoticeTime.apply {
                text = when {
                    hour < 24 -> {
                        val time = "${year}年${month}月${day}日 ${
                            String.format(
                                "%02d",
                                hour
                            )
                        }:${String.format("%02d", minute)}"
                        todo.endTime = time.replace(" ", "")
                        time
                    }

                    else -> {
                        todo.endTime = "${year}年${month}月${day}日00:00"
                        "${year}年${month}月${day}日"
                    }
                }
                setTextColor(getColor(R.color.todo_addtodo_time_color))
            }
            tvDeleteTime.visible()
        }.show()
    }

    //更新颜色
    private fun updateTextColor(s: CharSequence) {
        if (s.isEmpty()) {
            tvSave.setTextColor(getColor(R.color.todo_addtodo_save_text_none_color))
        } else {
            tvSave.setTextColor(getColor(R.color.todo_addtodo_save_text_color))
        }
    }

    private fun showCategoryUI() {
        viewLine.visible()
        llCategoryList.visible()
        btnAddtodoBt.visible()
        btnAddtodoBtCancel.visible()
        wpCategory.data = listOf("学习", "生活", "其他")
    }

    private fun hideUI() {
        viewLine.gone()
        llCategoryList.gone()
        btnAddtodoBt.gone()
        btnAddtodoBtCancel.gone()
    }
    private fun initView(dialog: Dialog){
        tvCancel = dialog.findViewById(R.id.todo_tv_addtodo_cancel)
        tvSave = dialog.findViewById(R.id.todo_tv_addtodo_save)
        etAddTodo = dialog.findViewById(R.id.todo_et_addtodo_title)
        tvAddNoticeTime = dialog.findViewById(R.id.todo_tv_addtodo_notice)
        tvDeleteTime = dialog.findViewById(R.id.todo_tv_addtodo_notice_time_delete)
        tvAddRepeat = dialog.findViewById(R.id.todo_tv_addtodo_repeat)
        rvRepeatTime = dialog.findViewById(R.id.todo_rv_addtodo_repeat)
        tvCategory = dialog.findViewById(R.id.todo_tv_addtodo_category_text)
        llCategory = dialog.findViewById(R.id.todo_ll_addtodo_category)
        llCategoryList = dialog.findViewById(R.id.todo_ll_addtodo_category_list)
        wpCategory = dialog.findViewById(R.id.todo_wp_addtodo_category_list)
        btnAddtodoBt = dialog.findViewById(R.id.todo_btn_confirm_addtodo)
        btnAddtodoBtCancel = dialog.findViewById(R.id.todo_btn_cancel_addtodo)
        viewLine = dialog.findViewById(R.id.todo_v_addtodo_category_line)
    }

}