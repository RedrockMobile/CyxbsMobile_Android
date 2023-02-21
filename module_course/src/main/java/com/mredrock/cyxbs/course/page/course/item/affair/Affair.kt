package com.mredrock.cyxbs.course.page.course.item.affair

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.View
import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.course.page.course.item.BaseOverlapSingleDayItem
import com.mredrock.cyxbs.course.page.course.item.affair.lp.AffairLayoutParams
import com.mredrock.cyxbs.course.page.course.item.view.IOverlapTag
import com.mredrock.cyxbs.course.page.course.item.view.OverlapTagHelper
import com.mredrock.cyxbs.course.page.course.utils.container.base.IDataOwner
import com.mredrock.cyxbs.course.page.course.utils.container.base.IRecycleItem
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.item.affair.IAffairItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.TouchItemHelper
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableItemHelperConfig
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableListener
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.LocationUtil
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.MovableItemHelper
import com.mredrock.cyxbs.lib.course.item.view.AffairItemView

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:43
 */
class Affair(private var affairData: AffairData) :
  BaseOverlapSingleDayItem<Affair.AffairView, AffairData>(),
  IDataOwner<AffairData>,
  IAffairItem,
  IRecycleItem,
  ITouchItem
{
  
  override fun setNewData(newData: AffairData) {
    getChildIterable().forEach {
      if (it is AffairView) {
        it.setNewData(newData)
      }
    }
    lp.setNewData(newData)
    affairData = newData
  }
  
  override fun createView(context: Context): AffairView {
    return AffairView(context, affairData)
  }
  
  override val isHomeCourseItem: Boolean
    get() = true
  
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
  @SuppressLint("ViewConstructor")
  class AffairView(
    context: Context,
    override var data: AffairData
  ) : AffairItemView(context), IOverlapTag, IDataOwner<AffairData> {
    
    private val mHelper = OverlapTagHelper(this)
  
    override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)
      mHelper.drawOverlapTag(canvas)
    }
  
    override fun setIsShowOverlapTag(isShow: Boolean) {
      mHelper.setIsShowOverlapTag(isShow)
    }
    
    init {
      setNewData(data)
      mHelper.setOverlapTagColor(mTextColor)
    }
  
    override fun setNewData(newData: AffairData) {
      data = newData
      setText(data.title, data.content)
    }
  }
  
  override fun onRecycle(): Boolean {
    return true
  }
  
  override fun onReuse(): Boolean {
    val view = getView() ?: return true
    return view.run {
      parent == null && !isAttachedToWindow
    }
  }
  
  override val rank: Int
    get() = lp.rank
  
  override val lp: AffairLayoutParams = AffairLayoutParams(affairData)
  
  override val data: AffairData
    get() = affairData
  
  override val touchHelper: ITouchItemHelper = TouchItemHelper(
    MovableItemHelper(
      object : IMovableItemHelperConfig by IMovableItemHelperConfig {
        override fun isMovableToNewLocation(
          parent: ICourseViewGroup,
          item: ITouchItem,
          child: View,
          newLocation: LocationUtil.Location
        ): Boolean {
          // 存在重复事务，暂不支持移动
          return false
        }
      }
    ).apply {
      addMovableListener(
        object : IMovableListener {
          override fun onLongPressStart(
            page: ICoursePage, item: ITouchItem, child: View,
            initialX: Int, initialY: Int, x: Int, y: Int
          ) {
            super.onLongPressStart(page, item, child, initialX, initialY, x, y)
            page.changeOverlap(this@Affair, false) // 暂时取消重叠
          }
          
          override fun onOverAnimStart(
            newLocation: LocationUtil.Location?,
            page: ICoursePage, item: ITouchItem, child: View
          ) {
            super.onOverAnimEnd(newLocation, page, item, child)
            page.changeOverlap(this@Affair, true) // 恢复重叠
          }
        }
      )
    }
  )
}
