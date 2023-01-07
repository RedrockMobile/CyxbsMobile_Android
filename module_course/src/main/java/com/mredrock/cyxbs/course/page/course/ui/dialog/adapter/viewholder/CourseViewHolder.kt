package com.mredrock.cyxbs.course.page.course.ui.dialog.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.course.page.course.data.ICourseItemData

/**
 *
 *
 * @author 985892345
 * @date 2022/9/17 17:59
 */
sealed class CourseViewHolder<T: ICourseItemData>(
  parent: ViewGroup,
  @LayoutRes layoutId: Int
) : RecyclerView.ViewHolder(
  LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
) {
  abstract fun onBindViewHolder(data: T)
  
  fun <T: View> findViewById(@IdRes id: Int): T {
    return itemView.findViewById(id)
  }
}