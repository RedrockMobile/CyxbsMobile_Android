package com.mredrock.cyxbs.lib.course.internal.view.course

import android.content.Context
import android.util.AttributeSet
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.view.course.base.AbstractCourseViewGroup
import com.mredrock.cyxbs.lib.course.internal.view.course.base.CourseScrollControlImpl
import com.ndhzs.netlayout.view.NetLayout

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
 * 最底层的实现类就是当前这个 [CourseViewGroup]，基本上是一个空壳，但它的最顶层父类 [NetLayout] 属于整个课表的基础框架
 *
 * ### 四、Fragment 层
 * **实现与业务高度相关的需求，在这一层实现业务逻辑。** 比如：中午和傍晚时间段的折叠动画、得到内部控件实例
 *
 * 如果之后遇到了课表的大型改变，比如添加一些新的区域，更建议在 base 层进行继承并实现，但要求写好对应的接口，
 * 然后添加在 [ICourseViewGroup] 中！！！
 *
 * 小型改变的话，就直接从 Fragment 层改动即可
 *
 * ## 为什么不用组合代替继承？
 * 其实能抽离出来的基本都抽出来，剩下的大部分是与整体有关联的东西，比如需要在子类中暴露 public 方法。因此才使用了继承的方式
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
}