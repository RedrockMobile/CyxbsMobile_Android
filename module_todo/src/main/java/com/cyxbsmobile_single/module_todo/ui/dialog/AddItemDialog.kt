package com.cyxbsmobile_single.module_todo.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import kotlinx.android.synthetic.main.todo_inner_add_thing_dialog.view.*

/**
 * @date 2021-08-18
 * @author Sca RayleighZ
 * 增加todo用的Dialog
 */
class AddItemDialog( context: Context, onConfirm: (Todo) -> Unit) : RedRockBottomSheetDialog(context) {
    init {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.todo_inner_add_thing_dialog, null, false)
        setContentView(dialogView)
        dialogView?.apply {
            todo_add_thing_first
        }
    }
}