package com.mredrock.cyxbs.course.adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.errorHandler
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.component.SimpleDotsView
import com.mredrock.cyxbs.course.component.ScheduleDetailView
import com.mredrock.cyxbs.course.database.ScheduleDatabase
import com.mredrock.cyxbs.course.event.DeleteAffairEvent
import com.mredrock.cyxbs.course.network.Course
import com.mredrock.cyxbs.course.network.CourseApiService
import com.mredrock.cyxbs.course.rxjava.ExecuteOnceObserver
import com.mredrock.cyxbs.course.ui.EditAffairActivity
import com.mredrock.cyxbs.course.ui.StudentListActivity
import com.mredrock.cyxbs.course.utils.CourseTimeParse
import org.greenrobot.eventbus.EventBus

/**
 * Created by anriku on 2018/8/21.
 */
class ScheduleDetailViewAdapter(private val mDialog: Dialog, private val mSchedules: List<Course>) :
        ScheduleDetailView.Adapter {

    private companion object {
        private const val TAG = "ScheduleDetailViewAdapter"
    }

    private val mCourseApiService: CourseApiService by lazy(LazyThreadSafetyMode.NONE) {
        ApiGenerator.getApiService(CourseApiService::class.java)
    }

    override fun getCourseDetailLayout(): Int {
        return R.layout.course_course_detail_item
    }

    override fun getAffairDetailLayout(): Int {
        return R.layout.course_affair_detail_item
    }

    override fun setScheduleContent(itemView: View, itemViewInfo: Course) {
        if (itemViewInfo.customType == Course.COURSE) {
            setCourseContent(itemView, itemViewInfo)
        } else {
            setAffairContent(itemView, itemViewInfo)
        }
    }


    override fun addDotsView(container: ViewGroup): ScheduleDetailView.DotsView {
        return SimpleDotsView(container.context).apply {
            dotsSize = mSchedules.size
        }
    }

    override fun setCurrentFocusDot(dotsView: ScheduleDetailView.DotsView, position: Int) {
        dotsView.setCurrentFocusDot(position)
    }


    override fun getSchedules(): List<Course> {
        return mSchedules
    }

    private fun setAffairContent(itemView: View, itemViewInfo: Course) {
        val affairName = itemView.findViewById<TextView>(R.id.tv_affair_name)
        val affairDetail = itemView.findViewById<TextView>(R.id.tv_affair_detail)
        val affairModify = itemView.findViewById<AppCompatButton>(R.id.acb_modify)
        val affairDelete = itemView.findViewById<AppCompatButton>(R.id.acb_delete)

        affairName.text = itemViewInfo.course
        affairDetail.text = itemViewInfo.classroom

        LogUtils.d(TAG, itemViewInfo.toString())
        affairModify.setOnClickListener {
            mDialog.context.startActivity(Intent(mDialog.context, EditAffairActivity::class.java).apply {
                putExtra(EditAffairActivity.AFFAIR_INFO, itemViewInfo)
            })
            mDialog.dismiss()
        }

        affairDelete.setOnClickListener {
            mCourseApiService.deleteAffair(BaseApp.user!!.stuNum!!, BaseApp.user!!.idNum!!,
                    itemViewInfo.courseId.toString())
                    .setSchedulers()
                    .errorHandler()
                    .subscribe(ExecuteOnceObserver(onExecuteOnceNext = { _ ->
                        //用于更新UI
                        EventBus.getDefault().post(DeleteAffairEvent())
                    }))
            mDialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCourseContent(itemView: View, itemViewInfo: Course) {
        itemView.findViewById<TextView>(R.id.tv_course_name).apply { text = itemViewInfo.course }
        itemView.findViewById<TextView>(R.id.tv_course_tech).apply { text = itemViewInfo.teacher }
        itemView.findViewById<TextView>(R.id.tv_course_classroom).apply { text = itemViewInfo.classroom }
        itemView.findViewById<TextView>(R.id.tv_course_time).apply {
            val courseTimeParse = CourseTimeParse(itemViewInfo.hashLesson * 2, itemViewInfo.period)
            text = """
            ${itemViewInfo.day}~${itemViewInfo.lesson}
            ${courseTimeParse.parseStartCourseTime()}~${courseTimeParse.parseEndCourseTime()}""".trimIndent()
        }
        itemView.findViewById<TextView>(R.id.tv_course_type).apply { text = itemViewInfo.type }
        itemView.findViewById<TextView>(R.id.tv_course_week).apply { text = itemViewInfo.rawWeek }

//        itemView.findViewById<TextView>(R.id.stu_list).apply {
//            setOnClickListener {
//                dialog.dismiss()
//                val context = dialog.context
//                val intent = Intent(context, StudentListActivity::class.java)
//                intent.putExtra(StudentListActivity.COURSE_INFO, itemViewInfo)
//                context.startActivity(intent)
//            }
//        }
    }

}