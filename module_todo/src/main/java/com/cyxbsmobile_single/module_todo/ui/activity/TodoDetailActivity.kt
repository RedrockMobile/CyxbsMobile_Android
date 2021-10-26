package com.cyxbsmobile_single.module_todo.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.RepeatInnerAdapter
import com.cyxbsmobile_single.module_todo.model.TodoModel
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog
import com.cyxbsmobile_single.module_todo.util.remindMode2RemindList
import com.cyxbsmobile_single.module_todo.viewmodel.TodoDetailViewModel
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.TODO_TODO_DETAIL
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.toast
import kotlinx.android.synthetic.main.todo_activity_inner_detail.*

@Route(path = TODO_TODO_DETAIL)
class TodoDetailActivity : BaseViewModelActivity<TodoDetailViewModel>() {

    lateinit var todo: Todo
    private lateinit var repeatAdapter: RepeatInnerAdapter
    private var backTime = 2

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
        fun initTodo(){
            //这里反序列化两次是为了防止内外拿到同一个引用
            viewModel.rawTodo = Gson().fromJson(intent.getStringExtra("todo"), Todo::class.java)

            initView()

            initClick()
        }
        if (intent.getBooleanExtra("is_from_receive", false)) {
            //如果来自端内跳转, 则重新加载todo
            val todoId = intent.getStringExtra("todo_id").toString().toLong()
            if (todoId <= 0){
                BaseApp.context.toast("没有这条代办的信息哦")
                finish()
            }
            TodoModel.INSTANCE.getTodoById(todoId,
                    onSuccess = {
                        todo = it
                        initTodo()
                    },
                    onError = {
                        BaseApp.context.toast("没有这条代办的信息哦")
                    }
            )
        } else {
            todo = Gson().fromJson(intent.getStringExtra("todo"), Todo::class.java)

            initTodo()
        }
    }

    private fun initView() {
        todo_tv_todo_title.setText(todo.title)
        todo_tv_todo_title.addTextChangedListener {
            if (it.toString() != todo.title) {
                //判定为做出了修改
                backTime = 2
                todo.title = it.toString()
                changeModifyStatus(true)
            }
        }
        todo_inner_detail_remark_ed.setText(todo.detail)
        setCheckedStatus()

        if (todo.remindMode.notifyDateTime == "") {
            todo_tv_inner_detail_time.hint = "设置提醒时间"
        } else {
            todo_tv_inner_detail_time.text = todo.remindMode.notifyDateTime
        }
    }

    private fun initClick() {
        todo_inner_detail_back.setOnClickListener {
            if (viewModel.isChanged) {
                backTime--
                if (backTime == 0) {
                    finish()
                } else {
                    BaseApp.context.toast("你的修改未保存")
                }
            } else {
                finish()
            }
        }

        todo_tv_inner_detail_del_todo.setOnClickListener {
            viewModel.delTodo(todo) {
                finish()
            }
        }

        todo_tv_inner_detail_time.setOnClickListener {
            backTime = 2
            AddItemDialog(context = this) {
                todo.remindMode.notifyDateTime = it.remindMode.notifyDateTime
                todo_tv_inner_detail_time.text = todo.remindMode.notifyDateTime
                changeModifyStatus()
            }.apply {
                setAsSinglePicker(AddItemDialog.CurOperate.NOTIFY)
                showNotifyDatePicker()
                //此方法应当在show之后执行，不然的话rv加载不出来
                resetNotifyTime(todo)
            }.show()
        }

        repeatAdapter = RepeatInnerAdapter(ArrayList(remindMode2RemindList(todo.remindMode))) {
            backTime = 2
            AddItemDialog(context = this) {
                repeatAdapter.resetAll(remindMode2RemindList(it.remindMode))
                todo.remindMode = it.remindMode
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

        todo_thing_detail_save.setOnClickListener {
            todo.detail = todo_inner_detail_remark_ed.text.toString()
            todo.lastModifyTime = System.currentTimeMillis()
            viewModel.updateTodo(todo) {
                finish()
            }
        }

        todo_inner_detail_remark_ed.addTextChangedListener {
            backTime = 2
            changeModifyStatus(it.toString() != viewModel.rawTodo?.detail)
        }

        todo_iv_todo_item.setOnClickListener {
            //改变todo的check状态
            todo.isChecked = 1 - todo.isChecked
            changeModifyStatus()
            backTime = 2
            setCheckedStatus()
        }
    }

    private fun changeModifyStatus() {
        viewModel.judgeChange(todo)
        todo_thing_detail_save.visibility =
                if (viewModel.isChanged) View.VISIBLE
                else View.GONE

    }

    private fun changeModifyStatus(isChanged: Boolean) {
        viewModel.isChanged = isChanged
        todo_thing_detail_save.visibility =
                if (viewModel.isChanged) View.VISIBLE
                else View.GONE
    }

    override fun onBackPressed() {
        if (viewModel.isChanged) {
            backTime--
            if (backTime == 0) {
                super.onBackPressed()
            } else {
                BaseApp.context.toast("你的修改未保存")
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun setCheckedStatus() {
        todo_iv_check.visibility = if (todo.isChecked == 1) View.VISIBLE else View.GONE
        todo_tv_todo_title.setTextColor(
                if (todo.isChecked == 1) ContextCompat.getColor(this, R.color.todo_item_checked_color)
                else ContextCompat.getColor(this, R.color.todo_check_line_color)
        )
    }
}