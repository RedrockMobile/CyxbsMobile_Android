package com.mredrock.cyxbs.course.page.course.ui.dialog.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.course.page.course.data.ICourseItemData
/**
 * 暂时去掉密封类的使用，待R8完全支持后请改回来，原因：
 * 密封类在 D8 和 R8 编译器（产生错误的编译器）中不完全受支持。
 * 在 https://issuetracker.google.com/227160052 中跟踪完全支持的密封类。
 * D8支持将出现在Android Studio Electric Eel中，目前处于预览状态，而R8支持在更高版本之前不会出现
 * stackoverflow上的回答：
 * https://stackoverflow.com/questions/73453524/what-is-causing-this-error-com-android-tools-r8-internal-nc-sealed-classes-are#:~:text=This%20is%20due%20to%20building%20using%20JDK%2017,that%20the%20dexer%20won%27t%20be%20able%20to%20handle.
 */
/**
 *
 *
 * @author 985892345
 * @date 2022/9/17 17:59
 */
abstract class CourseViewHolder<T: ICourseItemData>(
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