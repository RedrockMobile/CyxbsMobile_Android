package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.page.adapter.NoClassGatheringAdapter

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui.dialog
 * @ClassName:      NoClassGatherDialog
 * @Author:         Yan
 * @CreateDate:     2022年09月10日 18:17:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */
class NoClassGatherDialog (
  private val mStuMap : Map<String,Boolean>,
  private val mTextTime : String
) : BottomSheetDialogFragment() {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.noclass_sheet_dialog_style)
  }
  
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    super.onCreateDialog(savedInstanceState)
    val dialog = super.onCreateDialog(savedInstanceState)
    dialog.setContentView(R.layout.noclass_dialog_noclass_gather)
    initView(dialog)
    return dialog
  }
  
  private fun initView(dialog: Dialog){
    SchoolCalendarUtil
    val mTvTotal = dialog.findViewById<TextView>(R.id.noclass_tv_gathering_total).apply {
      text = "人数：共计 ${mStuMap.size} 人"
    }
    val mTvTime = dialog.findViewById<TextView>(R.id.noclass_tv_gathering_time).apply {
      text = mTextTime
    }
    val mViewPager2 = dialog.findViewById<ViewPager2>(R.id.noclass_vp_gather_container).apply {
      var index = 0
      val mMap = hashMapOf<Int,HashMap<String,Boolean>>()
      mStuMap.forEach {
        if (mMap[index] == null){
          mMap[index] = hashMapOf()
        }
        mMap[index]!![it.key] = it.value
        if (mMap[index]!!.size == 8){
          index++
        }
      }
      adapter = NoClassGatheringAdapter(context,mMap)
    }
  }
  
  
}