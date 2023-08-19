package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoClassBatchResponseInfo.Student
import com.mredrock.cyxbs.noclass.page.adapter.SameNameStudentsAdapter
import com.mredrock.cyxbs.noclass.page.viewmodel.activity.BatchAdditionViewModel

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/19
 * @Description:
 *
 */
class SameNameSelectionDialog(
    private val sameNameData: List<Student>
): BottomSheetDialogFragment() {

    private val batchAdditionViewModel by viewModels<BatchAdditionViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.noclass_dialog_select_samename_student)
        val mScreenWidth : Int = resources.displayMetrics.widthPixels
        val mScreenHeight : Int = resources.displayMetrics.heightPixels
        dialog.findViewById<ConstraintLayout>(R.id.noclass_batch_select_same_name_dialog_layout).apply {
            this.layoutParams.height = mScreenHeight
            this.layoutParams.width = mScreenWidth
            requestLayout()
        }

        initView(dialog)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        if (dialog is BottomSheetDialog) {
            val behaviour = (dialog as BottomSheetDialog).behavior
            behaviour.isDraggable=true
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.noclass_sheet_dialog_style)
    }

    private fun initView(dialog: Dialog) {
        // 弹窗的取消按钮
        dialog.findViewById<TextView>(R.id.noclass_batch_tv_select_student_dialog_cancel).apply {
            setOnClickListener {
                dialog.cancel()
            }
        }
        //点击空白处取消
        dialog.setCancelable(true)

        dialog.findViewById<RecyclerView>(R.id.noclass_batch_rv_dialog_students_container).apply {
            adapter = SameNameStudentsAdapter().apply {
                submitList(sameNameData)
            }
            layoutManager = LinearLayoutManager(context)
        }
        dialog.findViewById<Button>(R.id.noclass_batch_btn_confirm_selected_students).apply {
//            Log.d("ProgressTest","get there")
            setOnClickListener {
//                Log.d("ProgressTest","get there")
                val targetList = mutableListOf<Pair<String,String>>() // 学号-姓名 组合对的list
                sameNameData.forEach {
                    if (it.isSelected){  // 该重名学生被选中了
                        targetList.add(Pair(it.id, it.name))
                    }
                }
                batchAdditionViewModel.setSelectedStudents(targetList)
                dialog.setOnDismissListener(null)
                dialog.cancel()
            }
        }
    }
}