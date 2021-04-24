package com.mredrock.cyxbs.discover.othercourse.pages.stulist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.mredrock.cyxbs.common.config.COURSE_ENTRY
import com.mredrock.cyxbs.common.config.OTHERS_STU_NUM
import com.mredrock.cyxbs.common.config.OTHERS_TEA_NAME
import com.mredrock.cyxbs.common.config.OTHERS_TEA_NUM
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.network.Person
import com.mredrock.cyxbs.discover.othercourse.room.STUDENT_TYPE
import com.mredrock.cyxbs.discover.othercourse.room.TEACHER_TYPE
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.othercourse_discover_activity_other_course.*
import kotlinx.android.synthetic.main.othercourse_discover_activity_stu_list.*
import kotlinx.android.synthetic.main.othercourse_discover_other_course_item_rv_stu.view.*

/**
 * Created by zxzhu
 *   2018/10/19.
 *   enjoy it !!
 */
class StuListAdapter(val stuListActivity: StuListActivity, private val mList: List<Person>) : androidx.recyclerview.widget.RecyclerView.Adapter<StuListAdapter.StuListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StuListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.othercourse_discover_other_course_item_rv_stu, parent, false))

    override fun getItemCount() = mList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StuListViewHolder, position: Int) {

        holder.itemView.apply {
            discover_other_course_item_select_name.text = mList[position].name
            discover_other_course_item_select_class.text = mList[position].major
            other_course_tv_stu_num.text = mList[position].num
            setOnClickListener {
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
        }
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
        //在滑动下拉课表容器中添加整个课表
        stuListActivity.supportFragmentManager.beginTransaction().replace(R.id.course_bottom_sheet_content, fragment).apply {
            commit()
        }
        stuListActivity.bottomSheetBehavior.state = STATE_EXPANDED
    }


    class StuListViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)
}