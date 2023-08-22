package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.api.affair.DateJson
import com.mredrock.cyxbs.api.affair.IAffairService
import com.mredrock.cyxbs.api.affair.NoClassBean
import com.mredrock.cyxbs.lib.utils.adapter.FragmentVpAdapter
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.page.ui.fragment.NoClassBusyPageFragment
import com.mredrock.cyxbs.noclass.page.viewmodel.activity.NoClassViewModel
import com.mredrock.cyxbs.noclass.widget.MyVpIndicatorView

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
  private val mDateJson: DateJson,
  private val mNumNameIsSpare : HashMap<Pair<String,String>,Boolean>,
  private val mTextTime : String
) : BottomSheetDialogFragment() {

  private val mAdapter by lazy { FragmentVpAdapter(this) }

  private val mParentViewModel by activityViewModels<NoClassViewModel>()

  private lateinit var mViewPager2 : ViewPager2

  private lateinit var mIndicator : MyVpIndicatorView

  private var indicatorNum : Int = 1
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.noclass_sheet_dialog_style)
    initObserve()
  }

  private fun initObserve() {
    // 记录上一次的list的size，方便比较
    var beforeSize = mNumNameIsSpare.size
    mParentViewModel.busyNameList.observe(this){
      // 如果不等，就说明页面已经满了，回调
      if (it.size != beforeSize){
        beforeSize = it.size
        mAdapter.add(NoClassBusyPageFragment::class.java)
        indicatorNum++
        Log.d("lx", "indicatorNum:${indicatorNum} ")
        mIndicator.setAllDot(indicatorNum)
      }
    }
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    super.onCreateDialog(savedInstanceState)
    val dialog = super.onCreateDialog(savedInstanceState)
    dialog.setContentView(R.layout.noclass_dialog_noclass_gather)
    initView(dialog)
    return dialog
  }
  
  private fun initView(dialog: Dialog){
    dialog.findViewById<TextView>(R.id.noclass_tv_gathering_total).apply {
      val describe = "人数：共计 ${mNumNameIsSpare.size} 人"
      text = describe
    }
    dialog.findViewById<TextView>(R.id.noclass_tv_gathering_busy_total).apply {
      val busyNum = "忙碌${mNumNameIsSpare.filter { !it.value }.size}人"
      text = busyNum
    }
    dialog.findViewById<TextView>(R.id.noclass_tv_gathering_time).apply {
      text = mTextTime
    }
    //安排行程
    dialog.findViewById<Button>(R.id.noclass_btn_arrange_plan).apply {
      setOnClickListener {
        //跳转到安排行程模块
        val idIsSparePair = ArrayList<Pair<String,Boolean>>()
        mNumNameIsSpare.forEach { (idName, isSpare) ->
          idIsSparePair.add(idName.first to isSpare)
        }
        IAffairService::class.impl.startActivityForNoClass(NoClassBean(idIsSparePair,mDateJson))
      }
    }
    mIndicator = dialog.findViewById(R.id.noclass_indicator_gathering)
    mViewPager2 = dialog.findViewById<ViewPager2>(R.id.noclass_vp_gather_container).apply {
      // 忙碌的人名
      val busyNames = mNumNameIsSpare.filter { !it.value }.map { it.key }.map { it.second }
      mParentViewModel.setBusyNameList(busyNames)
      adapter = mAdapter.add(NoClassBusyPageFragment::class.java)
    }
    mViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
      override fun onPageSelected(position: Int) {
        mIndicator.setCurrentDot(position)
        super.onPageSelected(position)
      }
    })
  }
}