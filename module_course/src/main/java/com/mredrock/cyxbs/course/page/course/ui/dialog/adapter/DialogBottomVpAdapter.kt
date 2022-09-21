package com.mredrock.cyxbs.course.page.course.ui.dialog.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.course.page.course.data.ICourseData
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.data.TeaLessonData
import com.mredrock.cyxbs.course.page.course.ui.dialog.adapter.viewholder.AffairVH
import com.mredrock.cyxbs.course.page.course.ui.dialog.adapter.viewholder.CourseViewHolder
import com.mredrock.cyxbs.course.page.course.ui.dialog.adapter.viewholder.StuLessonVH
import com.mredrock.cyxbs.course.page.course.ui.dialog.adapter.viewholder.TeaLessonVH

/**
 *
 *
 * @author 985892345
 * @date 2022/9/17 17:50
 */
class DialogBottomVpAdapter(
  val data: List<ICourseData>,
  val isHomeCourse: Boolean
) : RecyclerView.Adapter<CourseViewHolder<*>>() {
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder<*> {
    return when (viewType) {
      AffairVH::class.hashCode() -> AffairVH(parent)
      StuLessonVH::class.hashCode() -> StuLessonVH(parent, isHomeCourse)
      TeaLessonVH::class.hashCode() -> TeaLessonVH(parent)
      else -> error("缺失该类型对应的 ViewHolder")
    }
  }
  
  override fun onBindViewHolder(holder: CourseViewHolder<*>, position: Int) {
    when (holder) {
      is AffairVH -> holder.onBindViewHolder(data[position] as AffairData)
      is StuLessonVH -> holder.onBindViewHolder(data[position] as StuLessonData)
      is TeaLessonVH -> holder.onBindViewHolder(data[position] as TeaLessonData)
    }
  }
  
  override fun getItemViewType(position: Int): Int {
    return when (data[position]) {
      is AffairData -> AffairVH::class.hashCode()
      is StuLessonData -> StuLessonVH::class.hashCode()
      is TeaLessonData -> TeaLessonVH::class.hashCode()
    }
  }
  
  override fun getItemCount(): Int {
    return data.size
  }
}