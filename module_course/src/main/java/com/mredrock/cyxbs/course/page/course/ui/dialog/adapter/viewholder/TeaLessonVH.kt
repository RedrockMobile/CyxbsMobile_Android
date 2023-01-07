package com.mredrock.cyxbs.course.page.course.ui.dialog.adapter.viewholder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.data.TeaLessonData

/**
 *
 *
 * @author 985892345
 * @date 2022/9/17 18:03
 */
class TeaLessonVH(
  parent: ViewGroup
) : CourseViewHolder<TeaLessonData>(parent, R.layout.course_dialog_bottom_lesson_tea) {
  
  private val mTvTitle = findViewById<TextView>(R.id.course_tv_dialog_tea_course)
  private val mTvClassroom = findViewById<TextView>(R.id.course_tv_dialog_tea_classroom)
  private val mTvTeacher = findViewById<TextView>(R.id.course_tv_dialog_tea_teacher)
  private val mTvRawWeek = findViewById<TextView>(R.id.course_tv_dialog_tea_rawWeek)
  private val mTvTime = findViewById<TextView>(R.id.course_tv_dialog_tea_time)
  private val mTvType = findViewById<TextView>(R.id.course_tv_dialog_tea_type)
  
  private val mRvClass = findViewById<RecyclerView>(R.id.course_rv_dialog_tea_class)
  
  init {
    // 在 init 中初始化 rv，防止因为外面的 rv 复用而重复加载里面的 rv
    mRvClass.layoutManager =
      FlexboxLayoutManager(itemView.context, FlexDirection.ROW, FlexWrap.WRAP)
    mRvClass.adapter = ClassRvAdapter()
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(data: TeaLessonData) {
    mTvTitle.text = data.course.course
    mTvClassroom.text = data.course.classroom
    mTvTeacher.text = data.course.teacher
    mTvRawWeek.text = data.course.rawWeek
    mTvTime.text = "${data.weekdayStr} ${data.durationStr}"
    mTvType.text = data.course.type
  
    (mRvClass.adapter as ClassRvAdapter).submitList(data.classNumber)
  }
  
  /**
   * 虽然我知道这个很简单，没必要上 ListAdapter 差分刷新，但管他的，刷就完事了 :)
   */
  private class ClassRvAdapter : ListAdapter<String, ClassRvAdapter.ClassVH>(
    object : DiffUtil.ItemCallback<String>() {
      override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
      }
      
      override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return true // areContentsTheSame() 只会在 areItemsTheSame() 返回 true 时回调，所以不用重复比较
      }
      
      override fun getChangePayload(oldItem: String, newItem: String): Any {
        return "" // 不返回 null，避免 ViewHolder 互换
      }
    }
  ) {
    class ClassVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val tvClass: TextView = itemView.findViewById(R.id.course_tv_dialog_bottom_tea_item)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassVH {
      return ClassVH(
        LayoutInflater.from(parent.context)
          .inflate(R.layout.course_rv_item_dialog_bottom_lesson_tea, parent, false)
      )
    }
    
    override fun onBindViewHolder(holder: ClassVH, position: Int) {
      holder.tvClass.text = getItem(position)
    }
  }
}