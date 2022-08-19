package com.mredrock.cyxbs.lib.course.internal.view.course.base

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.fold.FoldState
import com.mredrock.cyxbs.lib.course.internal.fold.OnFoldListener
import com.mredrock.cyxbs.lib.course.internal.period.dusk.IFoldDusk
import com.mredrock.cyxbs.lib.course.internal.period.noon.IFoldNoon
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseLayout

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 15:11
 */
@Suppress("DeprecatedCallableAddReplaceWith")
abstract class CourseFoldImpl @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseLayoutStyle,
  defStyleRes: Int = 0
) : CourseContainerImpl(context, attrs, defStyleAttr, defStyleRes), IFoldNoon, IFoldDusk {
  
  private var mNoonFoldState = FoldState.UNKNOWN // 当前中午时间段的状态，主要用于上一层保险，不能光靠他来判断
  private var mDuskFoldState = FoldState.UNKNOWN // 当前傍晚时间段的状态，主要用于上一层保险，不能光靠他来判断
  private var mOnFoldNoonListener = ArrayList<OnFoldListener>(3)
  private var mOnFoldDuskListener = ArrayList<OnFoldListener>(3)
  private var mNoonAnimation: ChangeWeightAnimation? = null // 中午折叠或者展开的动画
  private var mDuskAnimation: ChangeWeightAnimation? = null // 傍晚折叠或者展开的动画
  
  final override fun getNoonRowState(): FoldState {
    if (mNoonAnimation is FoldAnimation) return FoldState.FOLD_ANIM
    if (mNoonAnimation is UnfoldAnimation) return FoldState.UNFOLD_ANIM
    return mNoonFoldState
  }
  
  final override fun foldNoon(duration: Long, onChanged: ((weight: Float, fraction: Float) -> Unit)?) {
    when (getNoonRowState()) {
      FoldState.FOLD, FoldState.FOLD_ANIM -> {
        mNoonAnimation?.doOnChange(onChanged)
        return
      }
      else -> mNoonAnimation?.cancel()
    }
    mOnFoldNoonListener.forEach { it.onFoldStart(this) }
    val nowWeight = mNoonAnimation?.nowWeight ?: 0.99999F
    mNoonAnimation = FoldAnimation(nowWeight, duration) { weight, fraction ->
      changeNoonWeight(weight)
      onChanged?.invoke(weight, fraction)
      mOnFoldNoonListener.forEach { it.onFolding(this, fraction) }
    }.doOnEnd {
      mNoonAnimation = null
      mOnFoldNoonListener.forEach { it.onFoldEnd(this) }
    }.doOnCancel {
      mNoonAnimation = null
      mOnFoldNoonListener.forEach { it.onFoldCancel(this) }
    }.start()
  }
  
  final override fun foldNoonWithoutAnim() {
    mNoonAnimation?.cancel()
    mNoonAnimation = null
    if (getNoonRowState() != FoldState.FOLD) {
      changeNoonWeight(0F)
      mOnFoldNoonListener.forEach { it.onFoldWithoutAnim(this) }
    }
  }
  
  final override fun unfoldNoon(duration: Long, onChanged: ((weight: Float, fraction: Float) -> Unit)?) {
    when (getNoonRowState()) {
      FoldState.UNFOLD, FoldState.UNFOLD_ANIM -> {
        mNoonAnimation?.doOnChange(onChanged)
        return
      }
      else -> mNoonAnimation?.cancel()
    }
    val nowWeight = mNoonAnimation?.nowWeight ?: 0.00001F
    mOnFoldNoonListener.forEach { it.onUnfoldStart(this) }
    mNoonAnimation = UnfoldAnimation(nowWeight, duration) { weight, fraction ->
      changeNoonWeight(weight)
      onChanged?.invoke(weight, fraction)
      mOnFoldNoonListener.forEach { it.onUnfolding(this, fraction) }
    }.doOnEnd {
      mNoonAnimation = null
      mOnFoldNoonListener.forEach { it.onUnfoldEnd(this) }
    }.doOnCancel {
      mNoonAnimation = null
      mOnFoldNoonListener.forEach { it.onUnfoldCancel(this) }
    }.start()
  }
  
  final override fun unfoldNoonWithoutAnim() {
    mNoonAnimation?.cancel()
    mNoonAnimation = null
    if (getNoonRowState() != FoldState.UNFOLD) {
      changeNoonWeight(1F)
      mOnFoldNoonListener.forEach { it.onUnfoldWithoutAnim(this) }
    }
  }
  
  final override fun addNoonFoldListener(l: OnFoldListener) {
    mOnFoldNoonListener.add(l)
  }
  
  final override fun getDuskRowState(): FoldState {
    if (mNoonAnimation is FoldAnimation) return FoldState.FOLD_ANIM
    if (mNoonAnimation is UnfoldAnimation) return FoldState.UNFOLD_ANIM
    return mDuskFoldState
  }
  
  final override fun foldDusk(duration: Long, onChanged: ((weight: Float, fraction: Float) -> Unit)?) {
    when (getDuskRowState()) {
      FoldState.FOLD, FoldState.FOLD_ANIM -> {
        mDuskAnimation?.doOnChange(onChanged)
        return
      }
      else -> mDuskAnimation?.cancel()
    }
    mOnFoldDuskListener.forEach { it.onFoldStart(this) }
    val nowWeight = mDuskAnimation?.nowWeight ?: 0.99999F
    mDuskAnimation = FoldAnimation(nowWeight, duration) { weight, fraction ->
      changeDuskWeight(weight)
      onChanged?.invoke(weight, fraction)
      mOnFoldDuskListener.forEach { it.onFolding(this, fraction) }
    }.doOnEnd {
      mDuskAnimation = null
      mOnFoldDuskListener.forEach { it.onFoldEnd(this) }
    }.doOnCancel {
      mDuskAnimation = null
      mOnFoldDuskListener.forEach { it.onFoldCancel(this) }
    }.start()
  }
  
  final override fun foldDuskWithoutAnim() {
    mDuskAnimation?.cancel()
    mDuskAnimation = null
    if (getDuskRowState() != FoldState.FOLD) {
      changeDuskWeight(0F)
      mOnFoldDuskListener.forEach { it.onFoldWithoutAnim(this) }
    }
  }
  
  final override fun unfoldDusk(duration: Long, onChanged: ((weight: Float, fraction: Float) -> Unit)?) {
    when (getDuskRowState()) {
      FoldState.UNFOLD, FoldState.UNFOLD_ANIM -> {
        mDuskAnimation?.doOnChange(onChanged)
        return
      }
      else -> mDuskAnimation?.cancel()
    }
    mOnFoldDuskListener.forEach { it.onUnfoldStart(this) }
    val nowWeight = mDuskAnimation?.nowWeight ?: 0.00001F
    mDuskAnimation = UnfoldAnimation(nowWeight, duration) { weight, fraction ->
      changeDuskWeight(weight)
      onChanged?.invoke(weight, fraction)
      mOnFoldDuskListener.forEach { it.onUnfolding(this, fraction) }
    }.doOnEnd {
      mDuskAnimation = null
      mOnFoldDuskListener.forEach { it.onUnfoldEnd(this) }
    }.doOnCancel {
      mDuskAnimation = null
      mOnFoldDuskListener.forEach { it.onUnfoldCancel(this) }
    }.start()
  }
  
  final override fun unfoldDuskWithoutAnim() {
    mDuskAnimation?.cancel()
    mDuskAnimation = null
    if (getDuskRowState() != FoldState.UNFOLD) {
      changeDuskWeight(1F)
      mOnFoldDuskListener.forEach { it.onUnfoldWithoutAnim(this) }
    }
  }
  
  final override fun addDuskFoldListener(l: OnFoldListener) {
    mOnFoldDuskListener.add(l)
  }
  
  private fun changeNoonWeight(weight: Float) {
    forEachNoon {
      super.setRowShowWeight(it, weight)
    }
  }
  
  private fun changeDuskWeight(weight: Float) {
    forEachDusk {
      super.setRowShowWeight(it, weight)
    }
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  override fun setRowShowWeight(row: Int, weight: Float) {
    super.setRowShowWeight(row, weight)
  }
  
  @Deprecated("禁止子类调用", level = DeprecationLevel.HIDDEN)
  override fun setColumnShowWeight(column: Int, weight: Float) {
    super.setColumnShowWeight(column, weight)
  }
  
  // 折叠动画
  private class FoldAnimation(
    nowWeight: Float = 0.99999F,
    duration: Long,
    onChanged: (weight: Float, fraction: Float) -> Unit
  ) : ChangeWeightAnimation(
    nowWeight, 0F, if(duration < 0) (nowWeight * 200).toLong() else duration, onChanged)
  
  // 展开动画
  private class UnfoldAnimation(
    nowWeight: Float = 0.00001F,
    duration: Long,
    onChanged: (weight: Float, fraction: Float) -> Unit
  ) : ChangeWeightAnimation(
    nowWeight, 1F, if (duration < 0) ((1 - nowWeight) * 200).toLong() else duration, onChanged)
  
  // 比重改变的动画封装类
  private abstract class ChangeWeightAnimation(
    startWeight: Float,
    endWeight: Float,
    val time: Long,
    private val onChanged: (weight: Float, fraction: Float) -> Unit
  ) {
    val nowWeight: Float
      get() = animator.animatedValue as Float
    
    private var animator: ValueAnimator = ValueAnimator.ofFloat(startWeight, endWeight)
    fun start(): ChangeWeightAnimation {
      animator.run {
        addUpdateListener { onChanged.invoke(nowWeight, animatedFraction) }
        duration = time
        this.start()
      }
      return this
    }
    
    fun doOnEnd(onEnd: () -> Unit): ChangeWeightAnimation {
      animator.doOnEnd { onEnd.invoke() }
      return this
    }
    
    fun doOnCancel(onCancel: () -> Unit): ChangeWeightAnimation {
      animator.doOnCancel { onCancel.invoke() }
      return this
    }
    
    fun doOnChange(onChanged: ((weight: Float, fraction: Float) -> Unit)?): ChangeWeightAnimation {
      if (onChanged == null) return this
      animator.addUpdateListener { onChanged.invoke(nowWeight, animator.animatedFraction) }
      return this
    }
    
    fun cancel() {
      animator.cancel()
    }
  }
  
  init {
    // 为了分离逻辑，所以把 mNoonFoldState 和 mDuskFoldState 写在了这里
    addNoonFoldListener(
      object : OnFoldListener {
        override fun onFoldStart(course: ICourseLayout) {
          mNoonFoldState = FoldState.FOLD_ANIM
        }
  
        override fun onFoldEnd(course: ICourseLayout) {
          mNoonFoldState = FoldState.FOLD
        }
  
        override fun onFoldCancel(course: ICourseLayout) {
          mNoonFoldState = FoldState.UNKNOWN
        }
  
        override fun onFoldWithoutAnim(course: ICourseLayout) {
          mNoonFoldState = FoldState.FOLD
        }
  
        override fun onUnfoldStart(course: ICourseLayout) {
          mNoonFoldState = FoldState.UNFOLD_ANIM
        }
  
        override fun onUnfoldEnd(course: ICourseLayout) {
          mNoonFoldState = FoldState.UNFOLD
        }
  
        override fun onUnfoldCancel(course: ICourseLayout) {
          mNoonFoldState = FoldState.UNKNOWN
        }
  
        override fun onUnfoldWithoutAnim(course: ICourseLayout) {
          mNoonFoldState = FoldState.UNFOLD
        }
      }
    )
    
    addDuskFoldListener(
      object : OnFoldListener {
        override fun onFoldStart(course: ICourseLayout) {
          mDuskFoldState = FoldState.FOLD_ANIM
        }
    
        override fun onFoldEnd(course: ICourseLayout) {
          mDuskFoldState = FoldState.FOLD
        }
    
        override fun onFoldCancel(course: ICourseLayout) {
          mDuskFoldState = FoldState.UNKNOWN
        }
  
        override fun onFoldWithoutAnim(course: ICourseLayout) {
          mDuskFoldState = FoldState.FOLD
        }
  
        override fun onUnfoldStart(course: ICourseLayout) {
          mDuskFoldState = FoldState.UNFOLD_ANIM
        }
    
        override fun onUnfoldEnd(course: ICourseLayout) {
          mDuskFoldState = FoldState.UNFOLD
        }
    
        override fun onUnfoldCancel(course: ICourseLayout) {
          mDuskFoldState = FoldState.UNKNOWN
        }
  
        override fun onUnfoldWithoutAnim(course: ICourseLayout) {
          mDuskFoldState = FoldState.UNFOLD
        }
      }
    )
  }
}