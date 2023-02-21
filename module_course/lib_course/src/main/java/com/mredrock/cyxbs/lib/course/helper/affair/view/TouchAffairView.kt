package com.mredrock.cyxbs.lib.course.helper.affair.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnNextLayout
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchAffairItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.internal.view.course.lp.ItemLayoutParams
import com.mredrock.cyxbs.lib.course.item.single.SingleDayLayoutParams
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dimen
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import kotlin.math.roundToInt

/**
 * 这是长按空白区域生成的带有加号的那个灰色 View。用于点击一下打开添加事务界面
 *
 * @author 985892345
 * @date 2022/9/19 14:55
 */
@SuppressLint("ViewConstructor")
open class TouchAffairView(
  val course: ICourseViewGroup,
) : ViewGroup(course.getContext()), ITouchAffairItem {
  
  // 扩展动画
  private var mExpandValueAnimator: ValueAnimator? = null
  
  /**
   * 灰色圆角背景
   */
  private val mBackground = GradientDrawable().apply {
    val radius = R.dimen.course_item_radius.dimen
    // 设置圆角
    cornerRadii =
      floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
    // 背景颜色
    setColor(R.color.course_affair_bg.color)
  }
  
  private val mImageView = ImageView(context).apply {
    scaleType = ImageView.ScaleType.CENTER_INSIDE
    background = mBackground
    // 设置 ImageView 的前景图片
    setImageResource(R.drawable.course_ic_touch_affair)
  }
  
  override fun isInShow(): Boolean {
    return parent != null
  }
  
  override fun show(topRow: Int, bottomRow: Int, initialColumn: Int) {
    lp.apply {
      startColumn = initialColumn
      endColumn = initialColumn
      startRow = topRow
      endRow = bottomRow
    }
    // 添加一个入场动画
    startAnimation(AlphaAnimation(0F, 1F).apply { duration = 200 })
    course.addItem(this)
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    if (mExpandValueAnimator == null) {
      // 动画中是手动调用的 layout() 进行布局
      mImageView.layout(0, 0, r - l, b - t)
    }
  }
  
  /**
   * 该方法作用：
   * - 计算当前位置并刷新布局
   * - 启动一个过度动画
   */
  override fun refresh(
    oldTopRow: Int,
    oldBottomRow: Int,
    topRow: Int,
    bottomRow: Int,
  ) {
    val lp = layoutParams as ItemLayoutParams
    lp.startRow = topRow
    lp.endRow = bottomRow
    layoutParams = lp // 设置属性，刷新布局
    doOnNextLayout {
      // 在已经布局完毕后再调用动画，不然 TouchAffairView 高度值还是之前的大小，导致 ImageView 要闪
      startExpandValueAnimator(oldTopRow, oldBottomRow, topRow, bottomRow)
    }
  }
  
  override fun cancelShow() {
    mExpandValueAnimator?.end()
    // 离场动画
    startAnimation(AlphaAnimation(1F, 0F).apply { duration = 200 })
    course.removeItem(this)
  }
  
  override fun cloneLp(): SingleDayLayoutParams {
    return SingleDayLayoutParams(lp).apply {
      changeAll(lp)
    }
  }
  
  override fun setOnClickListener(l: () -> Unit) {
    setOnSingleClickListener { l.invoke() }
  }
  
  /**
   * 启动过度动画
   */
  private fun startExpandValueAnimator(
    oldTopRow: Int,
    oldBottomRow: Int,
    topRow: Int,
    bottomRow: Int,
  ) {
    mExpandValueAnimator?.end() // 取消之前的动画
    mExpandValueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
      addUpdateListener {
        val now = animatedValue as Float
        val oldTop = course.getRowsHeight(0, oldTopRow - 1) + course.getPaddingTop()
        val newTop = course.getRowsHeight(0, topRow - 1) + course.getPaddingTop()
        val nowTop = ((oldTop - newTop) * (1 - now)).roundToInt()
        val oldBottom = course.getRowsHeight(0, oldBottomRow) + course.getPaddingTop()
        val newBottom = course.getRowsHeight(0, bottomRow) + course.getPaddingTop()
        val nowBottom =
          ((newBottom - oldBottom) * now).roundToInt() + oldBottom - newTop - 2 * mMargin
        // 手动调用布局
        mImageView.layout(0, nowTop, width, nowBottom)
      }
      doOnStart {
        (parent as ViewGroup).clipChildren = false // 请求父布局不要裁剪
        background = null
        // 把背景交给 ImageView
        mImageView.background = mBackground
      }
      doOnEnd {
        mExpandValueAnimator = null
        (parent as ViewGroup).clipChildren = true // 及时关闭，减少不必要绘制
        // 设置为 ImageView 的背景后，这样会使整体移动中改变 translationZ 时带有阴影效果
        background = mImageView.background
        mImageView.background = null
      }
      interpolator = DecelerateInterpolator()
      duration = 120
      start()
    }
  }
  
  // 课表中 item 的边距值
  private val mMargin = R.dimen.course_item_margin.dimen.toInt()
  
  init {
    super.addView(mImageView)
  }
  
  override fun initializeView(context: Context): View {
    return this
  }
  
  override val lp = SingleDayLayoutParams(-1, -1, 0).apply {
    leftMargin = mMargin
    rightMargin = mMargin
    topMargin = mMargin
    bottomMargin = mMargin
  }
}