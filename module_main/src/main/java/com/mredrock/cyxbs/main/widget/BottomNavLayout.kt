package com.mredrock.cyxbs.main.widget

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CheckedTextView
import androidx.cardview.widget.CardView
import com.mredrock.cyxbs.lib.utils.extensions.drawable
import com.mredrock.cyxbs.main.BuildConfig
import com.mredrock.cyxbs.main.R

/**
 * @author  Jovines
 * @date  2020/4/6 18:18
 * description：底部导航栏，这里没有用nav为了方便以后增加或者更改
 *
 *
 * 后期修改：
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/15 14:58
 * 从之前的一个工具类改为自定义布局，方便管理 Activity 被异常重启后还原状态
 * 因为 FrameLayout 设置 elevation 无效，所以继承 CardView
 *
 */
class BottomNavLayout(
  context: Context,
  attrs: AttributeSet?
) : CardView(context, attrs) {
  
  private val mChild = LayoutInflater.from(context).inflate(R.layout.main_bottom_nav, this)
  private val mBtnExplore = mChild.findViewById<CheckedTextView>(R.id.main_btn_nav_explore)
  private val mBtnQa = mChild.findViewById<CheckedTextView>(R.id.main_btn_nav_qa)
  private val mBtnMine = mChild.findViewById<CheckedTextView>(R.id.main_btn_nav_mine)
  
  private val mBtnList = listOf(mBtnExplore, mBtnQa, mBtnMine)
  
  private var mLastSelectPosition = -1
  
  private val mSelectListeners = arrayListOf<(Int) -> Unit>()

  init {
    cardElevation = 0F
    radius = 0F
    mBtnList.forEachIndexed { index, view ->
      view.setOnClickListener {
        selectPosition(index)
      }
    }
    
    selectPosition(0)
    
    if (BuildConfig.DEBUG) {
      // debug 下，长按第三个按钮打开友盟测试 id
      mBtnMine.setOnLongClickListener {
        DebugDataDialog(context).show()
        true
      }
    }
  }
  
  fun addSelectListener(l: (Int) -> Unit) {
    mSelectListeners.add(l)
  }
  
  fun selectPosition(position: Int) {
    if (position !in mBtnList.indices) return
    val animDuration = 180L
    val view = mBtnList[position]
    if (mLastSelectPosition != position) {
      view.isChecked = true
      view.animate()
        .scaleX(1.1F)
        .scaleY(1.1F)
        .duration = animDuration
      if (mLastSelectPosition in mBtnList.indices) {
        val lastView = mBtnList[mLastSelectPosition]
        lastView.isChecked = false
        lastView.animate()
          .scaleX(1F)
          .scaleY(1F)
          .duration = animDuration
      }
      mLastSelectPosition = position
      mSelectListeners.forEach {
        it.invoke(position)
      }
    } else {
      ValueAnimator.ofFloat(1.1F, 0.9F, 1.1F).apply {
        duration = animDuration * 2
        addUpdateListener {
          val value = it.animatedValue as Float
          view.scaleX = value
          view.scaleY = value
        }
        start()
      }
    }
  }

  /**
   * 暴露给外界的设置底部btn的Selector方法
   * @param position        底部btn的位置，用于分别是哪个btn
   * @param showNormal      是否应用一般情况下的Selector，非一般情况下的Selector请自行在代码里设置
   */
  fun setBtnSelector(position: Int, showNormal: Boolean) {
    // 目前由于只有position=2的底部btn有改变Selector的需求，故
    if (position !in mBtnList.indices || position != 2) return
    when(position) {
      2 ->{
        if (!showNormal)
          mBtnList[position].setCompoundDrawablesWithIntrinsicBounds(null, R.drawable.main_selector_bottom_mine_red_dot.drawable,null,null)
        else
          mBtnList[position].setCompoundDrawablesWithIntrinsicBounds(null, R.drawable.main_selector_bottom_mine.drawable,null,null)
      }
      // 还未定义其他底部btn的改变Selector的逻辑
      else ->{}
    }
  }
  
  override fun onSaveInstanceState(): Parcelable {
    val parent = super.onSaveInstanceState()
    val bundle = Bundle()
    bundle.putInt("上一次点击状态", mLastSelectPosition)
    bundle.putParcelable("parent", parent)
    return bundle
  }
  
  override fun onRestoreInstanceState(state: Parcelable) {
    if (state is Bundle) {
      mLastSelectPosition = state.getInt("上一次点击状态")
      super.onRestoreInstanceState(state.getParcelable("parent"))
      selectPosition(mLastSelectPosition)
    }
  }
}