package com.mredrock.cyxbs.widget.widget.normal

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.IdRes
import com.alibaba.android.arouter.launcher.ARouter
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
    //生成calendar
    private val calendar = Calendar.getInstance()
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
//                    Toast.makeText(context, "已刷新", Toast.LENGTH_SHORT).show()
                }
            }
            if (isDoubleClick()) {
                Toast.makeText(context, "提示：点击星期返回今日", Toast.LENGTH_SHORT).show()
            }
        }
        if (intent.action == "btn.start.com") {
            fresh(context,0)//在应用没有打开的时候点击跳转需要刷新一下数据
            val newList = mutableListOf<WidgetCourse.DataBean>()
            list.forEach {it->
                newList.add(changeCourseToWidgetCourse(it))
            }
            val size = newList.size
            when (rId) {
                R.id.widget_normal_layout1 -> {
                    if(size < 1||newList[0].hash_lesson==EmptyCourseObject) return
                    startOperation(newList[0])
                }
                R.id.widget_normal_layout2 -> {
                    if(size < 2) return
                    startOperation(newList[1])
                }
                R.id.widget_normal_layout3 -> {
                    if(size < 3) return
                    startOperation(newList[2])
                }
                R.id.widget_normal_layout4 -> {
                    if(size < 4) return
                    startOperation(newList[3])
                }
                R.id.widget_normal_layout5 -> {
                    if(size < 5) return
                    startOperation(newList[4])
                }
                R.id.widget_normal_layout6 -> {
                    if(size < 6) return
                    startOperation(newList[5])
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

        if (anotherTime < 600) {
            return true
        }
        lastClickTime = time
        return false
    }

    /**
     * 刷新，传入offsetTime作为今天的偏移量
     */
    fun fresh(context: Context, offsetTime: Int) {
        try{//catch异常，避免课表挂了之后这边跟着挂
            val nowHour = calendar.get(Calendar.HOUR_OF_DAY)
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + offsetTime)

            //获取数据
            list = getCourseByCalendar(context, calendar)
                    ?: getErrorCourseList()

            if (list.isEmpty()) {
                list.add(getNoCourse())
            }

            //如果课已经上完了，而且过了晚上7点，显示明天的课程
            if (nowHour > 19) {
                var i = 0
                list.forEach {
                    val hour = getStartCalendarByNum(it.hash_lesson).get(Calendar.HOUR_OF_DAY)
                    if (nowHour > hour) {
                        i++
                    } else {
                        return@forEach
                    }
                }
                if (i == list.size) {
                    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1)
                    val tomorrowList = getCourseByCalendar(context, calendar)
                            ?: getErrorCourseList()
                    list.clear()
                    list.addAll(tomorrowList)
                }
            }

            val rv = RemoteViews(context.packageName, R.layout.widget_normal)

            //显示星期几
            rv.setTextViewText(R.id.widget_normal_title, getWeekDayChineseName(calendar.get(Calendar.DAY_OF_WEEK)))

            //显示课程
            var index = 1
            list.forEach { course ->
                rv.setViewVisibility(getLayoutId(index), View.VISIBLE)
                rv.setTextViewText(getTimeId(index),if (course.hash_lesson!=-1) formatTime(getStartCalendarByNum(course.hash_lesson)) else "")
                rv.setTextViewText(getCourseId(index), course.course)
                rv.setTextViewText(getRoomId(index), filterClassRoom(course.classroom!!))
                rv.setOnClickPendingIntent(getLayoutId(index),
                        getClickPendingIntent(context, getLayoutId(index), "btn.start.com", javaClass))
                index++
            }
            //隐藏后面的item
            for (i in index..6) {
                rv.setViewVisibility(getLayoutId(i), View.GONE)
            }

            //设置前后按钮操作
            addClickPendingIntent(rv, context)

            show(rv, context)
        }catch (e:Exception){
            e.printStackTrace()
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
    private fun getTimeId(num: Int): Int {
        when (num) {
            1 -> {
                return R.id.widget_normal_time1
            }
            2 -> {
                return R.id.widget_normal_time2
            }
            3 -> {
                return R.id.widget_normal_time3
            }
            4 -> {
                return R.id.widget_normal_time4
            }
            5 -> {
                return R.id.widget_normal_time5
            }
            6 -> {
                return R.id.widget_normal_time6
            }
            else -> {
                return R.id.widget_normal_time1
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