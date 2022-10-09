package com.mredrock.cyxbs.lib.course.fragment.page.base

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.fragment.page.expose.IToday
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2pxF
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.ndhzs.netlayout.draw.ItemDecoration

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/4 15:24
 */
abstract class TodayImpl : NoLessonImpl(), IToday {
  
  private val mTodayHighlightHelper by lazyUnlock {
    TodayHighlightHelper().also {
      nlWeek.addItemDecoration(it)
      course.addItemDecoration(it)
    }
  }
  
  override fun showToday(weekNum: Int) {
    mTodayHighlightHelper.setWeekNum(weekNum)
    getWeekWeekView(weekNum) { week, month ->
      val color = com.mredrock.cyxbs.config.R.color.config_white_black.color
      week.setTextColor(color)
      month.setTextColor(color)
    }
  }
  
  private inner class TodayHighlightHelper : ItemDecoration {
    
    private var mLeft = 0F
    private var mRight = 0F
    
    private val mRadius = 8.dp2pxF
    
    private val mWeekPaint = Paint().apply {
      color = com.mredrock.cyxbs.config.R.color.config_level_four_font_color.color
      style = Paint.Style.FILL
      isAntiAlias = true
    }
    
    private val mCoursePaint = Paint().apply {
      color = R.color.course_today_highlight.color
      style = Paint.Style.FILL
    }
    
    override fun onDrawBelow(canvas: Canvas, view: View) {
      when (view) {
        course -> {
          canvas.drawRect(mLeft, 0F, mRight, view.height.toFloat(), mCoursePaint)
        }
        nlWeek -> {
          canvas.drawRect(mLeft, mRadius, mRight, view.height.toFloat(), mCoursePaint)
          canvas.drawRoundRect(mLeft, 0F, mRight, view.height.toFloat(), mRadius, mRadius, mWeekPaint)
        }
      }
    }
    
    fun setWeekNum(weekNum: Int) {
      mLeft = getWeekNumStartWidth(weekNum).toFloat()
      mRight = getWeekNumEndWidth(weekNum).toFloat()
      if (mLeft == 0F && mRight == 0F) {
        // 如果都为 0，说明此时没有开始布局
        course.post {
          if (view != null) {
            // view 为 null 的时候说明此时 Fragment 已经被摧毁了
            // 出现这个情况的概率很低
            mLeft = getWeekNumStartWidth(weekNum).toFloat()
            mRight = getWeekNumEndWidth(weekNum).toFloat()
            nlWeek.invalidate()
            course.invalidate()
          }
        }
      } else {
        nlWeek.invalidate()
        course.invalidate()
      }
    }
  }
}