package com.mredrock.cyxbs.discover.othercourse.pages.stulist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.COURSE_OTHER_COURSE
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.network.Person
import com.mredrock.cyxbs.discover.othercourse.room.STUDENT_TYPE
import com.mredrock.cyxbs.discover.othercourse.room.TEACHER_TYPE
import kotlinx.android.synthetic.main.discover_other_course_item_rv_stu.view.*

/**
 * Created by zxzhu
 *   2018/10/19.
 *   enjoy it !!
 */
class StuListAdater(private val mList: List<Person>) : androidx.recyclerview.widget.RecyclerView.Adapter<StuListAdater.StuListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StuListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.discover_other_course_item_rv_stu, parent, false))

    override fun getItemCount() = mList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StuListViewHolder, position: Int) {
        holder.itemView.discover_other_course_item_select_name.text = mList[position].name
        holder.itemView.discover_other_course_item_select_class.text = mList[position].major
        holder.itemView.discover_other_course_item_select_num.text = mList[position].num
        holder.itemView.setOnClickListener {
            when (mList[position].type) {
                STUDENT_TYPE ->
                    ARouter.getInstance().build(COURSE_OTHER_COURSE).withString("stuNum", mList[position].num).navigation()
                TEACHER_TYPE ->
                    ARouter.getInstance().build(COURSE_OTHER_COURSE).withString("teaNum", mList[position].num).navigation()
            }

        }

    }

    class StuListViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)
}