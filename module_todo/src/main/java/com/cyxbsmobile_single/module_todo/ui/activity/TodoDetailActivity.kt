package com.cyxbsmobile_single.module_todo.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aigestudio.wheelpicker.WheelPicker
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.RepeatTimeRvAdapter
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.ui.dialog.CalendarDialog
import com.cyxbsmobile_single.module_todo.ui.dialog.DetailAlarmDialog
import com.cyxbsmobile_single.module_todo.ui.dialog.SelectRepeatDialog
import com.cyxbsmobile_single.module_todo.util.transformRepeat
import com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel
import com.google.gson.Gson
import com.mredrock.cyxbs.config.route.TODO_TODO_DETAIL
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.toastWithYOffset

/**
 * description:
 * author: sanhuzhen
 * date: 2024/8/20 17:31
 */
@Route(path = TODO_TODO_DETAIL)
class TodoDetailActivity : BaseActivity() {
    lateinit var todo: Todo
    private val repeatTimeAdapter by lazy { RepeatTimeRvAdapter(1) }
    private var SelectRepeatTimeList = ArrayList<String>()
    private val viewModel by viewModels<TodoViewModel>()

    private val edRemark by R.id.todo_inner_detail_remark_ed.view<AppCompatEditText>()
    private val tvRemark by R.id.todo_inner_detail_remark_tv.view<TextView>()
    private val llClassify by R.id.todo_detail_ll_classify_choose.view<LinearLayout>()
    private val btnConfirm by R.id.todo_detail_btn_confirm.view<AppCompatButton>()
    private val btnCancel by R.id.todo_detail_btn_cancel.view<AppCompatButton>()
    private val wpClassify by R.id.todo_wp_detail_category_list.view<WheelPicker>()
    private val etTitle by R.id.todo_detail_et_todo_title.view<AppCompatEditText>()
    private val tvDeadline by R.id.todo_detail_tv_deadline.view<AppCompatTextView>()
    private val tvRepeatTime by R.id.todo_tv_inner_detail_no_repeat_time.view<AppCompatTextView>()
    private val back by R.id.todo_inner_detail_back.view<TextView>()
    private val line by R.id.todo_detail_line.view<View>()
    private val tvClassify by R.id.todo_detail_tv_classify.view<TextView>()
    private val rvRepeatTime by R.id.todo_rv_inner_detail_repeat_time.view<RecyclerView>()
    private val tvSaveGrey by R.id.todo_thing_detail_no_save.view<TextView>()
    private val tvSave by R.id.todo_thing_detail_save.view<TextView>()

    companion object {
        fun startActivity(todo: Todo, context: Context) {
            context.startActivity(
                Intent(context, TodoDetailActivity::class.java).apply {
                    putExtra("todo", Gson().toJson(todo))
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //配置Window的背景颜色，使得共享动画时颜色正常
        //一定要放在onCreate之前，不然在共享动画时没有效果
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_detail)

        todo = Gson().fromJson(intent.getStringExtra("todo"), Todo::class.java)
        viewModel.rawTodo = todo

        initView()

        initClick()
    }

    private fun initView() {
        viewModel.isChanged.observe(this) {
            if (it) {
                tvSaveGrey.visibility = View.GONE
                tvSave.visibility = View.VISIBLE
            } else {
                tvSaveGrey.visibility = View.VISIBLE
                tvSave.visibility = View.GONE
            }
        }

        etTitle.setText(todo.title)
        edRemark.setText(todo.detail)
        tvClassify.text = when (todo.type) {
            "study" -> "学习"
            "life" -> "生活"
            else -> "其他"
        }
        tvDeadline.text = todo.remindMode.notifyDateTime

        val repeatMode: Int = todo.remindMode.repeatMode
        if (todo.remindMode.repeatMode == RemindMode.NONE) {
            tvRepeatTime.visibility = View.VISIBLE
        } else {
            tvRepeatTime.visibility = View.GONE
            val selectRepeatTimeList: List<String> = if (repeatMode == RemindMode.WEEK) {
                todo.remindMode.week
            } else {
                todo.remindMode.day
            }.map {
                it.toString()
            }
            SelectRepeatTimeList = transformRepeat(selectRepeatTimeList, repeatMode)

            repeatTimeAdapter.submitList(SelectRepeatTimeList)
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
                    if (SelectRepeatTimeList.isEmpty()) {
                        rvRepeatTime.visibility = View.INVISIBLE
                        tvRepeatTime.visibility = View.VISIBLE
                        viewModel.setChangeState(true)
                        todo.remindMode.repeatMode = RemindMode.NONE
                    }
                }
            }
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun initClick() {
        etTitle.addTextChangedListener {
            viewModel.setChangeState(it.toString() != viewModel.rawTodo?.title)
        }

        back.setOnClickListener {
            if (viewModel.isChanged.value == true) {
                DetailAlarmDialog.Builder(this)
                    .setPositiveClick {
                        finish()
                    }.setNegativeClick {
                        dismiss()
                    }.show()
            } else {
                finish()
            }
        }

        tvDeadline.setOnClickListener {
            CalendarDialog(this) { year, month, day, hour, minute ->
                tvDeadline.apply {
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
                    setTextColor(getColor(com.mredrock.cyxbs.config.R.color.config_level_two_font_color))
                    viewModel.setChangeState(tvDeadline.text != viewModel.rawTodo?.remindMode?.notifyDateTime)
                }
            }.show()
        }

        tvRepeatTime.setOnClickListener {
            SelectRepeatDialog(this) { selectRepeatTimeListIndex, selectRepeatTimeList, repeatMode ->
                todo.remindMode.repeatMode = repeatMode

                if (repeatMode == RemindMode.WEEK) {
                    todo.remindMode.week = selectRepeatTimeListIndex as ArrayList<Int>
                } else {
                    todo.remindMode.day = selectRepeatTimeListIndex as ArrayList<Int>
                }
                viewModel.setChangeState(SelectRepeatTimeList != selectRepeatTimeList as ArrayList<String>)
                SelectRepeatTimeList = selectRepeatTimeList

                repeatTimeAdapter.submitList(SelectRepeatTimeList)
                if (SelectRepeatTimeList.isNotEmpty()) {
                    rvRepeatTime.visibility = View.VISIBLE
                    tvRepeatTime.visibility = View.INVISIBLE
                }
            }.show()
        }

        tvSave.setOnClickListener {
            //如果没输入标题，就ban掉
            if (etTitle.text.toString().isEmpty()) {
                "掌友，标题不能为空哦".toastWithYOffset(67)
                return@setOnClickListener
            }
            todo.title = etTitle.text.toString()
            todo.detail = edRemark.text.toString()
            todo.lastModifyTime = System.currentTimeMillis()
            viewModel.updateTodo(todo)
            viewModel.judgeChange(todo)
        }

        tvClassify.setOnClickListener {
            wpClassify.data = listOf("学习", "生活", "其他")
            edRemark.visibility = View.GONE
            tvRemark.visibility = View.GONE
            llClassify.visibility = View.VISIBLE
        }

        btnConfirm.setOnClickListener {
            tvClassify.apply {
                text = when (wpClassify.currentItemPosition) {
                    0 -> "学习"
                    1 -> "生活"
                    else -> "其他"
                }
            }
            todo.type = when (tvClassify.text) {
                "学习" -> "study"
                "生活" -> "life"
                else -> "other"
            }
            viewModel.setChangeState(tvClassify.text != viewModel.rawTodo?.type)

            hideClassify()
        }

        btnCancel.setOnClickListener {
            hideClassify()
        }

        edRemark.addTextChangedListener {
            viewModel.setChangeState(it.toString() != viewModel.rawTodo?.detail)
            if (it != null) {
                if (it.length == 100) {
                    "已超100字，无法再输入".toastWithYOffset(line.top)
                }
            }
        }
    }

    private fun hideClassify() {
        edRemark.visibility = View.VISIBLE
        tvRemark.visibility = View.VISIBLE
        llClassify.visibility = View.GONE
    }
}