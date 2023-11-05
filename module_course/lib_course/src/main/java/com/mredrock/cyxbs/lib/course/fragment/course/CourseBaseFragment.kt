package com.mredrock.cyxbs.lib.course.fragment.course

import com.mredrock.cyxbs.lib.course.fragment.course.base.TimelineImpl

/**
 * 课表 Fragment 层
 *
 * ## 设计
 * 课表的 Fragment 层有一条继承链，虽然看上去很复杂，但是每一层都是独立的，他们之间耦合度小
 *
 * ### 为什么不使用组合 ？
 * 设计模式中常常提到少用继承，多用组合，但是课表这里并不适合使用组合，因为组合讲究的是一种平级关系，
 * 比如把这里的每一层父类都拆离成一个 helper，但是你在拆离后就会发现，组合时每个 helper 是不允许互相通信的，
 * 如果你想互相通信，必须借助一个中间者，反而变复杂了。
 * 还有一点是比如像 [TimelineImpl] 这一层，他是管时间轴的，你使用组合拆离出去，那么我想使用时间轴时我该怎么调用时间轴的方法呢?
 *
 * 这里你可以发现，如果这是一个系统自带的属性，那么使用继承能更好的表达，
 * 如果这是系统的一个可插拔式的功能，那么使用组合更合适（比如 ITouchItem 使用组合分离 item 的触摸事件）
 *
 * ### 如何添加新的功能呢 ？
 * 如果后续需要对课表添加的功能类似于属性，那么你需要按照当前我的设计：
 * - 写一个接口，用于描述你这一层可以向外暴露的功能，放在 expose 包下
 * - 在 base 包下写实现类，实现接口，并在继承链上的中间插入你的实现类
 * （注意按名字排序，如果你的类排在 FoldImpl 和 OverlapImpl 中间，则需要继承 FoldImpl，然后修改 OverlapImpl 的父类为你的实现类）
 * - 在最顶层接口 [ICourseBase] 中添加你的接口，这样就能保证继承链路上每一层都能使用你的功能
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 17:34
 */
open class CourseBaseFragment : TimelineImpl(), ICourseBase {
}