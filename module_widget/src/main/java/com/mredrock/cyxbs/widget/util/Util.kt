package com.mredrock.cyxbs.widget.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.IdRes
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.mredrock.cyxbs.common.bean.WidgetCourse
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.WIDGET_COURSE
import com.mredrock.cyxbs.common.event.WidgetCourseEvent
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.MAIN_MAIN
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.widget.bean.CourseStatus
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by zia on 2018/10/10.
 * 精力憔悴，这些方法直接揉在一起了
 */

const val EmptyCourseObject = -1

/**
 * 获得今天得课程list信息
 */
fun getTodayCourse(context: Context): List<CourseStatus.Course>? {
    return getCourseByCalendar(context, Calendar.getInstance())
}

fun getCourseByCalendar(context: Context, calendar: Calendar): ArrayList<CourseStatus.Course>? {
    val json = context.defaultSharedPreferences.getString(WIDGET_COURSE, "")
    val course = Gson().fromJson<CourseStatus>(json, CourseStatus::class.java) ?: return null
    if (course.data == null) return null
    val week = SchoolCalendar().weekOfTerm
    /*
    * 转换表，老外从周日开始计数,orz
    * 7 1 2 3 4 5 6 老外
    * 1 2 3 4 5 6 7 Calendar.DAY_OF_WEEK
    * 6 0 1 2 3 4 5 需要的结果(hash_day)
    * */
    val hash_day = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7

    val list = ArrayList<CourseStatus.Course>()
    course.data!!.forEach {
        if (it.hash_day == hash_day && it.week!!.contains(week)) {
//            LogUtils.d("Widget", it.toString())
            list.add(it)
        }
    }
    list.sortBy { it.hash_lesson }
    return list
}

fun getErrorCourseList(): ArrayList<CourseStatus.Course> {
    val data = CourseStatus.Course()
    data.hash_lesson = 0
    data.course = "数据异常，请刷新"
    data.classroom = ""
    val list = ArrayList<CourseStatus.Course>()
    list.add(data)
    return list
}

fun getNoCourse(): CourseStatus.Course {
    val data = CourseStatus.Course()
    data.hash_lesson = EmptyCourseObject
    data.course = "无课"
    data.classroom = ""
    return data
}

fun saveHashLesson(context: Context, hash_lesson: Int, shareName: String) {
    context.defaultSharedPreferences.editor {
        putInt(shareName, hash_lesson)
    }
}

fun getHashLesson(context: Context, shareName: String): Int {
    return context.defaultSharedPreferences.getInt(shareName, 0)
}


private const val SP_DayOffset = "dayOffset"

//天数偏移量，用于LittleWidget切换明天课程
fun saveDayOffset(context: Context, offset: Int) {
    context.defaultSharedPreferences.editor {
        putInt(SP_DayOffset, offset)
    }
}

fun getDayOffset(context: Context): Int {
    return context.defaultSharedPreferences.getInt(SP_DayOffset, 0)
}


fun isNight(): Boolean {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.HOUR_OF_DAY) > 19
}

/**
 * hash_lesson == 0 第1节 返回8:00
 */
fun getStartCalendarByNum(hash_lesson: Int): Calendar {
    val calendar = Calendar.getInstance()
    when (hash_lesson) {
        0 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, 0)
        }
        1 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 10)
            calendar.set(Calendar.MINUTE, 15)
        }
        2 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 14)
            calendar.set(Calendar.MINUTE, 0)
        }
        3 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 16)
            calendar.set(Calendar.MINUTE, 15)
        }
        4 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 19)
            calendar.set(Calendar.MINUTE, 0)
        }
        5 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 20)
            calendar.set(Calendar.MINUTE, 50)
        }
    }
    return calendar
}

fun getWeekDayChineseName(weekDay: Int): String {
    return when (weekDay) {
        1 -> "周日"
        2 -> "周一"
        3 -> "周二"
        4 -> "周三"
        5 -> "周四"
        6 -> "周五"
        7 -> "周六"
        else -> "null"
    }
}

fun getClickPendingIntent(context: Context, @IdRes resId: Int, action: String, clazz: Class<*>): PendingIntent {
    val intent = Intent()
    intent.setClass(context, clazz)
    intent.action = action
    intent.data = Uri.parse("id:$resId")

    return PendingIntent.getBroadcast(context, 0, intent, getPendingIntentFlags())
}

//给按钮返回PendingIntent
fun getClickIntent(context: Context, widgetId: Int, viewId: Int, requestCode: Int, action: String, clazz: Class<*>): PendingIntent? {
    //pendingintent中需要的intent，绑定这个类和当前context
    val i = Intent(context, clazz)
    //设置action
    i.action = action //设置更新动作
    //设置bundle
    val bundle = Bundle()
    //将widgetId放进bundle
    bundle.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
    //放进需要设置的viewId
    bundle.putInt("Button", viewId)
    i.putExtras(bundle)
    return PendingIntent.getBroadcast(context, requestCode, i,getPendingIntentFlags())
}

fun formatTime(calendar: Calendar): String {
    return SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE).format(calendar.time)
}

fun filterClassRoom(classRoom: String): String {
    return if (classRoom.length > 8) {
        classRoom.replace(Regex("[\\u4e00-\\u9fa5()（）]"), "")
    } else {
        classRoom
    }
}

//将widget模块的course转换为lib模块的WidgetCourse，WidgetCourse达到中转作用
fun changeCourseToWidgetCourse(courseStatusBean: CourseStatus.Course): WidgetCourse.DataBean {
    val bean = WidgetCourse.DataBean()
    bean.apply {
        hash_day = courseStatusBean.hash_day
        hash_lesson = courseStatusBean.hash_lesson
        begin_lesson = courseStatusBean.begin_lesson
        day = courseStatusBean.day
        lesson = courseStatusBean.lesson
        course = courseStatusBean.course
        course_num = courseStatusBean.course_num
        teacher = courseStatusBean.teacher
        classroom = courseStatusBean.classroom
        rawWeek = courseStatusBean.rawWeek
        weekModel = courseStatusBean.weekModel
        weekBegin = courseStatusBean.weekBegin
        weekEnd = courseStatusBean.weekEnd
        week = courseStatusBean.week
        type = courseStatusBean.type
        period = courseStatusBean.period
    }
    return bean
}

fun startOperation(dataBean: WidgetCourse.DataBean) {
    if (!ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
        CyxbsToast.makeText(BaseApp.appContext, "请登录之后再点击查看详细信息", Toast.LENGTH_SHORT).show()
    } else {
        ARouter.getInstance().build(MAIN_MAIN).navigation()
        EventBus.getDefault().postSticky(WidgetCourseEvent(mutableListOf(dataBean)))
    }
}
private fun getPendingIntentFlags(isMutable: Boolean = true) =
    when {
        isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        !isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
            PendingIntent.FLAG_UPDATE_CURRENT
        !isMutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
            PendingIntent.FLAG_CANCEL_CURRENT
        else -> PendingIntent.FLAG_UPDATE_CURRENT
    }
