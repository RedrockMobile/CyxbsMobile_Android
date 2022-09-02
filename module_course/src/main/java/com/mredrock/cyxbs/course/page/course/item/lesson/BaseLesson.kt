package com.mredrock.cyxbs.course.page.course.item.lesson

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
}