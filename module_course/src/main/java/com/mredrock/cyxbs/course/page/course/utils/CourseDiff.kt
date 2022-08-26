package com.mredrock.cyxbs.course.page.course.utils

import androidx.annotation.WorkerThread
import androidx.recyclerview.widget.DiffUtil
import com.mredrock.cyxbs.course.page.course.data.StuLessonData

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/3 16:56
 */
object CourseDiff {
  
  /**
   * 差分比较，之后有时间再上这个功能
   */
  @WorkerThread
  fun start(
    stuNum: String,
    newList: List<StuLessonData>,
    oldList: List<StuLessonData>,
  ): Result {
    return Result(
      stuNum, newList, oldList,
      DiffUtil.calculateDiff(
        object : DiffUtil.Callback() {
          override fun getOldListSize(): Int = oldList.size
          override fun getNewListSize(): Int = newList.size
          
          override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return StuLessonData.DIFF_UTIL.areItemsTheSame(
              oldList[oldItemPosition],
              newList[newItemPosition]
            )
          }
          
          override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return StuLessonData.DIFF_UTIL.areContentsTheSame(
              oldList[oldItemPosition],
              newList[newItemPosition]
            )
          }
        }
      )
    )
  }
  
  class Result(
    val stuNum: String,
    val newList: List<StuLessonData>,
    val oldList: List<StuLessonData>,
    val diffResult: DiffUtil.DiffResult
  )
}