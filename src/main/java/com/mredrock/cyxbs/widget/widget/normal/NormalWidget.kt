package com.mredrock.cyxbs.widget.widget.normal

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.IdRes
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.bean.WidgetCourse
import com.mredrock.cyxbs.common.config.MAIN_MAIN
import com.mredrock.cyxbs.common.event.WidgetCourseEvent
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.bean.CourseStatus
import com.mredrock.cyxbs.widget.util.*
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by zia on 2018/10/10.
 * 大课表
 * 简单原理：因为一天最多6节课，直接写了6个布局，隐藏后面没有用到的布局就完了
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

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        context.defaultSharedPreferences.editor {
            putInt(shareName, 0)
        }
        fresh(context, 0)
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
                        putInt(shareName, offsetTime - 1)
                    }
                    fresh(context, offsetTime - 1)
                }
                R.id.widget_normal_front -> {
                    context.defaultSharedPreferences.editor {
                        putInt(shareName, offsetTime + 1)
                    }
                    fresh(context, offsetTime + 1)
                }
                R.id.widget_normal_title -> {
                    context.defaultSharedPreferences.editor {
                        putInt(shareName, 0)
                    }
                    fresh(context, 0)
                }
            }
            if (isDoubleClick()) {
                Toast.makeText(context, "提示：点击星期返回今日", Toast.LENGTH_SHORT).show()
            }
        }
        if (intent.action == "btn.start.com") {
            list = gson.fromJson(context.defaultSharedPreferences.getString(courseData, ""), object : TypeToken<ArrayList<CourseStatus.Course>>() {}.type)
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

    private fun startOperation(dataBean: WidgetCourse.DataBean) {
        ARouter.getInstance().build(MAIN_MAIN).navigation()
        EventBus.getDefault().postSticky(WidgetCourseEvent(mutableListOf(dataBean)))
    }


    private fun isDoubleClick(): Boolean {
        val time = System.currentTimeMillis()
        val anotherTime = time - lastClickTime

        if (anotherTime < 200) {
            return true
        }
        lastClickTime = time
        return false
    }


    /**
     * 刷新，传入offsetTime作为今天的偏移量
     */
    private fun fresh(context: Context, offsetTime: Int) {
        try {//catch异常，避免课表挂了之后这边跟着挂
            val nowHour = calendar.get(Calendar.HOUR_OF_DAY)
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + offsetTime)

            //获取数据
            list = getCourseByCalendar(context, calendar)
                    ?: getErrorCourseList()

            val rv = RemoteViews(context.packageName, R.layout.widget_normal)

            //显示星期几
            val text = if (Calendar.getInstance()[Calendar.DAY_OF_WEEK] == calendar[Calendar.DAY_OF_WEEK]) "今" else getWeekDayChineseName(calendar.get(Calendar.DAY_OF_WEEK))
            rv.setTextViewText(R.id.widget_normal_title, text)

            //显示课程
            list.forEach { course ->
                val num = course.hash_lesson + 1
                rv.setTextViewText(getCourseId(num), course.course)
                rv.setTextViewText(getRoomId(num), filterClassRoom(course.classroom!!))
                rv.setOnClickPendingIntent(getLayoutId(num),
                        getClickPendingIntent(context, getLayoutId(num), "btn.start.com", javaClass))
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
        } catch (e: Exception) {
            e.printStackTrace()
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
}