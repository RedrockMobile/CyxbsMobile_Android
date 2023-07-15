package com.mredrock.cyxbs.affair.ui.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.ui.fragment.AddAffairFragment
import com.mredrock.cyxbs.affair.ui.fragment.EditAffairFragment
import com.mredrock.cyxbs.affair.ui.viewmodel.activity.AffairViewModel
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
     * 暂时去掉密封类的使用，待R8完全支持后请改回来，原因：
     * 密封类在 D8 和 R8 编译器（产生错误的编译器）中不完全受支持。
     * 在 https://issuetracker.google.com/227160052 中跟踪完全支持的密封类。
     * D8支持将出现在Android Studio Electric Eel中，目前处于预览状态，而R8支持在更高版本之前不会出现
     * stackoverflow上的回答：
     * https://stackoverflow.com/questions/73453524/what-is-causing-this-error-com-android-tools-r8-internal-nc-sealed-classes-are#:~:text=This%20is%20due%20to%20building%20using%20JDK%2017,that%20the%20dexer%20won%27t%20be%20able%20to%20handle.
     */
    interface Argument : Serializable
    data class AddAffairArgument(
      val week: Int,
      val day: Int,
      val beginLesson: Int,
      val period: Int
    ) : Argument
    
    data class EditAffairArgument(val onlyId: Int) : Argument
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
      finishAfterTransition()
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
    }
  }
}