package com.mredrock.cyxbs.lib.course.view.course

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.fold.OnFoldListener
import com.mredrock.cyxbs.lib.course.helper.ScrollTouchHandler
import com.mredrock.cyxbs.lib.course.touch.IMultiTouch
import com.mredrock.cyxbs.lib.course.view.course.base.CourseTimeLineImpl
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 15:17
 */
class CourseLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseLayoutStyle,
  defStyleRes: Int = 0
) : CourseTimeLineImpl(context, attrs, defStyleAttr, defStyleRes),
  ICourseLayout,
  IMultiTouch.DefaultHandler
{
  init {
    DEBUG = true
    /*
    * 以下两个 for 循环有如下作用：
    * 1、设置初始时中午和傍晚时间段的比重为 0，为了让板块初始时刚好撑满整个能够显示的高度，
    *    使中午和傍晚在折叠的状态下，外面的 ScrollView 不用滚动就刚好能显示其余板块
    * */
    forEachNoon {
      setRowInitialWeight(it, 0F)
    }
    forEachDusk {
      setRowInitialWeight(it, 0F)
    }
    // 下面这个 for 用于设置时间轴的初始化宽度
    forEachTimeLine {
      setColumnInitialWeight(it, 0.8F)
    }
    setDefaultHandler(this)
  }
  
  override fun getDefaultPointerHandler(event: IPointerEvent, view: View): IPointerTouchHandler {
    return ScrollTouchHandler
  }
  
  private lateinit var mIvNoonFold: ImageView
  private lateinit var mIvNoonUnfold: ImageView
  private lateinit var mIvDuskFold: ImageView
  private lateinit var mIvDuskUnfold: ImageView
  
  @CallSuper
  override fun onFinishInflate() {
    super.onFinishInflate()
    mIvNoonFold = findViewById(R.id.course_iv_noon_fold)
    mIvNoonUnfold = findViewById(R.id.course_iv_noon_unfold)
    mIvDuskFold = findViewById(R.id.course_iv_dusk_fold)
    mIvDuskUnfold = findViewById(R.id.course_iv_dusk_unfold)
    addNoonFoldListener(
      object : OnFoldListener {
        override fun onFoldStart(course: ICourseLayout) = onFolding(course, 0F)
        override fun onFoldEnd(course: ICourseLayout) = onFolding(course, 1F)
        override fun onFoldWithoutAnim(course: ICourseLayout) = onFoldEnd(course)
        override fun onFolding(course: ICourseLayout, fraction: Float) = onUnfolding(course, 1 - fraction)
        override fun onUnfoldStart(course: ICourseLayout) = onUnfolding(course, 0F)
        override fun onUnfoldEnd(course: ICourseLayout) = onUnfolding(course, 1F)
        override fun onUnfoldWithoutAnim(course: ICourseLayout) = onUnfoldEnd(course)
  
        override fun onUnfolding(course: ICourseLayout, fraction: Float) {
          mIvNoonFold.alpha = 1 - fraction
          mIvNoonUnfold.alpha = fraction
        }
      }
    )
    
    addDuskFoldListener(
      object : OnFoldListener {
        override fun onFoldStart(course: ICourseLayout) = onFolding(course, 0F)
        override fun onFoldEnd(course: ICourseLayout) = onFolding(course, 1F)
        override fun onFoldWithoutAnim(course: ICourseLayout) = onFoldEnd(course)
        override fun onFolding(course: ICourseLayout, fraction: Float) = onUnfolding(course, 1 - fraction)
        override fun onUnfoldStart(course: ICourseLayout) = onUnfolding(course, 0F)
        override fun onUnfoldEnd(course: ICourseLayout) = onUnfolding(course, 1F)
        override fun onUnfoldWithoutAnim(course: ICourseLayout) = onUnfoldEnd(course)
  
        override fun onUnfolding(course: ICourseLayout, fraction: Float) {
          mIvDuskFold.alpha = 1 - fraction
          mIvDuskUnfold.alpha = fraction
        }
      }
    )
    // 初始状态下折叠中午和傍晚时间段
    // 写在这里是因为需要在上面的监听增加后才能设置
    foldNoonWithoutAnim()
    foldDuskWithoutAnim()
  }
  
  override fun measureChildWithRatio(
    child: View,
    parentWidthMeasureSpec: Int,
    parentHeightMeasureSpec: Int,
    childWidthRatio: Float,
    childHeightRatio: Float
  ) {
    val lp = child.layoutParams.net()
    val parentWidth = MeasureSpec.getSize(parentWidthMeasureSpec) - paddingLeft - paddingRight
    val wMode = MeasureSpec.getMode(parentWidthMeasureSpec)
    val childWidth = (childWidthRatio * parentWidth).toInt()
    val childWidthMeasureSpec = getChildMeasureSpec(
      MeasureSpec.makeMeasureSpec(childWidth, wMode),
      lp.leftMargin + lp.rightMargin, lp.width
    )
    
    val parentHeight = MeasureSpec.getSize(parentHeightMeasureSpec) - paddingTop - paddingBottom
    val childHeight = (childHeightRatio * parentHeight).toInt()
    val childHeightMeasureSpec = getChildMeasureSpec(
      MeasureSpec.makeMeasureSpec(
        childHeight,
        /*
        * 这里为什么直接给 EXACTLY ?
        * 1、目前需求（22年）课表在开始时不显示中午和傍晚时间段，我设计的 NetLayout 可以把高度设置成
        *    wrap_content，再调用 setRowInitialWeight() 使中午和傍晚时间段初始的比重为 0，
        *    从而实现不展开中午和傍晚时刚好铺满外控件大小，即外面的 ScrollView 刚好不能滚动
        * 2、课表如果要显示中午和傍晚时间段，则外布局需要包裹一个 NestedScrollView，这时，父布局得到的
        *    测量模式为 UNSPECIFIED，该模式会使课表初始状态不再填充父布局，所以需要把子 View 的测量改为 EXACTLY 模式
        * 3、改子 View 的测量模式原因在于 TextView 等一般的 View 在收到 UNSPECIFIED 模式时会使用自身合适的高度值，
        *    而不是 childHeight 这个值，就会导致课表初始状态不再填充父布局
        * */
        MeasureSpec.EXACTLY
      ),
      lp.topMargin + lp.bottomMargin, lp.height
    )
    
    child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
  }
}