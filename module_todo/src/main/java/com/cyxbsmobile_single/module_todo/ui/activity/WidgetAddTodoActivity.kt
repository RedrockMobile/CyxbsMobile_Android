package com.cyxbsmobile_single.module_todo.ui.activity

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.TodoModel
import com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog
import com.cyxbsmobile_single.module_todo.ui.widget.TodoWidget
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.TODO_ADD_TODO_BY_WIDGET

//这里不使用BaseActivity的原因是需要设置成透明的theme
@Route(path = TODO_ADD_TODO_BY_WIDGET)
class WidgetAddTodoActivity : AppCompatActivity() {

    lateinit var dialog: AddItemDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_widget_add_todo)
        dialog = AddItemDialog(this) { todo ->
            TodoModel.INSTANCE
                .addTodo(todo) {
                    //通知小组件更新数据
                    this.sendBroadcast(
                        Intent("cyxbs.widget.todo.refresh").apply {
                            component = ComponentName(BaseApp.appContext, TodoWidget::class.java)
                        }
                    )
                    finish()
                }
        }.apply {
            //点击外部不允许hide
            setCanceledOnTouchOutside(false)
            //禁止用户手动拖拽关闭
            setCancelable(false)
            setOnKeyListener { _, keyCode, event ->
                val needDone = keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN
                if (needDone) {
                    hide()
                    finish()
                }
                needDone
            }
        }
        dialog.show()
    }

    override fun onBackPressed() {
        dialog.hide()
        finish()
        super.onBackPressed()
    }
}