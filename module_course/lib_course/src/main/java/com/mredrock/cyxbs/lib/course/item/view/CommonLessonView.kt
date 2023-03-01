package com.mredrock.cyxbs.lib.course.item.view

import android.content.Context
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.item.lesson.LessonPeriod
import com.mredrock.cyxbs.lib.utils.extensions.color

/**
 * 通用的课表 itemView
 *
 * 只有颜色设置，其他功能请自己实现
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 15:12
 */
abstract class CommonLessonView(context: Context) : ItemView(context) {
  
  // 写这里不写成静态变量是因为黑夜切换会导致颜色改变，但静态变量就不容易处理
  protected open val mAmTextColor = R.color.course_am_lesson_tv.color
  protected open val mPmTextColor = R.color.course_pm_lesson_tv.color
  protected open val mNightTextColor = R.color.course_night_lesson_tv.color
  protected open val mAmBgColor = R.color.course_am_lesson_bg.color
  protected open val mPmBgColor = R.color.course_pm_lesson_bg.color
  protected open val mNightBgColor = R.color.course_night_lesson_bg.color
  
  open fun setLessonColor(period: LessonPeriod) {
    when (period) {
      LessonPeriod.AM -> {
        tvTitle.setTextColor(mAmTextColor)
        tvContent.setTextColor(mAmTextColor)
        setCardBackgroundColor(mAmBgColor)
      }
      LessonPeriod.PM -> {
        tvTitle.setTextColor(mPmTextColor)
        tvContent.setTextColor(mPmTextColor)
        setCardBackgroundColor(mPmBgColor)
      }
      LessonPeriod.NIGHT -> {
        tvTitle.setTextColor(mNightTextColor)
        tvContent.setTextColor(mNightTextColor)
        setCardBackgroundColor(mNightBgColor)
      }
    }
  }
}