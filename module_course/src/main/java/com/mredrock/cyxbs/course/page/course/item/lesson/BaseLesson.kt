package com.mredrock.cyxbs.course.page.course.item.lesson

import androidx.core.view.forEach
import com.mredrock.cyxbs.course.page.course.item.view.ItemView
import com.mredrock.cyxbs.lib.course.internal.item.forEachRow
import com.mredrock.cyxbs.lib.course.internal.lesson.ILessonData
import com.mredrock.cyxbs.lib.course.item.AbstractLesson

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:42
 */
sealed class BaseLesson(data: ILessonData) : AbstractLesson(data) {
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
}