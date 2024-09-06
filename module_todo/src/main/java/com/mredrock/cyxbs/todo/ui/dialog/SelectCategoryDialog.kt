package com.mredrock.cyxbs.todo.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatTextView
import com.aigestudio.wheelpicker.WheelPicker
import com.mredrock.cyxbs.todo.R
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

    private lateinit var wpCategory: WheelPicker
    private lateinit var btnConfirm: AppCompatTextView
    private lateinit var btnCancel: AppCompatTextView

    init {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.todo_dialog_bottom_sheet_category, null, false)
        setContentView(dialogView)
        initView(this)
        dialogView?.apply {
            initClick()
            wpCategory.data = listOf("学习", "生活", "其他")
        }
    }

    private fun initView(dialog: Dialog) {
        wpCategory = dialog.findViewById(R.id.todo_wp_detail_category_list)
        btnConfirm = dialog.findViewById(R.id.todo_detail_btn_confirm)
        btnCancel = dialog.findViewById(R.id.todo_detail_btn_cancel)
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
