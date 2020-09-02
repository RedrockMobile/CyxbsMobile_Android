package com.mredrock.cyxbs.course.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.common.config.COURSE_POS_TO_MAP
import com.mredrock.cyxbs.common.config.DISCOVER_MAP
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.Num2CN
import com.mredrock.cyxbs.common.utils.extensions.errorHandler
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.component.ScheduleDetailView
import com.mredrock.cyxbs.course.component.SimpleDotsView
import com.mredrock.cyxbs.course.event.DeleteAffairEvent
import com.mredrock.cyxbs.course.network.Course
import com.mredrock.cyxbs.course.network.CourseApiService
import com.mredrock.cyxbs.course.rxjava.ExecuteOnceObserver
import com.mredrock.cyxbs.course.ui.activity.AffairEditActivity
import com.mredrock.cyxbs.course.utils.CourseTimeParse
import com.mredrock.cyxbs.course.utils.affairFilter
import kotlinx.android.synthetic.main.course_affair_detail_item.view.*
import kotlinx.android.synthetic.main.course_course_detail_item.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by anriku on 2018/8/21.
 */
class ScheduleDetailViewAdapter(private val mDialog: Dialog, private val mSchedules: List<Course>) :
        ScheduleDetailView.Adapter {

    private val dayOfWeek = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    private val courseOfDay = listOf("一二节课", "三四节课", "五六节课", "七八节课", "九十节课", "十一十二节课")

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
            tv_course_exact_time.text = courseOfDay[itemViewInfo.hashLesson]
            tv_affair_name.text = itemViewInfo.course
            tv_affair_detail.text = itemViewInfo.classroom
            acb_modify.setOnClickListener {
                val activity = (mDialog.context as ContextThemeWrapper).baseContext as Activity
                activity.startActivity(Intent(activity, AffairEditActivity::class.java).apply {
                    putExtra(AffairEditActivity.AFFAIR_INFO, itemViewInfo)
                })
                mDialog.dismiss()
            }
            acb_delete.setOnClickListener {
                mCourseApiService.deleteAffair(itemViewInfo.courseId.toString())
                        .setSchedulers()
                        .errorHandler()
                        .affairFilter()
                        .subscribe(ExecuteOnceObserver(onExecuteOnceNext = {
                            it.apply {
                                //用于更新UI
                                EventBus.getDefault().post(DeleteAffairEvent())
                                CyxbsToast.makeText(context, R.string.course_transaction_deleted_successfully, Toast.LENGTH_SHORT).show()
                            }
                        }))
                mDialog.dismiss()
            }
        }
    }

    /**
     * 用来构造事务
     */
    private fun createAffairWeekStr(itemViewInfo: Course): String {
        val tvCourseWeekTextBuilder = StringBuilder("第")
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
            tvCourseWeekTextBuilder.append(if (it.size == 1) {
                Num2CN.number2ChineseNumber(it.first().toLong())
            } else {
                "${Num2CN.number2ChineseNumber(it.first().toLong())} 至 ${Num2CN.number2ChineseNumber(it.last().toLong())}"
            })
            tvCourseWeekTextBuilder.append(if (it == weekGroup.last()) {
                "周"
            } else {
                ","
            })
        }
        tvCourseWeekTextBuilder.append("   ")
        tvCourseWeekTextBuilder.append(dayOfWeek[itemViewInfo.hashDay])
        return tvCourseWeekTextBuilder.toString()
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
            val value = object : RedRockAutoWarpView.Adapter() {
                override fun getItemCount(): Int = 2
                override fun getItemView(position: Int): View? = TextView(context).apply {
                    setTextColor(ContextCompat.getColor(context, R.color.common_level_two_font_color))
                    textSize = 13f
                    when (position) {
                        0 -> {
                            //教室名跳转到对应地图
                            text = itemViewInfo.classroom
                            setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.course_ic_test_temp,0)
                            setOnClickListener {
                                //跳转到对应Map位置
                                ARouter.getInstance().build(DISCOVER_MAP).withString(COURSE_POS_TO_MAP, itemViewInfo.classroom).navigation()
                            }
                        }
                        1 -> text = itemViewInfo.teacher
                    }
                }
            }
            tv_course_classroom.adapter = value
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