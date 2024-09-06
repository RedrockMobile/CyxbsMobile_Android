package com.mredrock.cyxbs.todo.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.todo.R
import com.mredrock.cyxbs.todo.model.bean.TodoListPushWrapper
import com.mredrock.cyxbs.todo.ui.dialog.AddTodoDialog
import com.mredrock.cyxbs.todo.viewmodel.TodoViewModel
import com.mredrock.cyxbs.config.route.TODO_ADD_TODO_BY_WIDGET
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.getSp

@Route(path = TODO_ADD_TODO_BY_WIDGET)
class WidgetAddTodoActivity : BaseActivity() {

    private val mViewModel by viewModels<TodoViewModel>()
    private lateinit var dialog: AddTodoDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity_add_widget)
        dialog = AddTodoDialog(this, R.style.BottomSheetDialogThemeNight) {
            val syncTime = appContext.getSp("todo").getLong("TODO_LAST_SYNC_TIME", 0L)
            val firstPush = if (syncTime == 0L) 1 else 0
            mViewModel.apply {
                pushTodo(
                    TodoListPushWrapper(
                        listOf(it), syncTime, TodoListPushWrapper.NONE_FORCE, firstPush
                    )
                )
                isPushed.observe(this@WidgetAddTodoActivity) {
                    finish()
                }
            }

        }.apply {
            //点击外部不允许hide
            setCanceledOnTouchOutside(false)
            //禁止用户手动拖拽关闭
            setCancelable(false)
            setOnKeyListener { _, keyCode, event ->
                val needDone =
                    keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN
                if (needDone) {
                    hide()
                    finish()
                }
                needDone
            }
        }
        dialog.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        dialog.hide()
        finish()
        super.onBackPressed()
    }

}