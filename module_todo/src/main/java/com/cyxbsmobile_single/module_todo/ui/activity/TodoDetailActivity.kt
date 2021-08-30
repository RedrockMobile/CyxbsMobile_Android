package com.cyxbsmobile_single.module_todo.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
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
    private lateinit var repeatAdapter :RepeatInnerAdapter
    private var backTime = 2

    companion object{
        fun startActivity(todo: Todo, context: Context){
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

        todo = Gson().fromJson(intent.getStringExtra("todo"),Todo::class.java)
        //这里反序列化两次是为了防止内外拿到同一个引用
        viewModel.rawTodo = Gson().fromJson(intent.getStringExtra("todo"),Todo::class.java)

        initView()

        initClick()
    }

    private fun initView(){
        todo_tv_todo_title.setText(todo.title)
        todo_tv_todo_title.addTextChangedListener{
            if (it.toString() != todo.title){
                //判定为做出了修改
                backTime = 2
                todo.title = it.toString()
                changeModifyStatus(true)
            }
        }
        todo_inner_detail_remark_ed.setText(todo.detail)
        setCheckedStatus()

        if (todo.remindMode.notifyDateTime == ""){
            todo_tv_inner_detail_time.hint = "设置提醒时间"
        } else {
            todo_tv_inner_detail_time.text = todo.remindMode.notifyDateTime
        }
    }

    private fun initClick(){
        todo_tv_inner_detail_time.setOnClickListener {
            backTime = 2
            AddItemDialog(context = this){
                todo.remindMode.notifyDateTime = it.remindMode.notifyDateTime
                todo_tv_inner_detail_time.text = todo.remindMode.notifyDateTime
                changeModifyStatus()
            }.apply {
                setAsSinglePicker()
                todo_tv_set_repeat_time.text = "设置提醒时间"
                setMargin(todo_tv_set_repeat_time, left = BaseApp.context.dip(15))
                showNotifyDatePicker()
                resetNotifyTime(todo)
            }.show()
        }

        repeatAdapter = RepeatInnerAdapter(ArrayList(remindMode2RemindList(todo.remindMode))){
            backTime = 2
            AddItemDialog(context = this){
                repeatAdapter.resetAll(remindMode2RemindList(it.remindMode))
                LogUtils.d("ZhangYu", "$it")
                todo.remindMode = it.remindMode
                changeModifyStatus()
            }.apply {
                setAsSinglePicker()
                todo_tv_set_repeat_time.text = "设置重复提醒"
                setMargin(todo_tv_set_repeat_time, left = BaseApp.context.dip(15))
                showRepeatModePicker()
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
            viewModel.updateTodo(todo){
                finish()
            }
        }

        todo_inner_detail_remark_ed.addTextChangedListener {
            backTime = 2
            changeModifyStatus(it.toString() != viewModel.rawTodo?.detail)
        }

        todo_iv_todo_item.setOnClickListener {
            //改变todo的check状态
            todo.isChecked = !todo.isChecked
            changeModifyStatus()
            //防止暴击，在todo更新成功之间不允许再一次点击
            it.isClickable = false
            viewModel.updateTodo(todo){
                setCheckedStatus()
                it.isClickable = true
            }
        }
    }

    private fun changeModifyStatus(){
        viewModel.judgeChange(todo)
        if (viewModel.isChanged) todo_thing_detail_save.setTextColor(Color.parseColor("#2923D2"))
        else todo_thing_detail_save.setTextColor(Color.parseColor("#4015315B"))

        LogUtils.d("RayleighZ", "${viewModel.rawTodo?.toString()}  $todo")
    }

    private fun changeModifyStatus(isChanged: Boolean){
        viewModel.isChanged = isChanged
        if (viewModel.isChanged) todo_thing_detail_save.setTextColor(Color.parseColor("#2923D2"))
        else todo_thing_detail_save.setTextColor(Color.parseColor("#4015315B"))

        LogUtils.d("RayleighZ", "${viewModel.isChanged}")
    }

    override fun onBackPressed() {
        if (viewModel.isChanged){
            backTime --
            if (backTime == 0){
                super.onBackPressed()
            } else {
                BaseApp.context.toast("你的修改未保存")
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun setCheckedStatus(){
        todo_iv_check.visibility = if(todo.isChecked) View.VISIBLE else View.GONE
    }
}