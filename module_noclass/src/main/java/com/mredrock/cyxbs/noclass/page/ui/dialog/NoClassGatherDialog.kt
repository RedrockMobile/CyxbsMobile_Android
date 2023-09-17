package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.affair.DateJson
import com.mredrock.cyxbs.api.affair.IAffairService
import com.mredrock.cyxbs.api.affair.NoClassBean
import com.mredrock.cyxbs.lib.utils.adapter.FragmentVpAdapter
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.page.ui.fragment.NoClassBusyPageFragment
import com.mredrock.cyxbs.noclass.util.BaseBottomSheetDialogFragment
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
class NoClassGatherDialog: BaseBottomSheetDialogFragment() {

  private val mDateJson: DateJson by arguments()
  private val mNumNameIsSpare : HashMap<Pair<String,String>,Boolean> by arguments()
  private val mTextTime : String by arguments()

  private val mAdapter by lazy { FragmentVpAdapter(this) }


  private lateinit var mViewPager2 : ViewPager2

  private lateinit var mIndicator : MyVpIndicatorView


  /**
   * 点击空白处，取消当前dialog之后回调的方法
   */
  private var mClickBlankCancel : (() -> Unit)? = null

  fun setClickBlankCancel(mClickBlankCancel : () -> Unit){
    this.mClickBlankCancel = mClickBlankCancel
  }

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
    dialog.findViewById<TextView>(R.id.noclass_tv_gathering_total).apply {
      val describe = "共计${mNumNameIsSpare.size} 人"
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
        //跳转到安排行程模块,这里将自己去掉了
        val mUserId =  ServiceManager.invoke(IAccountService::class).getUserService().getStuNum()
        val idIsSparePair = ArrayList<Pair<String,Boolean>>()
        mNumNameIsSpare.forEach { (idName, isSpare) ->
          if (idName.first != mUserId){
            idIsSparePair.add(idName.first to isSpare)
          }
        }
        IAffairService::class.impl.startActivityForNoClass(NoClassBean(idIsSparePair,mDateJson))
      }
    }
    mIndicator = dialog.findViewById(R.id.noclass_indicator_gathering)
    mViewPager2 = dialog.findViewById<ViewPager2>(R.id.noclass_vp_gather_container).apply {
      offscreenPageLimit = 2
      // 忙碌的人名
      val busyNames = mNumNameIsSpare.filter { !it.value }.map { it.key }.map { it.second }
      if (busyNames.isEmpty()){
        // 这里是为了没有忙碌人员的时候不显示线和缩小dialog的高度
        gone()
        dialog.findViewById<View>(R.id.noclass_dialog_gathering_line).gone()
        val constrain = dialog.findViewById<ConstraintLayout>(R.id.noclass_gather_dialog_constraint)
        val lp = constrain.layoutParams
        lp.height = 180.dp2px
        constrain.layoutParams = lp
        requestLayout()
      }else{
        fun getFillCallback(): (List<String>) -> Unit {
          return { newBusyNames ->
            if (newBusyNames.isNotEmpty()) {
              Log.d("lx", "getFillCallback:${newBusyNames} ")
              mAdapter.add{NoClassBusyPageFragment.newInstance(newBusyNames).apply {setFillCallback {getFillCallback().invoke(it)}}}
            }
          }
        }
        adapter = mAdapter.add{NoClassBusyPageFragment.newInstance(busyNames).apply {setFillCallback{getFillCallback().invoke(it)}}}
      }
    }
    mViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
      override fun onPageSelected(position: Int) {
        mIndicator.setCurrentDot(position)
        super.onPageSelected(position)
      }
    })
  }

  override fun onDestroy() {
    super.onDestroy()
    mClickBlankCancel?.invoke()
  }

  companion object{
    fun newInstance(
      mDateJson: DateJson,
      mNumNameIsSpare : HashMap<Pair<String,String>,Boolean>,
      mTextTime : String
    ) = NoClassGatherDialog().apply {
      arguments = bundleOf(
        this::mDateJson.name to mDateJson,
        this::mNumNameIsSpare.name to mNumNameIsSpare,
        this::mTextTime.name to mTextTime
      )
    }
  }
}