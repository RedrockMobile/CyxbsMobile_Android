package com.mredrock.cyxbs.discover.othercourse.pages.stulist

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.COURSE_OTHER_COURSE
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.network.Student
import kotlinx.android.synthetic.main.discover_other_course_item_rv_stu.view.*

/**
 * Created by zxzhu
 *   2018/10/19.
 *   enjoy it !!
 */
class StuListAdater(private val mStuList: List<Student>) : RecyclerView.Adapter<StuListAdater.StuListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = StuListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.discover_other_course_item_rv_stu, parent, false))

    override fun getItemCount() = mStuList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StuListViewHolder, position: Int) {
        holder.itemView.discover_other_course_item_select_name.text = mStuList[position].name
        holder.itemView.discover_other_course_item_select_class.text = mStuList[position].major + " " + mStuList[position].classnum
        holder.itemView.setOnClickListener {
            ARouter.getInstance().build(COURSE_OTHER_COURSE).withString("stuNum",mStuList[position].stunum).navigation()
        }

    }

    class StuListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}