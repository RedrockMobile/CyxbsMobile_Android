package com.mredrock.cyxbs.lib.course.fragment.course.base

import android.util.SparseArray
import androidx.core.util.forEach
import com.mredrock.cyxbs.lib.course.fragment.course.expose.container.ICourseContainer
import com.mredrock.cyxbs.lib.course.fragment.item.IAffair
import com.mredrock.cyxbs.lib.course.fragment.item.ILesson
import com.mredrock.cyxbs.lib.course.fragment.item.IOverlapItem
import com.mredrock.cyxbs.lib.course.internal.item.forEachRow
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.utils.getOrPut
import java.util.*

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 15:36
 */
abstract class ContainerImpl : AbstractCourseBaseFragment(), ICourseContainer {
  
  /**
   * 比较大小，用于重叠时的判断
   *
   * @return > 0，则 o1 显示在 o2 上；= 0，则 o1 会替换 o2（只建议在同一个对象时返回 0）
   */
  protected open fun compareOverlayItem(row: Int, column: Int, o1: IOverlapItem, o2: IOverlapItem): Int {
    return o1.compareTo(o2)
  }
  
  /**
   * 当 View 都被添加进来时的回调
   *
   * 添加 View 时会 post 一个 Runnable，然后在这个 Runnable 中回调该方法
   */
  protected open fun onAddIntoCourse() {}
  
  private val mLessons = arrayListOf<ILesson>()
  private val mAffairs = arrayListOf<IAffair>()
  
  private val mColumnAreas = SparseArray<ColumnArea>()
  
  final override fun addLesson(lesson: ILesson) {
    mColumnAreas.getOrPut(lesson.weekNum) { ColumnArea(it, course) }
      .addItem(lesson)
  }
  
  final override fun addLesson(lessons: List<ILesson>) {
    lessons.forEach { addLesson(it) }
  }
  
  final override fun removeLesson(lesson: ILesson) {
    mColumnAreas.get(lesson.weekNum)?.removeItem(lesson)
  }
  
  final override fun clearLesson() {
    mColumnAreas.forEach { _, value -> value.clearItem() }
  }
  
  final override fun addAffair(affair: IAffair) {
    mColumnAreas.getOrPut(affair.weekNum) { ColumnArea(it, course) }
      .addItem(affair)
  }
  
  final override fun addAffair(affairs: List<IAffair>) {
    affairs.forEach { addAffair(it) }
  }
  
  final override fun removeAffair(affair: IAffair) {
    mColumnAreas.get(affair.weekNum)?.removeItem(affair)
  }
  
  final override fun clearAffair() {
    mColumnAreas.forEach { _, value -> value.clearItem() }
  }
  
  /**
   * 管理每一列的一个工具类
   *
   * 因为目前课和事务都只能以一列存在，所以暂时这样设计
   */
  private inner class ColumnArea(
    val column: Int,
    val course: ICourseViewGroup,
  ) {
    private val items = arrayListOf<IOverlapItem>()
    private val newItems = arrayListOf<IOverlapItem>()
    private val grids = SparseArray<Grid>()
    private val runnable = Runnable {
      addIntoCourse()
    }
  
    fun addItem(item: IOverlapItem) {
      item.forEachRow { row ->
        grids.getOrPut(row) { Grid(it) }
          .addItem(item)
      }
      if (newItems.isEmpty()) {
        course.post(runnable)
      }
      newItems.add(item)
    }
    
    fun removeItem(item: IOverlapItem) {
      item.forEachRow {
        grids.get(it)?.removeItem(item)
      }
      item.clearOverlap()
      items.remove(item)
      newItems.remove(item)
    }
    
    fun clearItem() {
      items.forEach { it.clearOverlap() }
      newItems.forEach { it.clearOverlap() }
      items.clear()
      newItems.clear()
      grids.forEach { _, value ->
        value.clear()
      }
    }
    
    fun addIntoCourse() {
      val list = arrayListOf<IOverlapItem>()
      // 这里已经是下一个 Runnable，所有的 item 此时都已经添加进了 newItems
      newItems.forEach {
        if (it.isAllowToAddIntoCourse(course.getContext())) {
          when (it) {
            is ILesson -> course.addLessonItem(it)
            is IAffair -> course.addAffairItem(it)
            else -> course.addItem(it)
          }
          list.add(it)
        }
      }
      items.addAll(newItems)
      newItems.clear()
      onAddIntoCourse()
    }
  
    /**
     * 管理每个表格的工具类
     */
    private inner class Grid(val row: Int, ) {
  
      /**
       * TreeSet 该数据结构有个特点就是如果 Compare 比较出来等于 0，则会把之前那个给覆盖掉
       */
      private val sort = TreeSet<IOverlapItem> { o1, o2 ->
        compareOverlayItem(row, column, o1, o2)
      }
      
      fun addItem(item: IOverlapItem) {
        sort.add(item)
        val higher = sort.higher(item)
        higher?.onBelowItem(row, item)
        item.onAboveItem(row, higher)
        
        val lower = sort.lower(item)
        item.onBelowItem(row, lower)
        lower?.onAboveItem(row, item)
      }
    
      /**
       * 注意：这个 remove 必须要满足 Compare 比较等于 0 时才会被 remove 掉
       */
      fun removeItem(item: IOverlapItem) {
        val higher = sort.higher(item)
        val lower = sort.lower(item)
        higher?.onBelowItem(row, lower)
        lower?.onAboveItem(row, higher)
        sort.remove(item)
      }
      
      fun clear() {
        sort.clear()
      }
    }
  }
}