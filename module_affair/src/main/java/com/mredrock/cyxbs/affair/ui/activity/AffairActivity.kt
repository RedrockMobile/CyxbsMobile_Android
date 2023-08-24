package com.mredrock.cyxbs.affair.ui.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.ui.fragment.AddAffairFragment
import com.mredrock.cyxbs.affair.ui.fragment.EditAffairFragment
import com.mredrock.cyxbs.affair.ui.fragment.NoClassAffairFragment
import com.mredrock.cyxbs.affair.ui.viewmodel.activity.AffairViewModel
import com.mredrock.cyxbs.api.affair.NoClassBean
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import java.io.Serializable
import kotlin.math.PI
import kotlin.math.cos

class AffairActivity : BaseActivity() {
  
  companion object {
    fun startForAdd(
      week: Int,
      day: Int,
      beginLesson: Int,
      period: Int
    ) {
      appContext.startActivity(
        Intent(appContext, AffairActivity::class.java)
          .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          .putExtra(
            AffairActivity::mArguments.name,
            AddAffairArgument(week, day, beginLesson, period)
          )
      )
    }
    
    fun startForEdit(onlyId: Int) {
      appContext.startActivity(
        Intent(appContext, AffairActivity::class.java)
          .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          .putExtra(AffairActivity::mArguments.name, EditAffairArgument(onlyId))
      )
    }
    /**
     *  没课约的专属跳转方法
     *  @param noClassBean : 学号：是否空闲
     */
    fun startForNoClass(noClassBean: NoClassBean){
      Log.d("lx", "startForNoClass:${noClassBean} ")
      appContext.startActivity(
        Intent(appContext,AffairActivity::class.java)
          .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          .putExtra(AffairActivity::mArguments.name, NoClassAffairArgument(noClassBean))
      )
    }
    
    sealed interface Argument : Serializable
    data class AddAffairArgument(
      val week: Int,
      val day: Int,
      val beginLesson: Int,
      val period: Int
    ) : Argument
    
    data class EditAffairArgument(val onlyId: Int) : Argument

    data class NoClassAffairArgument(
      val noClassBean : NoClassBean
    ) : Argument
  }
  
  // 启动参数
  private val mArguments by intent<Argument>()
  
  private val mViewModel by viewModels<AffairViewModel>()
  private val mViewBg1: View by R.id.affair_view_affair_bg1.view()
  private val mViewBg2: View by R.id.affair_view_affair_bg2.view()
  private val mViewBg3: View by R.id.affair_view_affair_bg3.view()
  
  private val mViewBg4: View by R.id.affair_view_affair_bg4.view()
  
  // 返回键
  private val mBtnBack: ImageButton by R.id.affair_btn_edit_affair_back.view()
  
  // 事务设置的下一项
  private val mBtnNext: ImageButton by R.id.affair_btn_edit_affair_next.view()
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.affair_activity_affair)
    initBackground()
    initClick()
    initFragment()
  }
  
  private fun initBackground() {
    // 使用 LifecycleObserver，便于及时取消动画
    lifecycle.addObserver(
      object : DefaultLifecycleObserver {
        private val mAnimatorList = mutableListOf<ValueAnimator>()
        override fun onResume(owner: LifecycleOwner) {
          // 来个背景图片呼吸效果
          addAnimator(mViewBg1, 1000, 15000)
          addAnimator(mViewBg2, 2000, 26000)
          addAnimator(mViewBg3, 0, 20000)
          addAnimator(mViewBg4, 6000, 18000)
        }
        
        override fun onPause(owner: LifecycleOwner) {
          mAnimatorList.onEach { it.cancel() }.clear()
        }
        
        private fun addAnimator(view: View, delay: Long, duration: Long) {
          mAnimatorList.add(
            ValueAnimator.ofFloat(0F, 1F, 0F).apply {
              addUpdateListener {
                val x = it.animatedValue as Float
                val h = 0.3
                // 推荐个在线看函数的网址：https://www.geogebra.org/graphing
                val y = (1 + h / 2 - h / 2 * cos(2 * x * PI)).toFloat()
                view.scaleX = y
                view.scaleY = y
              }
              repeatCount = ValueAnimator.INFINITE
              repeatMode = ValueAnimator.RESTART
              this.duration = duration
              startDelay = delay
              start()
            }
          )
        }
      }
    )
  }
  
  private fun initClick() {
    mBtnBack.setOnSingleClickListener {
      if (mArguments is NoClassAffairArgument){
        mViewModel.clickLastBtn()
      }else{
        finishAfterTransition()
      }
    }
    
    mBtnNext.setOnSingleClickListener {
      mViewModel.clickNextBtn()
    }
  }
  
  private fun initFragment() {
    // 由不同参数打开不同界面
    when (val it = mArguments) {
      is EditAffairArgument -> {
        replaceFragment(R.id.affair_fcv_edit_affair) {
          EditAffairFragment.newInstance(it.onlyId)
        }
      }
      is AddAffairArgument -> {
        replaceFragment(R.id.affair_fcv_edit_affair) {
          AddAffairFragment.newInstance(it.week, it.day, it.beginLesson, it.period)
        }
      }
      is NoClassAffairArgument -> {
        replaceFragment(R.id.affair_fcv_edit_affair) {
          NoClassAffairFragment.newInstance(it.noClassBean)
        }
      }
    }
  }
}