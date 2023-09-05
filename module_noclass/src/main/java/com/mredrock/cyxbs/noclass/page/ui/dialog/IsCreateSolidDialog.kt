package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.noclass.R

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui.dialog
 * @ClassName:      SearchDoneDialog
 * @Author:         Yan
 * @CreateDate:     2022年09月05日 19:15:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    查询完成时候课表页面的弹窗,是否创建固定分组，是就进入创建界面，否就弹窗消失
 */
class IsCreateSolidDialog(context: Context) : AlertDialog(context) {
  
  /**
   * 退出按钮回调
   */
  private var mOnReturnClick : ((IsCreateSolidDialog, Boolean) -> Unit)? = null
  
  /**
   * 继续按钮的回调
   */
  private var mOnContinue : ((IsCreateSolidDialog, Boolean) -> Unit)? = null
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.noclass_dialog_search_done)
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    setCanceledOnTouchOutside(false)
    initView()
  }
  
  private fun initView(){
    
    val checkBox = findViewById<CheckBox>(R.id.noclass_cb_remind_next)
    
    findViewById<Button>(R.id.btn_noclass_dialog_search_done_back).apply {
      setOnSingleClickListener {
        mOnReturnClick?.invoke(this@IsCreateSolidDialog,checkBox.isChecked)
      }
    }
    
    findViewById<Button>(R.id.btn_noclass_dialog_search_done_continue).apply {
      setOnSingleClickListener {
        mOnContinue?.invoke(this@IsCreateSolidDialog,checkBox.isChecked)
      }
    }
    
  }
  
  fun setOnReturnClick(onReturnClick : (IsCreateSolidDialog, Boolean) -> Unit) : IsCreateSolidDialog{
    return this.apply {
      mOnReturnClick = onReturnClick
    }
  }
  
  fun setOnContinueClick(onContinue : (IsCreateSolidDialog, Boolean) -> Unit) : IsCreateSolidDialog{
    return this.apply {
      mOnContinue = onContinue
    }
  }
  
}