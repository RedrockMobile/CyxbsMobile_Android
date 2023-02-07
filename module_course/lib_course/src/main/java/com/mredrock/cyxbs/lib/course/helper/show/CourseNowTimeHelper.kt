package com.mredrock.cyxbs.lib.course.helper.show

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.course.ICourseBase
import com.mredrock.cyxbs.lib.course.fragment.course.expose.wrapper.ICourseWrapper
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.internal.view.course.base.AbstractCourseViewGroup
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.ndhzs.netlayout.draw.ItemDecoration
import java.util.*

/**
 * ### 该类作用：
 * - 封装左侧时间栏中当前时间线的绘制
 *
 * ### 该类设计：
 * - 对 [AbstractCourseViewGroup] 增加自定义绘图的监听来实现
 * - 绘图监听参考了 RV 的 [ItemDecoration] 的设计
 *
 * ### 注意事项：
 * - 该类只管理左侧时间栏中当前时间线的绘制，请不要添加一些不属于该类的功能，想添加功能应该再写一个 [ItemDecoration]
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/6 15:21
 */
open class CourseNowTimeHelper private constructor(
  private val base: ICourseBase,
) : ItemDecoration {
  
  companion object {
    fun attach(base: ICourseBase): CourseNowTimeHelper {
      return CourseNowTimeHelper(base)
    }
  }
  
  /**
   * 设置是否显示当前时间线
   */
  open fun setVisible(boolean: Boolean) {
    if (mVisible != boolean) {
      mVisible = boolean
      if (boolean) {
        mRefreshRunnable.start()
      } else {
        mRefreshRunnable.cancel()
      }
    }
  }
  
  private fun Int.dp2pxF() = base.course.getContext().resources.displayMetrics.density * this
  
  private val mCircleRadius = 3.dp2pxF() // 小圆半径
  private var mVisible = true // 是否显示
  
  // 画笔
  private val mPaint = Paint().apply {
    color = com.mredrock.cyxbs.config.R.color.config_level_four_font_color.color
    isAntiAlias = true
    style = Paint.Style.FILL
    strokeWidth = 1.dp2pxF()
  }
  
  // 用于每隔一段时间就刷新的 Runnable
  private val mRefreshRunnable = object : Runnable {
    override fun run() {
      base.course.invalidate()
      base.course.postDelayed(1000 * 20, this) // 20 秒刷新一次，但记得要取消，防止内存泄漏
    }
    
    fun start() {
      base.course.removeCallbacks(this)
      run()
    }
    
    fun cancel() {
      base.course.removeCallbacks(this)
      base.course.invalidate()
    }
  }
  
  init {
    // 如果类初始化时 View 已经被添加到屏幕上，则直接 start()
    if (base.course.isAttachedToWindow()) {
      mRefreshRunnable.start()
    }
  
    // 添加 course 生命周期监听，解决 Fragment 与 View 生命周期不同的问题
    base.addCourseLifecycleObservable(
      object : ICourseWrapper.CourseLifecycleObserver {
  
        // 添加 View 状态的监听，在脱离视图时取消 mRefreshRunnable，防止内存泄漏
        val attachListener = object : View.OnAttachStateChangeListener {
          override fun onViewAttachedToWindow(v: View) {
            mRefreshRunnable.start()
          }
  
          override fun onViewDetachedFromWindow(v: View) {
            mRefreshRunnable.cancel()
          }
        }
        
        override fun onCreateCourse(course: ICourseViewGroup) {
          super.onCreateCourse(course)
          base.course.addItemDecoration(this@CourseNowTimeHelper)
          course.addOnAttachStateChangeListener(attachListener)
        }
  
        override fun onDestroyCourse(course: ICourseViewGroup) {
          super.onDestroyCourse(course)
          base.course.removeItemDecoration(this@CourseNowTimeHelper)
          course.removeOnAttachStateChangeListener(attachListener)
        }
      }
    )
  }
  
  override fun onDrawBelow(canvas: Canvas, view: View) {
    if (mVisible) {
      val left = base.getTimelineStartWidth()
      val right = base.getTimelineEndWidth()
      val lineHeight = getLineHeight()
      val cx = left + 28F
      canvas.drawCircle(cx, lineHeight, mCircleRadius, mPaint)
      canvas.drawLine(cx, lineHeight, right - 20F, lineHeight, mPaint)
    }
  }
  
  protected val mCalendar = Calendar.getInstance()
  
  /**
   * 得到当前时间
   */
  protected fun getNowTime(): Int {
    mCalendar.timeInMillis = System.currentTimeMillis()
    val hour = mCalendar.get(Calendar.HOUR_OF_DAY)
    val minute = mCalendar.get(Calendar.MINUTE)
    return hour * 60 + minute
  }
  
  /**
   * 得到当前时间该显示的高度
   *
   * 因为包含下课时间段，还有中午时间段和傍晚时间段，尤其是他们又要展开，
   * 所以只能用穷举法列出了所有的时间段来计算对应的高度
   */
  protected open fun getLineHeight(): Float {
    val now = getNowTime()
    return when {
      now <= 6 * 60 -> {
        // 隐藏时间
        -mCircleRadius * 2
      }
      now <= 8 * 60 -> { //  8:00 前
        mCircleRadius + base.getLessonStartHeight(1)
      }
      now <= 8 * 60 + 45 -> { // 8:00 - 8:45 第一节课
        val start = 8 * 60
        // multiple 是计算当前时间占当前对应课程的比例，后面一样
        val multiple = (now - start) / 45F
        val lessonH = base.getLessonEndHeight(1) - base.getLessonStartHeight(1) - mCircleRadius
        multiple * lessonH + mCircleRadius + base.getLessonStartHeight(1)
      }
      now <= 8 * 60 + 55 -> { // 8:45 - 8:55 第一节课课间
        val start = 8 * 60 + 45
        val end = 8 * 60 + 55
        val multiple = (now - start) / (end - start).toFloat()
        val lessonH = base.getLessonStartHeight(2) - base.getLessonEndHeight(1)
        multiple * lessonH + base.getLessonEndHeight(1)
      }
      now <= 9 * 60 + 40 -> { // 8:55 - 9:40 第二节课
        val start = 8 * 60 + 55
        val multiple = (now - start) / 45F
        val lessonH = base.getLessonEndHeight(2) - base.getLessonStartHeight(2)
        multiple * lessonH + base.getLessonStartHeight(2)
      }
      now <= 10 * 60 + 15 -> { // 9:40 - 10:15 第二节课课间
        val start = 9 * 60 + 40
        val end = 10 * 60 + 15
        val multiple = (now - start) / (end - start).toFloat()
        val lessonH = base.getLessonStartHeight(3) - base.getLessonEndHeight(2)
        multiple * lessonH + base.getLessonEndHeight(2)
      }
      now <= 11 * 60 -> { // 10:15 - 11:00 第三节课
        val start = 10 * 60 + 15
        val multiple = (now - start) / 45F
        val lessonH = base.getLessonEndHeight(3) - base.getLessonStartHeight(3)
        multiple * lessonH + base.getLessonStartHeight(3)
      }
      now <= 11 * 60 + 10 -> { // 11:00 - 11:10 第三节课课间
        val start = 11 * 60
        val end = 11 * 60 + 10
        val multiple = (now - start) / (end - start).toFloat()
        val lessonH = base.getLessonStartHeight(4) - base.getLessonEndHeight(3)
        multiple * lessonH + base.getLessonEndHeight(3)
      }
      now <= 11 * 60 + 55 -> { // 11:10 - 11:55 第四节课
        val start = 11 * 60 + 10
        val multiple = (now - start) / 45F
        val lessonH = base.getLessonEndHeight(4) - base.getLessonStartHeight(4)
        multiple * lessonH + base.getLessonStartHeight(4)
      }
      now <= 14 * 60 -> { // 11:55 - 14:00 中午时间段
        val start = 11 * 60 + 55
        val end = 14 * 60
        val multiple = (now - start) / (end - start).toFloat()
        val noonH = base.getNoonEndHeight() - base.getNoonStartHeight()
        multiple * noonH + base.getNoonStartHeight()
      }
      now <= 14 * 60 + 45 -> { // 14:00 - 14:45 第五节课
        val start = 14 * 60
        val multiple = (now - start) / 45F
        val lessonH = base.getLessonEndHeight(5) - base.getLessonStartHeight(5)
        multiple * lessonH + base.getLessonStartHeight(5)
      }
      now <= 14 * 60 + 55 -> { // 14:45 - 14:55 第五节课课间
        val start = 14 * 60 + 45
        val end = 14 * 60 + 55
        val multiple = (now - start) / (end - start).toFloat()
        val lessonH = base.getLessonStartHeight(6) - base.getLessonEndHeight(5)
        multiple * lessonH + base.getLessonEndHeight(5)
      }
      now <= 15 * 60 + 40 -> { // 14:55 - 15:40 第六节课
        val start = 14 * 60 + 55
        val multiple = (now - start) / 45F
        val lessonH = base.getLessonEndHeight(6) - base.getLessonStartHeight(6)
        multiple * lessonH + base.getLessonStartHeight(6)
      }
      now <= 16 * 60 + 15 -> { // 15:40 - 16:15 第六节课课间
        val start = 15 * 60 + 40
        val end = 16 * 60 + 15
        val multiple = (now - start) / (end - start).toFloat()
        val lessonH = base.getLessonStartHeight(7) - base.getLessonEndHeight(6)
        multiple * lessonH + base.getLessonEndHeight(6)
      }
      now <= 17 * 60 -> { // 16:15 - 17:00 第七节课
        val start = 16 * 60 + 15
        val multiple = (now - start) / 45F
        val lessonH = base.getLessonEndHeight(7) - base.getLessonStartHeight(7)
        multiple * lessonH + base.getLessonStartHeight(7)
      }
      now <= 17 * 60 + 10 -> { // 17:00 - 17:10 第七节课课间
        val start = 17 * 60
        val end = 17 * 60 + 10
        val multiple = (now - start) / (end - start).toFloat()
        val lessonH = base.getLessonStartHeight(8) - base.getLessonEndHeight(7)
        multiple * lessonH + base.getLessonEndHeight(7)
      }
      now <= 17 * 60 + 55 -> { // 17:10 - 17:55 第八节课
        val start = 17 * 60 + 10
        val multiple = (now - start) / 45F
        val lessonH = base.getLessonEndHeight(8) - base.getLessonStartHeight(8)
        multiple * lessonH + base.getLessonStartHeight(8)
      }
      now <= 19 * 60 -> { // 17:55 - 19:00 傍晚时间段
        val start = 17 * 60 + 55
        val end = 19 * 60
        val multiple = (now - start) / (end - start).toFloat()
        val duskH = base.getDuskEndHeight() - base.getDuskStartHeight()
        multiple * duskH + base.getDuskStartHeight()
      }
      now <= 19 * 60 + 45 -> { // 19:00 - 19:45 第九节课
        val start = 19 * 60
        val multiple = (now - start) / 45F
        val lessonH = base.getLessonEndHeight(9) - base.getLessonStartHeight(9)
        multiple * lessonH + base.getLessonStartHeight(9)
      }
      now <= 19 * 60 + 55 -> { // 19:45 - 19:55 第九节课课间
        val start = 19 * 60 + 45
        val end = 19 * 60 + 55
        val multiple = (now - start) / (end - start).toFloat()
        val lessonH = base.getLessonStartHeight(10) - base.getLessonEndHeight(9)
        multiple * lessonH + base.getLessonEndHeight(9)
      }
      now <= 20 * 60 + 40 -> { // 19:55 - 20:40 第十节课
        val start = 19 * 60 + 55
        val multiple = (now - start) / 45F
        val lessonH = base.getLessonEndHeight(10) - base.getLessonStartHeight(10)
        multiple * lessonH + base.getLessonStartHeight(10)
      }
      now <= 20 * 60 + 50 -> { // 20:40 - 20:50 第十节课课间
        val start = 20 * 60 + 40
        val end = 20 * 60 + 50
        val multiple = (now - start) / (end - start).toFloat()
        val lessonH = base.getLessonStartHeight(11) - base.getLessonEndHeight(10)
        multiple * lessonH + base.getLessonEndHeight(10)
      }
      now <= 21 * 60 + 35 -> { // 20:50 - 21:35 第十一节课
        val start = 20 * 60 + 50
        val multiple = (now - start) / 45F
        val lessonH = base.getLessonEndHeight(11) - base.getLessonStartHeight(11)
        multiple * lessonH + base.getLessonStartHeight(11)
      }
      now <= 21 * 60 + 45 -> { // 21:35 - 21:45 第十一节课课间
        val start = 21 * 60 + 35
        val end = 21 * 60 + 45
        val multiple = (now - start) / (end - start).toFloat()
        val lessonH = base.getLessonStartHeight(12) - base.getLessonEndHeight(11)
        multiple * lessonH + base.getLessonEndHeight(11)
      }
      now <= 22 * 60 + 30 -> { // 21:45 - 22:30 第十二节课
        val start = 21 * 60 + 45
        val multiple = (now - start) / 45F
        val lessonH = base.getLessonEndHeight(12) - base.getLessonStartHeight(12)
        multiple * lessonH + base.getLessonStartHeight(12)
      }
      now <= 24 * 60 -> { // 22:30 - 24:00 晚上最后一节课下课到凌晨
        val start = 22 * 60 + 30
        val end = 24 * 60
        val multiple = (now - start) / (end - start).toFloat()
        val lessonH = base.course.getHeight() - base.getLessonEndHeight(12)
        multiple * lessonH + base.getLessonEndHeight(12) - mCircleRadius
      }
      else -> {
        -999F // 跑到屏幕外不显示，但应该不会到这一分支
      }
    }
  }
}