package com.mredrock.cyxbs.course.page.course.item.affair

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.core.view.forEach
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.course.page.course.item.IRank
import com.mredrock.cyxbs.course.page.course.item.affair.lp.AffairLayoutParams
import com.mredrock.cyxbs.course.page.course.item.view.ItemView
import com.mredrock.cyxbs.lib.course.fragment.item.IOverlapItem
import com.mredrock.cyxbs.lib.course.internal.item.forEachRow
import com.mredrock.cyxbs.lib.course.item.AbstractAffair
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import kotlin.math.max
import kotlin.math.sqrt

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:43
 */
class Affair(val data: AffairData) : AbstractAffair(data), IRank, IWeek by data {
  
  override val rank: Int
    get() = lp.rank
  
  override val lp: AffairLayoutParams = AffairLayoutParams(data)
  
  override fun createView(context: Context, parentStartRow: Int, parentEndRow: Int): View {
    return AffairView.newInstance(context, data)
  }
  
  override fun compareTo(other: IOverlapItem): Int {
    return if (other is IRank) compareToInternal(other) else 1
  }
  
  override fun onAddIntoCourse() {
    super.onAddIntoCourse()
    forEachRow { row ->
      if (getBelowItem(row) != null) {
        view.forEach {
          if (it is ItemView) {
            it.setIsShowOverlapTag(true)
          }
        }
        return@forEachRow
      }
    }
  }
  
  /**
   * 描述:课表中事务的背景View，
   * 别问为什么不用图片，问就是图片太麻烦，而且效果还不好
   *
   * @author Jovines
   * @create 2020-01-26 2:36 PM
   *
   * @author 985892345
   * @data 2022/2/7 16:40
   * @describe 因需求变更，我开始重构课表，简单优化了一下之前学长写的逻辑
   */
  private class AffairView private constructor(
    context: Context,
  ) : ItemView(context) {
    
    companion object {
      private val COLOR_TEXT = com.mredrock.cyxbs.config.R.color.config_level_two_font_color.color
      private val COLOR_STRIPE = R.color.course_affair_stripe.color
      
      fun newInstance(context: Context, data: AffairData): AffairView {
        return AffairView(context).apply {
          setLessonData(data)
        }
      }
    }
    
    fun setLessonData(data: AffairData) {
      setColor()
      setText(data.title, data.content)
    }
    
    private fun setColor() {
      mTvTitle.setTextColor(COLOR_TEXT)
      mTvContent.setTextColor(COLOR_TEXT)
      setOverlapTagColor(COLOR_TEXT)
    }
    
    init {
      setCardBackgroundColor(Color.TRANSPARENT)
    }
  
    private val mPaint = Paint().apply {
      color = COLOR_STRIPE
    }
  
    private val mRectF = RectF()
    private val mClipBounds = Rect()
  
    override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)
      canvas.getClipBounds(mClipBounds)
      val width = mClipBounds.width()
      val height = mClipBounds.height()
      val drawEdge = max(width, height) * sqrt(2F)
      val space = 8.dp2px
      val num = (drawEdge / (space * 2)).toInt()
      canvas.save()
      canvas.translate(width / 2F, height / 2F)
      canvas.rotate(45F)
      mRectF.set(
        -drawEdge / 2,
        drawEdge / 2,
        -drawEdge / 2 + space,
        -drawEdge / 2
      )
      for (i in 0 until num) {
        canvas.drawRect(mRectF, mPaint)
        mRectF.set(
          mRectF.left + (space * 2),
          mRectF.top,
          mRectF.right + (space * 2),
          mRectF.bottom
        )
      }
      canvas.restore()
      drawOverlapTag(canvas) // 因为会被上面的覆盖，所以需要单独再绘制一遍
    }
  }
}