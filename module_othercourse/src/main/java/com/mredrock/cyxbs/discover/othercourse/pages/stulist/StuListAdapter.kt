package com.mredrock.cyxbs.discover.othercourse.pages.stulist

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.mredrock.cyxbs.common.config.COURSE_ENTRY
import com.mredrock.cyxbs.common.config.OTHERS_STU_NUM
import com.mredrock.cyxbs.common.config.OTHERS_TEA_NAME
import com.mredrock.cyxbs.common.config.OTHERS_TEA_NUM
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.network.Person
import com.mredrock.cyxbs.discover.othercourse.room.STUDENT_TYPE
import com.mredrock.cyxbs.discover.othercourse.room.TEACHER_TYPE
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * Created by zxzhu
 *   2018/10/19.
 *   enjoy it !!
 */
class StuListAdapter(
    val stuListActivity: StuListActivity,
    private val mList: List<Person>
) : RecyclerView.Adapter<StuListAdapter.StuListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StuListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.othercourse_discover_other_course_item_rv_stu, parent, false)
        val holder = StuListViewHolder(view)
        view.setOnSingleClickListener {
            val position = holder.layoutPosition
            when (mList[position].type) {
                STUDENT_TYPE -> {
                    openCourseFragment(OTHERS_STU_NUM, position)
                    Observable.just("")
                        .subscribeOn(Schedulers.io())
                        .safeSubscribeBy {
                            stuListActivity.database
                                .getHistoryDao()
                                .updateHistory(stuListActivity.historyId, mList[position].num)
                        }
                }
                TEACHER_TYPE -> {
                    openCourseFragment(OTHERS_TEA_NUM, position)
                    Observable.just("")
                        .subscribeOn(Schedulers.io())
                        .safeSubscribeBy {
                            stuListActivity.database
                                .getHistoryDao()
                                .updateHistory(stuListActivity.historyId, mList[position].num)
                        }
                }
            }
        }
        return holder
    }

    override fun getItemCount() = mList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StuListViewHolder, position: Int) {
        holder.tvName.text = mList[position].name
        holder.tvClass.text = mList[position].major
        holder.tvStuNum.text = mList[position].num
    }

    private fun openCourseFragment(key: String, position: Int) {
        val fragment = (ARouter.getInstance().build(COURSE_ENTRY).navigation() as Fragment).apply {
            arguments = Bundle().apply {
                putString(key, mList[position].num)
                //如果是老师，则需要额外添加name属性
                if (key == OTHERS_TEA_NUM) {
                    putString(OTHERS_TEA_NAME, mList[position].name)
                }
            }
        }
        /*
        * 这里是搜索后点击具体同学的跳转
        *
        * 在滑动下拉课表容器中添加整个课表
        * */
        stuListActivity.supportFragmentManager.beginTransaction().replace(R.id.course_bottom_sheet_content, fragment).commit()
        stuListActivity.bottomSheetBehavior.state = STATE_EXPANDED
    }


    class StuListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.discover_other_course_item_select_name)
        val tvClass: TextView = itemView.findViewById(R.id.discover_other_course_item_select_class)
        val tvStuNum: TextView = itemView.findViewById(R.id.other_course_tv_stu_num)
    }
}