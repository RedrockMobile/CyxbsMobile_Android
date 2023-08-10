package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.postDelayed
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.page.viewmodel.activity.GroupManagerViewModel
import com.mredrock.cyxbs.noclass.util.startShake

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui
 * @ClassName:      CreateGroupDialogFragment
 * @Author:         Yan
 * @CreateDate:     2022年08月15日 17:23:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    创建新分组的bottom sheet dial og
 */
class CreateGroupDialog() : BottomSheetDialogFragment() {
  
  private val viewModel by activityViewModels<GroupManagerViewModel>()
  
  private var mExtraNums : String? = null
  
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    super.onCreateDialog(savedInstanceState)
    val dialog = super.onCreateDialog(savedInstanceState)
    dialog.setContentView(R.layout.noclass_dialog_create_group)
    initView(dialog)
    return dialog
  }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.noclass_sheet_dialog_style)
  }
  
  private fun initView(dialog: Dialog) {
    //分组名称textview
    val tvName = dialog.findViewById<TextView>(R.id.tv_noclass_create_group_name)
    //创建分组名称dialog
    val etName = dialog.findViewById<EditText>(R.id.et_noclass_create_group)
    //创建完成按钮上方的提示
    val tvHint = dialog.findViewById<TextView>(R.id.tv_noclass_create_group_hint).apply {
      visibility = View.INVISIBLE
    }
    //取消textview
    dialog.findViewById<TextView>(R.id.tv_noclass_create_group_cancel).apply {
      setOnClickListener {
        dialog.cancel()
      }
    }
    
    //TextWatcher监听
    etName.addTextChangedListener(
      onTextChanged = { s, _, before, _ ->
        if (s?.length == 0) {
          etName.gravity = Gravity.NO_GRAVITY
          tvName.visibility = View.VISIBLE
          tvHint.text = "请输入你的分组名称"
          tvHint.visibility = View.VISIBLE
        } else if (s?.length == 12) {
          etName.gravity = Gravity.CENTER
          tvName.visibility = View.GONE
          tvHint.postDelayed(3000) {
            tvHint.visibility = View.INVISIBLE
          }
          tvHint.text = "分组名称不能超过12个字符"
          tvHint.visibility = View.VISIBLE
        } else {
          etName.gravity = Gravity.CENTER
          tvName.visibility = View.GONE
          tvHint.visibility = View.INVISIBLE
        }
      }
    )
    
    //创建完成的按钮
    dialog.findViewById<Button>(R.id.btn_noclass_group_create_done).apply {
      setOnSingleClickListener {
        if (etName.text.isEmpty()) {
          createUndone(tvHint)
          tvHint.text = "请输入你的分组名称"
          tvHint.visibility = View.VISIBLE
        } else {
          createDone(etName.text.toString(), tvHint)
        }
      }
    }
  }
  
  /**
   * 创建成功
   */
  private fun createDone(name: String, tvHint: TextView) {
    //todo 等待网络请求到existsName
    val existsName = ArrayList<String>()
    for (i in existsName) { //判断是否有重名
      if (i == name) {
        tvHint.text = "和已有分组重名，再想想吧"
        tvHint.visibility = View.VISIBLE
        createUndone(tvHint)
        return
      }
    }
    viewModel.postNoclassGroup(name, mExtraNums ?: "")
    viewModel.isCreateSuccess.observe(this) {
      if (it.id == -1) {
        toast("似乎出现了什么问题呢,请稍后再试")
      } else {
        toast("创建成功")
        dialog?.cancel()
      }
    }
  }
  
  /**
   * 创建失败
   */
  private fun createUndone(textView: TextView) {
    textView.startShake()
  }
  
  fun setExtraNumbers(nums : String){
    mExtraNums = nums
  }
  
  
}