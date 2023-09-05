package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
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
class SameNameSelectionDialog : BottomSheetDialogFragment {
    private val sameNameData: List<Student>
    constructor(sameNameData: List<Student>) : super() {
        this.sameNameData = sameNameData
    }
    // 无参构造器是为了给 Activity重建（如配置更新时、夜间模式等）后恢复 该BottomSheetDialogFragment用（防止APP崩溃）
    constructor() : super() {
        this.sameNameData = emptyList()
    }

    // 批量添加页面activity的viewModel
    private val batchAdditionViewModel by activityViewModels<BatchAdditionViewModel>()

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
        // 一般认为调用无参构造器时sameNameData才为空，此时默认为fragment恢复，则该dialog可取消
        // 或者按逻辑讲如果有参构造器传入的也是空列表，那么该dialog也没必要出现
        if (sameNameData.isEmpty()) dialog?.cancel()
        if (dialog is BottomSheetDialog) {
            val behaviour = (dialog as BottomSheetDialog).behavior
            behaviour.isDraggable=false
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
        // 禁用点击空白处取消
        dialog.setCancelable(false)

        dialog.findViewById<RecyclerView>(R.id.noclass_batch_rv_dialog_students_container).apply {
            adapter = SameNameStudentsAdapter().apply {
                submitList(sameNameData)
            }
            layoutManager = LinearLayoutManager(context)
        }
        dialog.findViewById<Button>(R.id.noclass_batch_btn_confirm_selected_students).apply {
            setOnClickListener {
                val targetList = mutableListOf<Pair<String,String>>() // 学号-姓名 组合对的list
                sameNameData.forEach {
                    if (it.isSelected){  // 该重名学生被选中了
                        targetList.add(Pair(it.id, it.name))
                    }
                }
                batchAdditionViewModel.setSelectedStudents(targetList)
                dialog.cancel()
            }
        }
    }
}