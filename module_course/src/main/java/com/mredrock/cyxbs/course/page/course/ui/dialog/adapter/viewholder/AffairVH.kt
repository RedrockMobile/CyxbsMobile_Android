package com.mredrock.cyxbs.course.page.course.ui.dialog.adapter.viewholder

import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener

/**
 *
 *
 * @author 985892345
 * @date 2022/9/17 18:03
 */
class AffairVH(
  parent: ViewGroup
) : CourseViewHolder<AffairData>(parent, R.layout.course_dialog_bottom_affair) {
  
  private val mTvTitle = findViewById<TextView>(R.id.course_tv_dialog_affair_title)
  private val mTvContent = findViewById<TextView>(R.id.course_tv_dialog_affair_content)
  private val mTvDuration = findViewById<TextView>(R.id.course_tv_dialog_affair_duration)
  private val mBtnDelete = findViewById<Button>(R.id.course_btn_dialog_affair_delete)
  private val mBtnChange = findViewById<Button>(R.id.course_btn_dialog_affair_change)
  
  override fun onBindViewHolder(data: AffairData) {
    mTvTitle.text = data.title
    mTvContent.text = data.content
    mTvDuration.text = "${data.weekStr} ${data.weekdayStr}   ${data.durationStr}"
    mBtnDelete.setOnSingleClickListener {
      // TODO
    }
    mBtnChange.setOnSingleClickListener {
      // TODO
    }
  }
}