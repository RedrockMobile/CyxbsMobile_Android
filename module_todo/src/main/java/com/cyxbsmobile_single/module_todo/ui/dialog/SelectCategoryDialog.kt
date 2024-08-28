package com.cyxbsmobile_single.module_todo.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatButton
import com.aigestudio.wheelpicker.WheelPicker
import com.cyxbsmobile_single.module_todo.R
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * author : zeq
 * email : 1301731619@qq.com
 * date : 2024/8/28 20:20
 */
class SelectCategoryDialog(
    context: Context,
    style: Int,
    val selectRepeat: (String) -> Unit
) :
    BottomSheetDialog(context, style) {
    private var mCategory = "其他"

    private val wpCategory by lazy { findViewById<WheelPicker>(R.id.todo_wp_detail_category_list)!! }
    private val btnConfirm by lazy { findViewById<AppCompatButton>(R.id.todo_detail_btn_confirm)!! }
    private val btnCancel by lazy { findViewById<AppCompatButton>(R.id.todo_detail_btn_cancel)!! }

    init {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.todo_dialog_bottom_sheet_category, null, false)
        setContentView(dialogView)

        dialogView?.apply {
            initClick()
            wpCategory.data = listOf("学习", "生活", "其他")
        }
    }

    private fun initClick() {
        btnConfirm.setOnClickListener {
            mCategory = when (wpCategory.currentItemPosition) {
                0 -> "学习"
                1 -> "生活"
                else -> "其他"
            }
            selectRepeat(mCategory)
            dismiss()
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
    }

}
