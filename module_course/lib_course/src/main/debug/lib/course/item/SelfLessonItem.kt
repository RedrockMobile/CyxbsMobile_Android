package lib.course.item

import android.content.Context
import android.view.View
import androidx.core.view.forEach
import com.mredrock.cyxbs.lib.course.fragment.page.item.IOverlapItem
import com.mredrock.cyxbs.lib.course.internal.lesson.BaseLessonLayoutParams
import com.mredrock.cyxbs.lib.course.item.AbstractLesson
import lib.course.data.LessonData
import lib.course.item.lp.SelfLessonLp
import lib.course.item.view.SelfLessonView

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 18:09
 */
class SelfLessonItem(
  val data: LessonData
) : AbstractLesson(data) {
  
  override val lp: BaseLessonLayoutParams = SelfLessonLp(data)
  
  override fun createView(context: Context, startNode: Int, length: Int): View {
    return SelfLessonView(context).apply {
      setData(data)
    }
  }
  
  override fun onAddIntoCourse() {
    super.onAddIntoCourse()
    forEachPosition {
      if (getBelowItem(it) != null) {
        view.forEach { child ->
          if (child is SelfLessonView) {
            child.isShowOverlapTag(true)
          }
        }
        return
      }
    }
  }
  
  /**
   * item 重叠时的比较
   *
   * 越后面的数据会显示在越上面
   *
   * 请记住：
   * return 正数，other 在下
   * return 负数，other 在上
   *
   */
  override fun compareTo(other: IOverlapItem): Int {
    if (this === other) return 0
    if (other is SelfLessonItem) {
      if (data.week < other.data.week) return -1
      if (data.week > other.data.week) return 1
      if (weekNum < other.weekNum) return -1
      if (weekNum > other.weekNum) return 1
      val t1 = startRow
      val b1 = endRow
      val t2 = other.startRow
      val b2 = other.endRow
      // 这里以后修改的话尽量写清楚逻辑，可读性优先
      if (b1 < t2) {
        // 这是两节课都没有交叉的时候
        return t1 - t2
      } else {
        if (t1 <= t2 && b1 >= b2) {
          // 完全包含 other
          return 1
        } else if (t1 < t2) {
          // 这里是两节课互相有交叉，但却没有完全包含
          return 1
        }
        // 下面这个与上面的逻辑相反，因为 o1 变成了 o2，o2 变成了 o1
        return -other.compareTo(this)
      }
    } else return 1
  }
}