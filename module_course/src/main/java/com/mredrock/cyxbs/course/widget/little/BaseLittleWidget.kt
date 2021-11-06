package com.mredrock.cyxbs.course.widget.little

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.main.MAIN_MAIN
import com.mredrock.cyxbs.common.event.WidgetCourseEvent
import com.mredrock.cyxbs.course.bean.CourseStatus
import com.mredrock.cyxbs.course.utils.*
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by zia on 2018/10/10.
 * 精简版桌面小控件
 */
abstract class BaseLittleWidget : AppWidgetProvider() {

    private val shareName = "share_hash_lesson_trans"
    private var curCourseStatus: CourseStatus.Course = CourseStatus.Course()//保存当前显示的course

    @LayoutRes
    protected abstract fun getLayoutResId(): Int

    protected abstract fun getShareName(): String
    @IdRes
    protected abstract fun getUpResId(): Int

    @IdRes
    protected abstract fun getDownResId(): Int

    @IdRes
    protected abstract fun getTitleResId(): Int

    @IdRes
    protected abstract fun getCourseNameResId(): Int

    @IdRes
    protected abstract fun getRoomResId(): Int

    @IdRes
    protected abstract fun getRefreshResId(): Int


    protected abstract fun getRemoteViews(context: Context, courseStatus: CourseStatus.Course?, timeTv: String = "课程安排"): RemoteViews


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        refresh(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == "btn.text.com") {
            val data = intent.data
            var rId = -1
            if (data != null) {
                rId = data.schemeSpecificPart.toInt()
            }
            if (rId == getUpResId() && getUpResId() != 0) {
                goUp(context)
            } else if (rId == getDownResId() && getDownResId() != 0) {
                goDown(context)
            } else if (rId == getRefreshResId() && getRefreshResId() != 0) {
                refresh(context)
                Toast.makeText(context, "已刷新", Toast.LENGTH_SHORT).show()
            }
        }

        if(intent.action == "btn.start.com"){
            ARouter.getInstance().build(MAIN_MAIN).navigation()
            refresh(context)//在应用没有打开的时候点击跳转需要刷新一下数据
            EventBus.getDefault().postSticky(
                    WidgetCourseEvent(mutableListOf(changeCourseToWidgetCourse(curCourseStatus)))
            )
        }
    }

    private fun show(context: Context, courseStatus: CourseStatus.Course?, startTime: String = "课程安排") {
        val manager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, javaClass)
        curCourseStatus = courseStatus ?: CourseStatus.Course()
        manager.updateAppWidget(componentName, getRemoteViews(context, courseStatus, startTime))
    }

    //获取正常显示的下一节课
    fun refresh(context: Context) {
        try {//catch异常，避免课表挂了之后这边跟着挂
            saveDayOffset(context, 0)//重置天数偏移
            val list = getTodayCourse(context)
                    ?: getErrorCourseList()

            var isFound = false
            list.forEach {
                val endCalendar = getStartCalendarByNum(it.hash_lesson)
                //如果今天还有下一节课，显示下一节
                if (Calendar.getInstance() < endCalendar) {
                    show(context, it, formatTime(getStartCalendarByNum(it.hash_lesson)))
                    return
                }
                isFound = true
            }

            if (isFound) {//今天有课，但是上完了
                //新策略：显示明天第一节
                showTomorrowCourse(context)
            } else {//今天没有课
                if (isNight()) {//如果在晚上，显示明天课程
                    showTomorrowCourse(context)
                } else {
                    //白天显示今天无课
                    show(context, null)
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun showTomorrowCourse(context: Context) {
        saveDayOffset(context, 1)//天数偏移加一，上下切换课程将会切换明天的

        val tomorrowCalendar = Calendar.getInstance()
        tomorrowCalendar.set(Calendar.DAY_OF_YEAR, tomorrowCalendar.get(Calendar.DAY_OF_YEAR) + 1)
        val tomorrowList = getCourseByCalendar(context, tomorrowCalendar)
        if (tomorrowList == null) {//数据出错
            show(context, getErrorCourseList().first(), "掌上重邮")
        } else {
            if (tomorrowList.isEmpty()) {//明日无课
                show(context, getNoCourse(), "明日课程")
            } else {//显示明天第一节课
                show(context, tomorrowList.first(), "明日：" + formatTime(getStartCalendarByNum(tomorrowList.first().hash_lesson)))
            }
        }
    }

    //获取当前课程的上一节课
    private fun goUp(context: Context) {
        val hash_lesson = getHashLesson(context, getShareName())

        val dayOffset = getDayOffset(context)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + dayOffset)

        //拿到当前课程的上一个课程
        val list = getCourseByCalendar(context, calendar)
                ?: getErrorCourseList()

        var courseStatus: CourseStatus.Course? = null
        list.forEach {
            if (hash_lesson > it.hash_lesson) {
                courseStatus = it
            }
        }
        if (courseStatus != null) {
            val beforeTitle = if (dayOffset == 1) "明日：" else ""
            show(context, courseStatus, beforeTitle + formatTime(getStartCalendarByNum(courseStatus!!.hash_lesson)))
        } else {
            Toast.makeText(context, "没有上一节课了~", Toast.LENGTH_SHORT).show()
        }
    }

    //获取当前课程的下一节课
    private fun goDown(context: Context) {
        val hash_lesson = getHashLesson(context, getShareName())

        val dayOffset = getDayOffset(context)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + dayOffset)

        //拿到最后一节
        val list = getCourseByCalendar(context, calendar)
                ?: getErrorCourseList()
        list.forEach {
            if (hash_lesson < it.hash_lesson) {
                val beforeTitle = if (dayOffset == 1) "明日：" else ""
                show(context, it, beforeTitle + formatTime(getStartCalendarByNum(it.hash_lesson)))
                return
            }
        }
        Toast.makeText(context, "没有下一节课了~", Toast.LENGTH_SHORT).show()
    }
}