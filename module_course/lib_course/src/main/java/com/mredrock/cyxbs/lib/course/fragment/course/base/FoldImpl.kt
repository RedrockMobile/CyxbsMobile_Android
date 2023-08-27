package com.mredrock.cyxbs.lib.course.fragment.course.base

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.addListener
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.fragment.course.expose.fold.*
import com.mredrock.cyxbs.lib.course.helper.fold.CourseFoldHelper
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.item.overlap.AbstractOverlapSingleDayItem
import com.mredrock.cyxbs.lib.course.item.overlap.IOverlap
import com.mredrock.cyxbs.lib.course.utils.forEachInline
import com.mredrock.cyxbs.lib.utils.extensions.invisible
import com.mredrock.cyxbs.lib.utils.extensions.visible

/**
 * ## 展开时出现卡顿
 * 如果你在后期修改中发现调用展开动画时，部分 item 出现卡顿，很有可能是有东西调用了 ViewGroup.suppressLayout(true) 导致的。
 * 该方法会抑制 ViewGroup 的 layout() 回调，将使 ViewGroup 不再进行布局（官方的很多需要改变 View 大小的动画中使用了 suppressLayout(true)）
 * ### 解决方法
 * 1. 如果是因为 item 行数方式改变导致的，可能是因为调用了 [IOverlap.refreshOverlap] 导致的，可以看看 [IOverlap.lockRefreshAnim]
 * 2. 如果是因为其他东西导致的，要么跟它错开，要么在外面包两层 FrameLayout，可以参考 [AbstractOverlapSingleDayItem.RootLayout]
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 14:26
 */
@Suppress("LeakingThis")
abstract class FoldImpl : ContainerImpl(), IFold {
  
  private var mNoonFoldState = FoldState.UNKNOWN // 当前中午时间段的状态，主要用于上一层保险，不能光靠他来判断
  private var mDuskFoldState = FoldState.UNKNOWN // 当前傍晚时间段的状态，主要用于上一层保险，不能光靠他来判断
  private var mOnFoldNoonListener = ArrayList<OnFoldListener>(3)
  private var mOnFoldDuskListener = ArrayList<OnFoldListener>(3)
  private var mNoonAnimation: ChangeWeightAnimation? = null // 中午折叠或者展开的动画
  private var mDuskAnimation: ChangeWeightAnimation? = null // 傍晚折叠或者展开的动画
  
  private var mFoldNoonLockedTimes = 0 // 折叠中午上锁次数
  private var mFoldDuskLockedTimes = 0 // 折叠傍晚上锁次数
  
  final override fun getNoonRowState(): FoldState {
    if (mNoonAnimation is FoldAnimation) return FoldState.FOLD_ANIM
    if (mNoonAnimation is UnfoldAnimation) return FoldState.UNFOLD_ANIM
    return mNoonFoldState
  }
  
  final override fun foldNoon(duration: Long): IFoldAnimation? {
    if (isLockedFoldNoon()) return null
    when (getNoonRowState()) {
      FoldState.FOLD, FoldState.FOLD_ANIM -> return null
      else -> {}
    }
    mNoonAnimation?.cancel()
    mOnFoldNoonListener.forEachInline { it.onFoldStart(course) }
    val nowWeight = mNoonAnimation?.nowWeight ?: 0.99999F
    return FoldAnimation(nowWeight, duration)
      .doOnChange { weight, fraction ->
        changeNoonWeight(weight)
        mOnFoldNoonListener.forEachInline { it.onFolding(course, fraction) }
      }.doOnEnd {
        mNoonAnimation = null
        mOnFoldNoonListener.forEachInline { it.onFoldEnd(course) }
      }.doOnCancel {
        mNoonAnimation = null
        mOnFoldNoonListener.forEachInline { it.onFoldCancel(course) }
      }.start().also { mNoonAnimation = it }
  }
  
  final override fun foldNoonWithoutAnim() {
    if (isLockedFoldNoon()) return
    mNoonAnimation?.cancel()
    mNoonAnimation = null
    if (getNoonRowState() != FoldState.FOLD) {
      changeNoonWeight(0F)
      mOnFoldNoonListener.forEachInline { it.onFoldWithoutAnim(course) }
    }
  }
  
  final override fun unfoldNoon(duration: Long): IFoldAnimation? {
    when (getNoonRowState()) {
      FoldState.UNFOLD, FoldState.UNFOLD_ANIM -> return null
      else -> {}
    }
    mNoonAnimation?.cancel()
    val nowWeight = mNoonAnimation?.nowWeight ?: 0.00001F
    mOnFoldNoonListener.forEachInline { it.onUnfoldStart(course) }
    return UnfoldAnimation(nowWeight, duration)
      .doOnChange { weight, fraction ->
        changeNoonWeight(weight)
        mOnFoldNoonListener.forEachInline { it.onUnfolding(course, fraction) }
      }.doOnEnd {
        mNoonAnimation = null
        mOnFoldNoonListener.forEachInline { it.onUnfoldEnd(course) }
      }.doOnCancel {
        mNoonAnimation = null
        mOnFoldNoonListener.forEachInline { it.onUnfoldCancel(course) }
      }.start().also { mNoonAnimation = it }
  }
  
  final override fun unfoldNoonWithoutAnim() {
    mNoonAnimation?.cancel()
    mNoonAnimation = null
    if (getNoonRowState() != FoldState.UNFOLD) {
      changeNoonWeight(1F)
      mOnFoldNoonListener.forEachInline { it.onUnfoldWithoutAnim(course) }
    }
  }
  
  final override fun addNoonFoldListener(l: OnFoldListener) {
    mOnFoldNoonListener.add(l)
  }
  
  final override fun lockFoldNoon() {
    mFoldNoonLockedTimes++
  }
  
  final override fun unlockFoldNoon() {
    if (isLockedFoldNoon()) {
      mFoldNoonLockedTimes--
    }
  }
  
  private fun isLockedFoldNoon(): Boolean {
    return mFoldNoonLockedTimes > 0
  }
  
  final override fun getDuskRowState(): FoldState {
    if (mDuskAnimation is FoldAnimation) return FoldState.FOLD_ANIM
    if (mDuskAnimation is UnfoldAnimation) return FoldState.UNFOLD_ANIM
    return mDuskFoldState
  }
  
  final override fun foldDusk(duration: Long): IFoldAnimation? {
    if (isLockedFoldDusk()) return null
    when (getDuskRowState()) {
      FoldState.FOLD, FoldState.FOLD_ANIM -> return null
      else -> {}
    }
    mDuskAnimation?.cancel()
    mOnFoldDuskListener.forEachInline { it.onFoldStart(course) }
    val nowWeight = mDuskAnimation?.nowWeight ?: 0.99999F
    return FoldAnimation(nowWeight, duration)
      .doOnChange { weight, fraction ->
        changeDuskWeight(weight)
        mOnFoldDuskListener.forEachInline { it.onFolding(course, fraction) }
      }.doOnEnd {
        mDuskAnimation = null
        mOnFoldDuskListener.forEachInline { it.onFoldEnd(course) }
      }.doOnCancel {
        mDuskAnimation = null
        mOnFoldDuskListener.forEachInline { it.onFoldCancel(course) }
      }.start().also { mDuskAnimation = it }
  }
  
  final override fun foldDuskWithoutAnim() {
    if (isLockedFoldDusk()) return
    mDuskAnimation?.cancel()
    mDuskAnimation = null
    if (getDuskRowState() != FoldState.FOLD) {
      changeDuskWeight(0F)
      mOnFoldDuskListener.forEachInline { it.onFoldWithoutAnim(course) }
    }
  }
  
  final override fun unfoldDusk(duration: Long): IFoldAnimation? {
    when (getDuskRowState()) {
      FoldState.UNFOLD, FoldState.UNFOLD_ANIM -> return null
      else -> {}
    }
    mDuskAnimation?.cancel()
    mOnFoldDuskListener.forEachInline { it.onUnfoldStart(course) }
    val nowWeight = mDuskAnimation?.nowWeight ?: 0.00001F
    return UnfoldAnimation(nowWeight, duration)
      .doOnChange { weight, fraction ->
        changeDuskWeight(weight)
        mOnFoldDuskListener.forEachInline { it.onUnfolding(course, fraction) }
      }.doOnEnd {
        mDuskAnimation = null
        mOnFoldDuskListener.forEachInline { it.onUnfoldEnd(course) }
      }.doOnCancel {
        mDuskAnimation = null
        mOnFoldDuskListener.forEachInline { it.onUnfoldCancel(course) }
      }.start().also { mDuskAnimation = it }
  }
  
  final override fun unfoldDuskWithoutAnim() {
    mDuskAnimation?.cancel()
    mDuskAnimation = null
    if (getDuskRowState() != FoldState.UNFOLD) {
      changeDuskWeight(1F)
      mOnFoldDuskListener.forEachInline { it.onUnfoldWithoutAnim(course) }
    }
  }
  
  final override fun addDuskFoldListener(l: OnFoldListener) {
    mOnFoldDuskListener.add(l)
  }
  
  final override fun lockFoldDusk() {
    mFoldDuskLockedTimes++
  }
  
  final override fun unlockFoldDusk() {
    if (isLockedFoldDusk()) {
      mFoldDuskLockedTimes--
    }
  }
  
  private fun isLockedFoldDusk(): Boolean {
    return mFoldDuskLockedTimes > 0
  }
  
  private fun changeNoonWeight(weight: Float) {
    forEachNoon {
      course.setRowShowWeight(it, weight)
    }
  }
  
  private fun changeDuskWeight(weight: Float) {
    forEachDusk {
      course.setRowShowWeight(it, weight)
    }
  }
  
  // 折叠动画
  private class FoldAnimation(
    nowWeight: Float = 0.99999F,
    duration: Long,
  ) : ChangeWeightAnimation(
    nowWeight, 0F,
    if (duration < 0) (nowWeight * 200).toLong() else (duration * nowWeight).toLong(),
  )
  
  // 展开动画
  private class UnfoldAnimation(
    nowWeight: Float = 0.00001F,
    duration: Long,
  ) : ChangeWeightAnimation(
    nowWeight, 1F,
    if (duration < 0) ((1 - nowWeight) * 200).toLong() else (duration * (1F - nowWeight)).toLong(),
  )
  
  
  // 比重改变的动画封装类
  private abstract class ChangeWeightAnimation(
    startWeight: Float,
    endWeight: Float,
    val time: Long,
  ) : IFoldAnimation {
    val nowWeight: Float
      get() = animator.animatedValue as Float
    
    private var animator: ValueAnimator = ValueAnimator.ofFloat(startWeight, endWeight)
    fun start(): ChangeWeightAnimation {
      animator.run {
        duration = time
        this.start()
      }
      return this
    }
    
    override fun doOnEnd(onEnd: () -> Unit): ChangeWeightAnimation {
      animator.doOnEnd { onEnd.invoke() }
      return this
    }
    
    override fun doOnCancel(onCancel: () -> Unit): ChangeWeightAnimation {
      animator.doOnCancel { onCancel.invoke() }
      return this
    }
  
    override fun doOnEndOrCancel(onOver: () -> Unit): IFoldAnimation {
      val over = { _: Animator -> onOver.invoke() }
      animator.addListener(onEnd = over, onCancel = over)
      return this
    }
    
    override fun doOnChange(onChanged: (weight: Float, fraction: Float) -> Unit): ChangeWeightAnimation {
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
        override fun onFoldStart(course: ICourseViewGroup) {
          mNoonFoldState = FoldState.FOLD_ANIM
        }
        
        override fun onFoldEnd(course: ICourseViewGroup) {
          mNoonFoldState = FoldState.FOLD
        }
        
        override fun onFoldCancel(course: ICourseViewGroup) {
          mNoonFoldState = FoldState.UNKNOWN
        }
        
        override fun onFoldWithoutAnim(course: ICourseViewGroup) {
          mNoonFoldState = FoldState.FOLD
        }
        
        override fun onUnfoldStart(course: ICourseViewGroup) {
          mNoonFoldState = FoldState.UNFOLD_ANIM
        }
        
        override fun onUnfoldEnd(course: ICourseViewGroup) {
          mNoonFoldState = FoldState.UNFOLD
        }
        
        override fun onUnfoldCancel(course: ICourseViewGroup) {
          mNoonFoldState = FoldState.UNKNOWN
        }
        
        override fun onUnfoldWithoutAnim(course: ICourseViewGroup) {
          mNoonFoldState = FoldState.UNFOLD
        }
      }
    )
    
    addDuskFoldListener(
      object : OnFoldListener {
        override fun onFoldStart(course: ICourseViewGroup) {
          mDuskFoldState = FoldState.FOLD_ANIM
        }
        
        override fun onFoldEnd(course: ICourseViewGroup) {
          mDuskFoldState = FoldState.FOLD
        }
        
        override fun onFoldCancel(course: ICourseViewGroup) {
          mDuskFoldState = FoldState.UNKNOWN
        }
        
        override fun onFoldWithoutAnim(course: ICourseViewGroup) {
          mDuskFoldState = FoldState.FOLD
        }
        
        override fun onUnfoldStart(course: ICourseViewGroup) {
          mDuskFoldState = FoldState.UNFOLD_ANIM
        }
        
        override fun onUnfoldEnd(course: ICourseViewGroup) {
          mDuskFoldState = FoldState.UNFOLD
        }
        
        override fun onUnfoldCancel(course: ICourseViewGroup) {
          mDuskFoldState = FoldState.UNKNOWN
        }
        
        override fun onUnfoldWithoutAnim(course: ICourseViewGroup) {
          mDuskFoldState = FoldState.UNFOLD
        }
      }
    )
  }
  
  
  /////////////////////////////////////
  //
  //             业务逻辑区
  //
  /////////////////////////////////////
  
  override val viewNoonFold       by R.id.course_iv_noon_fold.view<ImageView>()
  override val viewNoonUnfold     by R.id.course_iv_noon_unfold.view<ImageView>()
  override val viewDuskFold       by R.id.course_iv_dusk_fold.view<ImageView>()
  override val viewDuskUnfold     by R.id.course_iv_dusk_unfold.view<ImageView>()
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initNoon()
    initDusk()
    initFoldLogic()
  }
  
  protected open fun initNoon() {
    course.apply {
      // 设置初始时中午时间段的比重为 0，为了让板块初始时刚好撑满整个能够显示的高度，
      // 使中午和傍晚在折叠的状态下，外面的 ScrollView 不用滚动就刚好能显示其余板块
      forEachNoon {
        setRowInitialWeight(it, 0F)
      }
      
      // 初始状态下折叠中午时间段
      foldNoonWithoutAnim()
      // 如果直接设置透明度会失效，即使使用 post 也是如此，原因未知，所以使用 invisible
      viewNoonUnfold.invisible()
      
      // 中午时间段的折叠合展开动画
      addNoonFoldListener(
        object : OnFoldListener {
          override fun onFoldStart(course: ICourseViewGroup) = onFolding(course, 0F)
          override fun onFoldEnd(course: ICourseViewGroup) = onFolding(course, 1F)
          override fun onFoldWithoutAnim(course: ICourseViewGroup) = onFoldEnd(course)
          override fun onFolding(course: ICourseViewGroup, fraction: Float) =
            onUnfolding(course, 1 - fraction)
          
          override fun onUnfoldStart(course: ICourseViewGroup) = onUnfolding(course, 0F)
          override fun onUnfoldEnd(course: ICourseViewGroup) = onUnfolding(course, 1F)
          override fun onUnfoldWithoutAnim(course: ICourseViewGroup) = onUnfoldEnd(course)
          
          override fun onUnfolding(course: ICourseViewGroup, fraction: Float) {
            viewNoonFold.alpha = 1 - fraction
            viewNoonUnfold.alpha = fraction
            viewNoonUnfold.visible()
          }
        }
      )
    }
  }
  
  protected open fun initDusk() {
    course.apply {
      // 设置初始时傍晚时间段的比重为 0，为了让板块初始时刚好撑满整个能够显示的高度，
      // 使中午和傍晚在折叠的状态下，外面的 ScrollView 不用滚动就刚好能显示其余板块
      forEachDusk {
        setRowInitialWeight(it, 0F)
      }
      
      // 初始状态下折叠傍晚时间段
      foldDuskWithoutAnim()
      // 如果直接设置透明度会失效，即使使用 post 也是如此，原因未知，所以使用 invisible
      viewDuskUnfold.invisible()
      
      // 傍晚时间段的折叠和展开动画
      addDuskFoldListener(
        object : OnFoldListener {
          override fun onFoldStart(course: ICourseViewGroup) = onFolding(course, 0F)
          override fun onFoldEnd(course: ICourseViewGroup) = onFolding(course, 1F)
          override fun onFoldWithoutAnim(course: ICourseViewGroup) = onFoldEnd(course)
          override fun onFolding(course: ICourseViewGroup, fraction: Float) =
            onUnfolding(course, 1 - fraction)
          
          override fun onUnfoldStart(course: ICourseViewGroup) = onUnfolding(course, 0F)
          override fun onUnfoldEnd(course: ICourseViewGroup) = onUnfolding(course, 1F)
          override fun onUnfoldWithoutAnim(course: ICourseViewGroup) = onUnfoldEnd(course)
          
          override fun onUnfolding(course: ICourseViewGroup, fraction: Float) {
            viewDuskFold.alpha = 1 - fraction
            viewDuskUnfold.alpha = fraction
            viewDuskUnfold.visible()
          }
        }
      )
    }
  }
  
  protected open fun initFoldLogic() {
    CourseFoldHelper.attach(this)
  }
}