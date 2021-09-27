package com.cyxbsmobile_single.module_todo.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.adapter.RepeatInnerAdapter
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog
import com.cyxbsmobile_single.module_todo.util.remindMode2RemindList
import com.cyxbsmobile_single.module_todo.util.setMargin
import com.cyxbsmobile_single.module_todo.viewmodel.TodoDetailViewModel
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.dip
import com.mredrock.cyxbs.common.utils.extensions.toast
import kotlinx.android.synthetic.main.todo_activity_inner_detail.*
import kotlinx.android.synthetic.main.todo_inner_add_thing_dialog.*

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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_inner_detail)
        todo = Gson().fromJson(intent.getStringExtra("todo"), Todo::class.java)
        //这里反序列化两次是为了防止内外拿到同一个引用
        viewModel.rawTodo = Gson().fromJson(intent.getStringExtra("todo"), Todo::class.java)

        LogUtils.d("RayleighZ", " todo = $todo")

        initView()

        initClick()
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
        todo_inner_detail_header.setOnClickListener {
            finish()
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
            //防止暴击，在todo更新成功之间不允许再一次点击
            it.isClickable = false
            viewModel.updateTodo(todo) {
                setCheckedStatus()
                it.isClickable = true
            }
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