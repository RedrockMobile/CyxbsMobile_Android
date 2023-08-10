package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.adapter.SearchStudentAdapter

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui.dialog
 * @ClassName:      SearchStudentDialog
 * @Author:         Yan
 * @CreateDate:     2022年09月02日 00:29:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    寻找学生的dialog
 */
class SearchStudentDialog(
  private val students : List<Student>,
  private val onAddClick:(Student) -> Unit
) : BottomSheetDialogFragment() {
  
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    super.onCreateDialog(savedInstanceState)
    val dialog = super.onCreateDialog(savedInstanceState)
    dialog.setContentView(R.layout.noclass_dialog_search_student)
    val mScreenWidth : Int = resources.displayMetrics.widthPixels
    val mScreenHeight : Int = resources.displayMetrics.heightPixels
    dialog.findViewById<ConstraintLayout>(R.id.test_ccc).apply {
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
      behaviour.isDraggable=false
      behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }
  }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.noclass_sheet_dialog_style)
  }
  
  private fun initView(dialog : Dialog){
    dialog.findViewById<TextView>(R.id.tv_noclass_search_student_cancel).apply {
      setOnClickListener {
        dialog.cancel()
      }
    }
    dialog.findViewById<RecyclerView>(R.id.rv_noclass_search_container).apply {
      adapter = SearchStudentAdapter().apply {
        setOnAddClick {
          onAddClick(it)
          deleteStudent(it)
          dismiss()
        }
        submitList(students)
      }
      layoutManager = LinearLayoutManager(context)
    }
  }



}