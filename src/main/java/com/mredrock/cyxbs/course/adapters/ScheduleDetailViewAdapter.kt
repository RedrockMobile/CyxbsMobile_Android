package com.mredrock.cyxbs.course.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.utils.Num2CN
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.errorHandler
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.component.ScheduleDetailView
import com.mredrock.cyxbs.course.component.SimpleDotsView
import com.mredrock.cyxbs.course.event.DeleteAffairEvent
import com.mredrock.cyxbs.course.network.Course
import com.mredrock.cyxbs.course.network.CourseApiService
import com.mredrock.cyxbs.course.rxjava.ExecuteOnceObserver
import com.mredrock.cyxbs.course.ui.EditAffairActivity
import com.mredrock.cyxbs.course.utils.CourseTimeParse
import kotlinx.android.synthetic.main.course_affair_detail_item.view.*
import kotlinx.android.synthetic.main.course_course_detail_item.view.*
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

    /**
     * 设置点击课表中事务后弹出的dialog内容
     * @param itemView dialog 的content View
     * @param itemViewInfo 点击的事务信息
     */
    private fun setAffairContent(itemView: View, itemViewInfo: Course) {
        itemView.apply {
            tv_course_cycle.text = createAffairWeekStr(itemViewInfo)
            tv_course_exact_time.text = listOf("一二节课", "三四节课", "五六节课", "七八节课", "九十节课", "十一十二节课")[itemViewInfo.hashLesson]
            tv_affair_name.text = itemViewInfo.course
            tv_affair_detail.text = itemViewInfo.classroom
            acb_modify.setOnClickListener {
                val activity = (mDialog.context as ContextThemeWrapper).baseContext as Activity
                activity.startActivity(Intent(activity, EditAffairActivity::class.java).apply {
                    putExtra(EditAffairActivity.AFFAIR_INFO, itemViewInfo)
                })
                mDialog.dismiss()
            }
            acb_delete.setOnClickListener {
                mCourseApiService.deleteAffair(ServiceManager.getService(IAccountService::class.java).getUserService().getStuNum(),
                                BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")!!,
                                itemViewInfo.courseId.toString())
                        .setSchedulers()
                        .errorHandler()
                        .subscribe(ExecuteOnceObserver(onExecuteOnceNext = {
                            //用于更新UI
                            EventBus.getDefault().post(DeleteAffairEvent())
                        }))
                mDialog.dismiss()
            }
        }
    }

    /**
     * 用来构造事务
     */
    private fun createAffairWeekStr(itemViewInfo: Course): String {
        var tvCourseWeekText = "第"
        val weekGroup = ArrayList<ArrayList<Int>>()
        var lastWeek: Int? = null
        itemViewInfo.week?.forEach {
            if (lastWeek != null && lastWeek!! + 1 == it) {
                weekGroup.last().add(it)
            } else {
                weekGroup.add(ArrayList<Int>().apply { add(it) })
            }
            lastWeek = it
        }
        weekGroup.forEach {
            tvCourseWeekText += if (it.size == 1) {
                Num2CN.number2ChineseNumber(it.first().toLong())
            } else {
                "${Num2CN.number2ChineseNumber(it.first().toLong())} 至 ${Num2CN.number2ChineseNumber(it.last().toLong())}"
            }
            tvCourseWeekText += if (it == weekGroup.last()) {
                "周"
            } else {
                ","
            }
        }
        tvCourseWeekText += "   "
        tvCourseWeekText += listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")[itemViewInfo.hashDay]
        return tvCourseWeekText
    }


    /**
     * 设置点击课表中课程后弹出的dialog内容
     * @param itemView dialog 的content View
     * @param itemViewInfo 点击的课程信息
     */
    @SuppressLint("SetTextI18n")
    private fun setCourseContent(itemView: View, itemViewInfo: Course) {
        itemView.apply {
            tv_course_name.apply { text = itemViewInfo.course }
            tv_course_tech.apply { text = itemViewInfo.teacher }
            tv_course_classroom.apply { text = itemViewInfo.classroom }
            tv_course_time.apply {
                val courseTimeParse = CourseTimeParse(itemViewInfo.hashLesson * 2, itemViewInfo.period)
                text = """${itemViewInfo.day}  ${courseTimeParse.parseStartCourseTime()}-${courseTimeParse.parseEndCourseTime()}""".trimIndent()
            }
            tv_course_type.apply { text = itemViewInfo.type }
            tv_course_week.apply { text = itemViewInfo.rawWeek }
            if (itemViewInfo.classNumber.isNotEmpty() && itemViewInfo.classNumber[0].isNotEmpty()) {
                course_tea_class.visibility = View.VISIBLE
                rrawv_course_class.adapter = TeaClassAdapter(itemViewInfo.classNumber)
            }
        }
    }

}