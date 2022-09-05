package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
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
 * @Description:    搜索完成返回的弹窗
 */
class SearchDoneDialog(context: Context) : AlertDialog(context) {
  
  /**
   * 退出按钮回调
   */
  private var mOnReturnClick : ((SearchDoneDialog) -> Unit)? = null
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.noclass_dialog_search_done)
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    setCanceledOnTouchOutside(false)
    initView()
  }
  
  private fun initView(){
    
    findViewById<Button>(R.id.btn_noclass_dialog_search_done_back).apply {
      setOnSingleClickListener {
        cancel()
      }
    }
    
    findViewById<Button>(R.id.btn_noclass_dialog_search_done_continue).apply {
      setOnSingleClickListener {
        mOnReturnClick?.invoke(this@SearchDoneDialog)
      }
    }
    
  }
  
  fun setOnReturnClick(onReturnClick : (SearchDoneDialog) -> Unit) : SearchDoneDialog{
    return this.apply {
      mOnReturnClick = onReturnClick
    }
  }
  
}