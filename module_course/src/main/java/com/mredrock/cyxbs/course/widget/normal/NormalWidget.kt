package com.mredrock.cyxbs.course.widget.normal

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.IdRes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.bean.WidgetCourse
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.bean.CourseStatus
import com.mredrock.cyxbs.course.utils.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by zia on 2018/10/10.
 * 大课表
 *
 */
class NormalWidget : AppWidgetProvider() {

    private val shareName = "zscy_widget_normal"
    private val courseData = "courseData"


    //生成calendar
    private val calendar = Calendar.getInstance()
    private var gson = Gson()
    private var list = ArrayList<CourseStatus.Course>()

    companion object {
        private var lastClickTime: Long = 0//用于记录点击时间
        //用于保存每一条刷新的课程，多个方法都要使用，若非静态，其他方法无法调用到正确数据
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        context.defaultSharedPreferences.editor {
            putInt(shareName, 0)
        }
        fresh(context, 0)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        val minHeight = newOptions.getInt(OPTION_APPWIDGET_MIN_HEIGHT)
        val rv = RemoteViews(context.packageName, R.layout.course_normal)
        when {
            minHeight <= 100 -> setTextMaxLines(rv, 2)
            minHeight <= 110 -> setTextMaxLines(rv, 3)
            else -> setTextMaxLines(rv, 10)
        }
        show(rv, context)
    }

    private fun setTextMaxLines(rv: RemoteViews, i1: Int) {
        for (i in 1..6) {
            rv.setInt(getCourseId(i), "setMaxLines", i1)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val data = intent.data
        var rId = -1
        if (data != null) {
            rId = data.schemeSpecificPart.toInt()
        }
        if (intent.action == "btn.text.com") {

            val offsetTime = context.defaultSharedPreferences.getInt(shareName, 0)

            when (rId) {
                R.id.widget_normal_back -> {
                    context.defaultSharedPreferences.editor {
                        putInt(shareName, makeOffsetTime(offsetTime - 1))
                    }
                    fresh(context, makeOffsetTime(offsetTime - 1))
                }
                R.id.widget_normal_front -> {
                    context.defaultSharedPreferences.editor {
                        putInt(shareName, makeOffsetTime(offsetTime + 1))
                    }
                    fresh(context, makeOffsetTime(offsetTime + 1))
                }
                R.id.widget_normal_title -> {
                    context.defaultSharedPreferences.editor {
                        putInt(shareName, 0)
                    }
                    fresh(context, 0)
                }
            }
            if (isDoubleClick()) {
                CyxbsToast.makeText(context, "提示：点击星期返回今日", Toast.LENGTH_SHORT).show()
            }
        }
        if (intent.action == "btn.start.com") {
            list = gson.fromJson(context.defaultSharedPreferences.getString(courseData, ""),
                object : TypeToken<ArrayList<CourseStatus.Course>>() {}.type)
            val newList = mutableListOf<WidgetCourse.DataBean>()
            list.forEach {
                newList.add(changeCourseToWidgetCourse(it))
            }
            when (rId) {
                R.id.widget_normal_layout1 -> {
                    startOperation(newList.filter { it.hash_lesson == 0 }[0])
                }
                R.id.widget_normal_layout2 -> {
                    startOperation(newList.filter { it.hash_lesson == 1 }[0])
                }
                R.id.widget_normal_layout3 -> {
                    startOperation(newList.filter { it.hash_lesson == 2 }[0])
                }
                R.id.widget_normal_layout4 -> {
                    startOperation(newList.filter { it.hash_lesson == 3 }[0])
                }
                R.id.widget_normal_layout5 -> {
                    startOperation(newList.filter { it.hash_lesson == 4 }[0])
                }
                R.id.widget_normal_layout6 -> {
                    startOperation(newList.filter { it.hash_lesson == 5 }[0])
                }
            }

        }
    }

    private fun makeOffsetTime(offsetTime: Int): Int {
        val i1 = Calendar.getInstance()[Calendar.DAY_OF_WEEK]
        val dayOfWeek = if (i1 == 1) 7 else i1 - 1
        val i = 7 - dayOfWeek
        val i2 = -dayOfWeek + 1
        if (offsetTime > i) {
            return (offsetTime - i) % 7 - dayOfWeek
        }
        if (offsetTime < i2) {
            return 8 + (offsetTime - i2) % 7 - dayOfWeek
        }
        return offsetTime
    }


    private fun isDoubleClick(): Boolean {
        val time = System.currentTimeMillis()
        val anotherTime = time - lastClickTime

        if (anotherTime < 150) {
            return true
        }
        lastClickTime = time
        return false
    }


    /**
     * 刷新，传入offsetTime作为今天的偏移量
     */
    private fun fresh(context: Context, offsetTime: Int) {
        val rv = RemoteViews(context.packageName, R.layout.course_normal)
        initView(rv, context)
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + offsetTime)
        //获取数据
        getCourseByCalendar(context, calendar).safeSubscribeBy {
            list = it
            //显示星期几
            val text =
                if (Calendar.getInstance()[Calendar.DAY_OF_WEEK] == calendar[Calendar.DAY_OF_WEEK]) "今" else getWeekDayChineseName(
                    calendar.get(Calendar.DAY_OF_WEEK))
            rv.setTextViewText(R.id.widget_normal_title, text)

            //显示课程
            list.forEach { course ->
                val num = course.hash_lesson + 1
                if (course.period == 2) {
                    rv.setTextViewText(getCourseId(num), course.course)
                    rv.setTextViewText(getRoomId(num), filterClassRoom(course.classroom!!))
                    rv.setOnClickPendingIntent(getLayoutId(num),
                        getClickPendingIntent(context,
                            getLayoutId(num),
                            "btn.start.com",
                            javaClass))
                } else if (course.period == 3) {
                    setMoreView(num, rv, course, context)

                } else if (course.period == 4) {
                    setMoreView(num, rv, course, context)
                    rv.setViewVisibility(getMoreViewId((num + 1) / 2), View.GONE)
                }

            }

            val listLesson = list.map { it.hash_lesson }
            MutableList(6) { it }.filter { !listLesson.contains(it) }.forEach {
                val num = it + 1
                rv.setTextViewText(getCourseId(num), "")
                rv.setTextViewText(getRoomId(num), "")
                rv.setOnClickPendingIntent(getLayoutId(num),
                    getClickPendingIntent(context, getLayoutId(num), "", javaClass))
            }

            //设置前后按钮操作
            addClickPendingIntent(rv, context)

            show(rv, context)
            context.defaultSharedPreferences.editor {
                putString(courseData, gson.toJson(list))
            }
        }
    }

    private fun setMoreView(
        num: Int,
        rv: RemoteViews,
        course: CourseStatus.Course,
        context: Context
    ) {
        val moreViewNum = (num + 1) / 2
        hideNormalLayout(moreViewNum, rv)
        rv.setViewVisibility(getMoreContentId(moreViewNum), View.VISIBLE)
        rv.setTextViewText(getMoreCourseId(moreViewNum), course.course)
        rv.setTextViewText(getMoreRoomId(moreViewNum), filterClassRoom(course.classroom!!))
        rv.setOnClickPendingIntent(getMoreContentId(moreViewNum),
            getClickPendingIntent(context, getLayoutId(num), "btn.start.com", javaClass))
    }

    private fun initView(rv: RemoteViews, context: Context) {
        for (i in 1..6) {
            rv.setTextViewText(getCourseId(i), "")
            rv.setTextViewText(getRoomId(i), "")
            rv.setOnClickPendingIntent(getLayoutId(i),
                getClickPendingIntent(context, getLayoutId(i), "", javaClass))
            rv.setViewVisibility(getCourseId(i), View.VISIBLE)
            rv.setViewVisibility(getRoomId(i), View.VISIBLE)
            rv.setViewVisibility(getLayoutId(i), View.VISIBLE)
            val b = i / 2 != 0
            if (b) {
                rv.setViewVisibility(getMoreContentId(i), View.GONE)
                rv.setViewVisibility(getMoreViewId(i), View.VISIBLE)
            }
        }
    }

    private fun hideNormalLayout(num: Int, rv: RemoteViews) {
        when (num) {
            1 -> {
                rv.setViewVisibility(R.id.widget_normal_layout1, View.GONE)
                rv.setViewVisibility(R.id.widget_normal_layout2, View.GONE)
            }
            2 -> {
                rv.setViewVisibility(R.id.widget_normal_layout3, View.GONE)
                rv.setViewVisibility(R.id.widget_normal_layout4, View.GONE)
            }
            3 -> {
                rv.setViewVisibility(R.id.widget_normal_layout5, View.GONE)
                rv.setViewVisibility(R.id.widget_normal_layout6, View.GONE)
            }
        }
    }

    private fun getWeekDayChineseName(weekDay: Int): String {
        return when (weekDay) {
            1 -> "七"
            2 -> "一"
            3 -> "二"
            4 -> "三"
            5 -> "四"
            6 -> "五"
            7 -> "六"
            else -> "null"
        }
    }

    private fun addClickPendingIntent(rv: RemoteViews, context: Context) {
        rv.setOnClickPendingIntent(R.id.widget_normal_title,
            getClickPendingIntent(context, R.id.widget_normal_title, "btn.text.com", javaClass))
        rv.setOnClickPendingIntent(R.id.widget_normal_front,
            getClickPendingIntent(context, R.id.widget_normal_front, "btn.text.com", javaClass))
        rv.setOnClickPendingIntent(R.id.widget_normal_back,
            getClickPendingIntent(context, R.id.widget_normal_back, "btn.text.com", javaClass))
    }

    private fun show(remoteViews: RemoteViews, context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, javaClass)
        manager.updateAppWidget(componentName, remoteViews)
    }

    @IdRes
    private fun getLayoutId(num: Int): Int {
        when (num) {
            1 -> {
                return R.id.widget_normal_layout1
            }
            2 -> {
                return R.id.widget_normal_layout2
            }
            3 -> {
                return R.id.widget_normal_layout3
            }
            4 -> {
                return R.id.widget_normal_layout4
            }
            5 -> {
                return R.id.widget_normal_layout5
            }
            6 -> {
                return R.id.widget_normal_layout6
            }
            else -> {
                return R.id.widget_normal_layout1
            }
        }
    }

    @IdRes
    private fun getCourseId(num: Int): Int {
        when (num) {
            1 -> {
                return R.id.widget_normal_course1
            }
            2 -> {
                return R.id.widget_normal_course2
            }
            3 -> {
                return R.id.widget_normal_course3
            }
            4 -> {
                return R.id.widget_normal_course4
            }
            5 -> {
                return R.id.widget_normal_course5
            }
            6 -> {
                return R.id.widget_normal_course6
            }
            else -> {
                return R.id.widget_normal_course1
            }
        }
    }

    @IdRes
    private fun getRoomId(num: Int): Int {
        when (num) {
            1 -> {
                return R.id.widget_normal_room1
            }
            2 -> {
                return R.id.widget_normal_room2
            }
            3 -> {
                return R.id.widget_normal_room3
            }
            4 -> {
                return R.id.widget_normal_room4
            }
            5 -> {
                return R.id.widget_normal_room5
            }
            6 -> {
                return R.id.widget_normal_room6
            }
            else -> {
                return R.id.widget_normal_room1
            }
        }
    }


    fun getMoreContentId(num: Int) = when (num) {
        1 -> R.id.widget_normal_content_more_class1
        2 -> R.id.widget_normal_content_more_class2
        3 -> R.id.widget_normal_content_more_class3
        else -> R.id.widget_normal_content_more_class1
    }

    fun getMoreRoomId(num: Int) = when (num) {
        1 -> R.id.widget_normal_room_more_class1
        2 -> R.id.widget_normal_room_more_class2
        3 -> R.id.widget_normal_room_more_class3
        else -> R.id.widget_normal_room_more_class1
    }

    fun getMoreCourseId(num: Int) = when (num) {
        1 -> R.id.widget_normal_course_more_class1
        2 -> R.id.widget_normal_course_more_class2
        3 -> R.id.widget_normal_course_more_class3
        else -> R.id.widget_normal_course_more_class1
    }


    fun getMoreViewId(num: Int) = when (num) {
        1 -> R.id.widget_normal_layout_view_more_class1
        2 -> R.id.widget_normal_layout_view_more_class2
        3 -> R.id.widget_normal_layout_view_more_class3
        else -> R.id.widget_normal_layout_view_more_class1
    }


}