package com.cyxbsmobile_single.module_todo.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.RepeatInnerAdapter
import com.cyxbsmobile_single.module_todo.model.TodoModel
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog
import com.cyxbsmobile_single.module_todo.ui.dialog.DetailAlarmDialog
import com.cyxbsmobile_single.module_todo.ui.widget.TodoWidget
import com.cyxbsmobile_single.module_todo.util.remindMode2RemindList
import com.cyxbsmobile_single.module_todo.viewmodel.TodoDetailViewModel
import com.google.gson.Gson
import com.mredrock.cyxbs.common.config.TODO_TODO_DETAIL
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.extensions.toastWithYOffset


@Route(path = TODO_TODO_DETAIL)
class TodoDetailActivity : BaseViewModelActivity<TodoDetailViewModel>() {

    lateinit var todo: Todo
    private lateinit var repeatAdapter: RepeatInnerAdapter

    private val todo_inner_detail_remark_ed by R.id.todo_inner_detail_remark_ed.view<AppCompatEditText>()
    private val todo_detail_et_todo_title by R.id.todo_detail_et_todo_title.view<AppCompatEditText>()
    private val todo_detail_tv_deadline by R.id.todo_detail_tv_deadline.view<AppCompatTextView>()
    private val todo_inner_detail_back by R.id.todo_inner_detail_back.view<TextView>()
    private val todo_detail_line by R.id.todo_detail_line.view<View>()
    private val todo_detail_tv_classify by R.id.todo_detail_tv_classify.view<TextView>()
    private val todo_rv_inner_detail_repeat_time by R.id.todo_rv_inner_detail_repeat_time.view<RecyclerView>()
    private val todo_thing_detail_no_save by R.id.todo_thing_detail_no_save.view<TextView>()
    private val todo_thing_detail_save by R.id.todo_thing_detail_save.view<TextView>()

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
        setContentView(R.layout.todo_activity_inner_detail)
        //下面的逻辑是为了处理端内跳转
        fun initTodo() {
            //这里反序列化两次是为了防止内外拿到同一个引用
            viewModel.rawTodo = Gson().fromJson(intent.getStringExtra("todo"), Todo::class.java)

            initView()

            initClick()
        }
        if (intent.getBooleanExtra("is_from_receive", false)) {
            //如果来自端内跳转, 则重新加载todo
            val todoId = intent.getStringExtra("todo_id").toString().toLong()
            if (todoId <= 0) {
                "没有这条代办的信息哦".toast()
                finish()
            }
            TodoModel.INSTANCE.getTodoById(todoId,
                onSuccess = {
                    todo = it
                    initTodo()
                },
                onError = {
                    "没有这条代办的信息哦".toast()
                }
            )
        } else {
            todo = Gson().fromJson(intent.getStringExtra("todo"), Todo::class.java)

            initTodo()
        }
    }

    private fun initView() {
        todo_detail_et_todo_title.setText(todo.title)
        todo_detail_et_todo_title.addTextChangedListener {
            if (it.toString() != todo.title) {
                //判定为做出了修改
                todo.title = it.toString()
                changeModifyStatus(true)
            }
        }
        todo_inner_detail_remark_ed.setText(todo.detail)

        if (todo.remindMode.notifyDateTime == "") {
            todo_detail_tv_deadline.hint = "设置提醒时间"
        } else {
            todo_detail_tv_deadline.text = todo.remindMode.notifyDateTime
        }
    }

    private fun initClick() {
        todo_inner_detail_back.setOnClickListener {
            if (viewModel.isChanged) {
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

        todo_detail_tv_deadline.setOnClickListener {
            AddItemDialog(context = this) { todo ->
                todo.remindMode.notifyDateTime = todo.remindMode.notifyDateTime
                todo_detail_tv_deadline.text = todo.remindMode.notifyDateTime
                changeModifyStatus()
            }.apply {
                setAsSinglePicker(AddItemDialog.CurOperate.NOTIFY)
                showNotifyDatePicker()
                //此方法应当在show之后执行，不然的话rv加载不出来
                resetNotifyTime(todo)
            }.show()
        }

        repeatAdapter = RepeatInnerAdapter(ArrayList(remindMode2RemindList(todo.remindMode))) {
            AddItemDialog(context = this) { todo ->
                repeatAdapter.resetAll(remindMode2RemindList(todo.remindMode))
                todo.remindMode = todo.remindMode
                changeModifyStatus()
            }.apply {
                setAsSinglePicker(AddItemDialog.CurOperate.REPEAT)
                showRepeatDatePicker()
                //此方法应当在show之后执行，不然的话rv加载不出来
                resetAllRepeatMode(todo)
            }.show()
        }

        todo_rv_inner_detail_repeat_time.adapter = repeatAdapter
        todo_rv_inner_detail_repeat_time.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }

        todo_detail_tv_classify.setOnClickListener {
            
        }

        todo_thing_detail_save.setOnClickListener {
            //如果没输入标题，就ban掉
            if (todo_detail_et_todo_title.text.toString().isEmpty()) {
                "掌友，标题不能为空哦".toastWithYOffset(67)
                return@setOnClickListener
            }
            todo.detail = todo_inner_detail_remark_ed.text.toString()
            todo.lastModifyTime = System.currentTimeMillis()
            viewModel.updateTodo(todo) {
                this.sendBroadcast(
                    Intent("cyxbs.widget.todo.refresh").apply {
                        component = ComponentName(this@TodoDetailActivity, TodoWidget::class.java)
                    }
                )
                finish()
            }
        }

        todo_inner_detail_remark_ed.apply {
            addTextChangedListener {
                changeModifyStatus(it.toString() != viewModel.rawTodo?.detail)
                if (it != null) {
                    if (it.length == 100) {
                        "已超100字，无法再输入".toastWithYOffset(todo_detail_line.top)
                    }
                }
            }
        }
    }

    private fun changeModifyStatus() {
        viewModel.judgeChange(todo)
        if (viewModel.isChanged) {
            todo_thing_detail_no_save.visibility = View.GONE
            todo_thing_detail_save.visibility = View.VISIBLE
        } else {
            todo_thing_detail_save.visibility = View.GONE
            todo_thing_detail_no_save.visibility = View.VISIBLE
        }
    }

    private fun changeModifyStatus(isChanged: Boolean) {
        viewModel.isChanged = isChanged
        if (viewModel.isChanged) {
            todo_thing_detail_no_save.visibility = View.GONE
            todo_thing_detail_save.visibility = View.VISIBLE
        } else {
            todo_thing_detail_save.visibility = View.GONE
            todo_thing_detail_no_save.visibility = View.VISIBLE
        }
    }
}