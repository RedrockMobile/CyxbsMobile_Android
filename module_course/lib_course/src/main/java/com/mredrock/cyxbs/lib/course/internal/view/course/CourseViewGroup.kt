package com.mredrock.cyxbs.lib.course.internal.view.course

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.view.course.base.AbstractCourseViewGroup
import com.mredrock.cyxbs.lib.course.internal.view.course.base.CourseScrollControlImpl

/**
 *
 * 与业务高度耦合的课表控件实现类
 *
 * ## 设计理念
 * 课表因为功能有点多，所以采用多个接口和一系列的 base 层来分离实现，用于之后好添加功能
 *
 * 整个课表分为四层
 * - 接口层
 * - base 层
 * - 最底层的实现类
 * - Fragment 层
 *
 * ### 一、接口层
 * 进行功能的定义，思想就是面向接口编程，把一些功能进行抽象，分离成多个接口来约束它们的行为
 *
 * ### 二、base 层
 * 考虑到如果只靠一层来实现所有接口的方法，会导致实现类膨胀，所以抽了一系列的 base 层，分别来实现对应的功能（由于 java 没有多继承，所以只能这样）。
 *
 * 每一层都实现了对应接口的方法，[AbstractCourseViewGroup] 作为最顶层的抽象类，接入总接口 [ICourseViewGroup]，
 * 然后下面几层，每层都分开实现某一个高度相关的功能
 *
 * ### 三、最底层的实现类
 * 最底层的实现类就是当前这个 [CourseViewGroup]，基本上是一个空壳
 *
 * ### 四、Fragment 层
 * **实现与业务高度相关的需求，在这一层实现业务逻辑。** 比如：中午和傍晚时间段的折叠动画、得到内部控件实例
 *
 * 如果之后遇到了课表的大型改变，比如添加一些新的区域，更建议在 base 层进行继承并实现，但要求写好对应的接口，
 * 然后添加在 [ICourseViewGroup] 中！！！
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 15:17
 */
class CourseViewGroup @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseLayoutStyle,
  defStyleRes: Int = 0
) : CourseScrollControlImpl(context, attrs, defStyleAttr, defStyleRes), ICourseViewGroup {
  
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