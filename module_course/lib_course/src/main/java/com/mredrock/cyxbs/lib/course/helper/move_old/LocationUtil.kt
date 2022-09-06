//package com.mredrock.cyxbs.course2.page.course.helper.multitouch.entitymove
//
//import android.view.View
//import androidx.annotation.UiThread
//import com.mredrock.cyxbs.course2.page.course.helper.IItemTypeProvider
//import com.mredrock.cyxbs.course2.page.course.widget.course.ItemType
//import com.mredrock.cyxbs.lib.course.net.attrs.INetBean
//import com.mredrock.cyxbs.lib.course.net.attrs.NetLayoutParams
//import com.mredrock.cyxbs.lib.course.net.view.NetLayout
//import kotlin.math.max
//import kotlin.math.min
//
///**
// * 移动后判断能否放得下的工具类
// *
// * 判断有点多，但不算很复杂，为了以后更好的兼容，我写成了任意行宽和列宽的判断，
// * 意思就是：目前课表虽然列宽都是为 1 的 View，但我并没有使用这特殊条件，而是写出了任意列宽的比较
// *
// * @author 985892345 (Guo Xiangrui)
// * @email 2767465918@qq.com
// * @date 2022/2/21 14:40
// */
//object LocationUtil {
//
//  /**
//   * 得到移动后的位置
//   * @return 如果返回 null，则移动后的位置放不下，需要回到原位置
//   */
//  @UiThread
//  fun getLocation(
//    view: View?,
//    parent: NetLayout,
//    itemTypeProvider: IItemTypeProvider,
//    isSkipForeachJudge: (View) -> Boolean
//  ): Location? {
//    if (view != null) {
//      val layoutParams = view.layoutParams as NetLayoutParams
//      /*
//      * 可能你会问为什么不直接用 View.left + View.translationX 或者直接 View.x
//      * 原因如下：
//      * 虽然目前课表的每个 item 宽高都是 match，
//      * 但 item 的移动都是限制在方格内的，不应该用 View 的位置来判断
//      * */
//      val l = layoutParams.constraintLeft + view.translationX.toInt()
//      val r = layoutParams.constraintRight + view.translationX.toInt()
//      val t = layoutParams.constraintTop + view.translationY.toInt()
//      val b = layoutParams.constraintBottom + view.translationY.toInt()
//
//      val rowCount = layoutParams.rowCount
//      val columnCount = layoutParams.columnCount
//
//      var topRow = parent.getRow(t)
//      var bottomRow = parent.getRow(b)
//      var leftColumn = parent.getColumn(l)
//      var rightColumn = parent.getColumn(r)
//
//      val topDistance = parent.getRowsHeight(0, topRow) - t
//      val bottomDistance = b - parent.getRowsHeight(0, bottomRow - 1)
//      val leftDistance = parent.getColumnsWidth(0, leftColumn) - l
//      val rightDistance = r - parent.getColumnsWidth(0, rightColumn - 1)
//
//      /*
//      * 第一次修正：
//      * 根据与 View 内部最相邻的格子约束线的距离，修正此时的位置（格子约束线：因为是网状布局嘛，所以会有一个一个的格子）
//      * 如果不修正，那由边界高度值取得的行数和列数将比总行数和总列数大上一行和一列
//      * */
//      if (leftDistance >= rightDistance) {
//        rightColumn = leftColumn + columnCount - 1
//      } else {
//        leftColumn = rightColumn - columnCount + 1
//      }
//      if (topDistance >= bottomDistance) {
//        bottomRow = topRow + rowCount - 1
//      } else {
//        topRow = bottomRow - rowCount + 1
//      }
//
//      // 记录与其他子 View 部分相交时该取得的最大最小值
//      var maxTopRow = 0
//      var minBottomRow = parent.rowCount - 1
//      var maxLeftColumn = 0
//      var minRightColumn = parent.columnCount - 1
//
//      // 判断行和列是否都完全包含或被包含，不包含取等时，取等要单独判断
//      fun judgeIsContain(
//        l1: Int, r1: Int, t1: Int, b1: Int,
//        l2: Int, r2: Int, t2: Int, b2: Int
//      ): Boolean {
//        val c1 = l1 < l2 && r1 > r2
//        val c2 = l1 > l2 && r1 < r2
//        val d1 = t1 < t2 && b1 > b2
//        val d2 = t1 > t2 && b1 < b2
//        return (c1 || c2) && (d1 || d2)
//      }
//
//      /*
//      * 第一次遍历：
//      * 1、判断行或列是否完全包含或被包含，如果完全包含或被包含则回到原位置
//      * 2、计算与其他 View 相交时该取得的边界最大最小值
//      * */
//      for (i in 0 until parent.childCount) {
//        val child = parent.getChildAt(i)
//        if (isSkipForeachJudge(child)) continue
//        when (itemTypeProvider.getItemType(child)) {
//          ItemType.AFFAIR_TOUCH, ItemType.SUBSTITUTE, ItemType.UNKNOWN -> continue
//          else -> {}
//        }
//        val lp = child.layoutParams as NetLayoutParams
//        val l1 = lp.constraintLeft
//        val r1 = lp.constraintRight
//        val t1 = lp.constraintTop
//        val b1 = lp.constraintBottom
//
//        /*
//        * 由于没有判断是否是 mLongPressView，只判断了 mSubstituteView，
//        * 所以当在 mLongPressView 没有添加进 overlay 时，是会直接回到原位置的，
//        * 没有被添加进 overlay 说明要么处在动画中松手，要么没有长按就
//        * */
//
//        // 如果完全包含或被包含则回到原位置
//        if (judgeIsContain(l, r, t, b, l1, r1, t1, b1)) {
//          return null
//        }
//        val centerX = (l + r) / 2
//        val centerY = (t + b) / 2
//        // 以下是只有列完全包含或被包含时的特殊情况
//        if (l <= l1 && r >= r1 || l >= l1 && r <= r1) {
//          when {
//            centerY < t1 -> minBottomRow = min(minBottomRow, lp.startRow - 1)
//            centerY > b1 -> maxTopRow = max(maxTopRow, lp.endRow + 1)
//            else -> {
//              return null
//            }
//          }
//        }
//        // 以下是只有行完全包含或被包含的特殊情况
//        if (t <= t1 && b >= b1 || t >= t1 && b <= b1) {
//          when {
//            centerX < l1 -> minRightColumn = min(minRightColumn, lp.startColumn - 1)
//            centerX > r1 -> maxLeftColumn = max(maxLeftColumn, lp.endColumn + 1)
//            else -> {
//              return null
//            }
//          }
//        }
//        /*
//        * 以下是只相交一个角时，此时主要是计算边界最大最小值
//        * 情况如下：
//        * 一、水平重叠的距离超过自身一半，且垂直重叠的距离也超过一半，不允许放置，回到原位置
//        * 二、水平重叠的距离少于自身一半，且垂直重叠的距离也少于一半，根据重叠间距来计算对应的最大最小值
//        * 三、水平重叠的距离超过一半，垂直重叠的距离少于一半，计算对应的最大最小值
//        * 四、垂直重叠的距离超过一半，水平重叠的距离少于一半，计算对应的最大最小值
//        * */
//        val e1 = centerX in l1..r1
//        val e2 = centerY in t1..b1
//        if (e1 && e2) { // 情况一
//          return null
//        } else if (!e1 && !e2) { // 比较复杂的情况二
//          if (centerX < l1 && centerY < t1) { // 在一个子 View 的左上角
//            val dl = r - l1 // 水平重叠间距
//            val dt = b - t1 // 垂直重叠间距
//            if (dl > dt) {
//              minBottomRow = min(minBottomRow, lp.startRow - 1)
//            } else {
//              minRightColumn = min(minRightColumn, lp.startColumn - 1)
//            }
//          } else if (centerX > r1 && centerY < t1) { // 在一个子 View 的右上角
//            val dr = r1 - l // 水平重叠间距
//            val dt = b - t1 // 垂直重叠间距
//            if (dr > dt) {
//              minBottomRow = min(minBottomRow, lp.startRow - 1)
//            } else {
//              maxLeftColumn = max(maxLeftColumn, lp.endColumn + 1)
//            }
//          } else if (centerX > r1 && centerY > b1) { // 在一个子 View 的右下角
//            val dr = r1 - l // 水平重叠间距
//            val db = b1 - t // 垂直重叠间距
//            if (dr > db) {
//              maxTopRow = max(maxTopRow, lp.endRow + 1)
//            } else {
//              maxLeftColumn = max(maxLeftColumn, lp.endColumn + 1)
//            }
//          } else { // 在一个子 View 的左下角
//            val dl = r - l1 // 水平重叠间距
//            val db = b1 - t // 垂直重叠间距
//            if (dl > db) {
//              maxTopRow = max(maxTopRow, lp.endRow + 1)
//            } else {
//              minRightColumn = min(minRightColumn, lp.startColumn - 1)
//            }
//          }
//        } else if (e1) { // 情况三
//          if (centerY < t1) {
//            minBottomRow = min(minBottomRow, lp.startRow - 1)
//          } else if (centerY > b1) {
//            maxTopRow = max(maxTopRow, lp.endRow + 1)
//          }
//        } else { // 情况四
//          if (centerX < l1) {
//            minRightColumn = min(minRightColumn, lp.startColumn - 1)
//          } else if (centerX > r1) {
//            maxLeftColumn = max(maxLeftColumn, lp.endColumn + 1)
//          }
//        }
//      }
//
//      // 判断最大最小值是否能装下自己，如果不能，则回到原位置
//      if (minRightColumn - maxLeftColumn + 1 < columnCount
//        || minBottomRow - maxTopRow + 1 < rowCount
//      ) {
//        return null
//      }
//
//      /*
//      * 第二次修正：
//      * 根据最大最小值修正最终的位置
//      * */
//      if (maxLeftColumn > leftColumn) {
//        leftColumn = maxLeftColumn
//        rightColumn = maxLeftColumn + columnCount - 1
//      } else if (minRightColumn < rightColumn) {
//        leftColumn = minRightColumn - columnCount + 1
//        rightColumn = minRightColumn
//      }
//      if (maxTopRow > topRow) {
//        topRow = maxTopRow
//        bottomRow = maxTopRow + rowCount - 1
//      } else if (minBottomRow < bottomRow) {
//        topRow = minBottomRow - rowCount + 1
//        bottomRow = minBottomRow
//      }
//
//      // 如果最终的位置没有发生改变则直接回到原位置
//      if (topRow == layoutParams.startRow
//        && bottomRow == layoutParams.endRow
//        && leftColumn == layoutParams.startColumn
//        && rightColumn == layoutParams.endColumn
//      ) {
//        return null
//      }
//
//      /*
//      * 第二次遍历：
//      * 1、对于修正后最终位置再次遍历子 View，寻找是否与其他子 View 有交集，若有，则回到原位置
//      * */
//      for (i in 0 until parent.childCount) {
//        val child = parent.getChildAt(i)
//        if (isSkipForeachJudge(child)) continue
//        when (itemTypeProvider.getItemType(child)) {
//          ItemType.AFFAIR_TOUCH, ItemType.SUBSTITUTE, ItemType.UNKNOWN -> continue
//          else -> {}
//        }
//        val lp = child.layoutParams as NetLayoutParams
//        val a1 = lp.startRow in topRow..bottomRow
//        val a2 = lp.endRow in topRow..bottomRow
//        val b1 = lp.startColumn in leftColumn..rightColumn
//        val b2 = lp.endColumn in leftColumn..rightColumn
//        if ((a1 || a2) && (b1 || b2)) {
//          return null
//        }
//      }
//      return Location(topRow, bottomRow, leftColumn, rightColumn)
//    }
//    return null
//  }
//
//  class Location(
//    override val startRow: Int,
//    override val endRow: Int,
//    override val startColumn: Int,
//    override val endColumn: Int
//  ) : INetBean
//}