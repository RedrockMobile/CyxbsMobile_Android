package com.mredrock.cyxbs.widget.widget.little

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.util.*
import com.mredrock.cyxbs.widget.widget.little.bean.LittleWidgetState
import com.mredrock.cyxbs.widget.widget.little.bean.emptyLittleWidgetState
import com.mredrock.cyxbs.widget.widget.page.trans.TransConfig
import java.util.*

/**
 * Created by zia on 2018/10/10.
 * 小型部件，不透明
 */

private val layoutId = R.layout.widget_little_trans
private val titleId = R.id.widget_little_title_trans
private val courseNameId = R.id.widget_little_trans_courseName
private val roomId = R.id.widget_little_trans_room
private val refreshId = R.id.widget_little_title_trans

private var hasClass = false
private var currentClass: LittleWidgetState = emptyLittleWidgetState()


//供给用户的刷新事件
private const val actionRefresh = "refresh"

//供给用户的start事件
private const val actionStart = "start"

//提供给外部对该组件进行刷新的事件
private const val actionFlush = "flush"

class LittleTransWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        initData(context)
        refresh(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            actionStart -> {

            }
            actionFlush -> {
                onUpdate(context, null, null)
            }
        }
    }


    /**
     * 刷新小组件
     */
    private fun refresh(context: Context) {
        refreshRemoteView(context, null, null, initRemoteView(context))
    }

    /**
     * 返回新的RemoteView
     */
    private fun refreshRemoteView(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?,
        rv: RemoteViews
    ) {
        val manager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, javaClass)
        manager.updateAppWidget(componentName, rv)
    }

    /**
     * 重新new一个新的remoteView供给刷新界面
     */
    private fun initRemoteView(context: Context): RemoteViews {
        val rv = RemoteViews(context.packageName, layoutId)


        rv.setTextViewText(titleId, currentClass.title)
        rv.setTextViewText(courseNameId, currentClass.courseName)
        rv.setTextViewText(roomId, currentClass.classRoomNum)

        if (hasClass) {
            //有课的时候才能跳转
            rv.setOnClickPendingIntent(
                courseNameId,
                getClickPendingIntent(context, courseNameId, actionStart, javaClass)
            )
            rv.setOnClickPendingIntent(
                roomId,
                getClickPendingIntent(context, roomId, actionStart, javaClass)
            )
        }

        //设置用户自定义定义配置
        try {//这个tryCatch防止用户输入的颜色有误,parseColor报错
            //标题（时间）
            val config = TransConfig.getUserConfig(context)
            rv.setTextColor(titleId, Color.parseColor(config.timeTextColor))
            rv.setTextViewTextSize(
                titleId,
                TypedValue.COMPLEX_UNIT_SP,
                config.timeTextSize.toFloat()
            )

            //课程名
            rv.setTextColor(courseNameId, Color.parseColor(config.courseTextColor))
            rv.setTextViewTextSize(
                courseNameId,
                TypedValue.COMPLEX_UNIT_SP,
                config.courseTextSize.toFloat()
            )

            //教室
            rv.setTextColor(roomId, Color.parseColor(config.roomTextColor))
            rv.setTextViewTextSize(
                roomId,
                TypedValue.COMPLEX_UNIT_SP,
                config.roomTextSize.toFloat()
            )

            rv.setViewVisibility(R.id.widget_little_trans_holder, View.VISIBLE)

            //装饰，用ARGB_8888是为了设置透明度
            val holderBm = Bitmap.createBitmap(300, 1, Bitmap.Config.ARGB_8888)
            holderBm.eraseColor(Color.parseColor(config.holderColor))
            rv.setImageViewBitmap(R.id.widget_little_trans_holder, holderBm)

            //这个方法不能运行，上面的代码性能不行，来个人优化下？
//        rv.setInt(R.id.widget_little_trans_holder,"setBackgroundColor",Color.parseColor(config.holderColor))

            if (config.holderColor.length == 9) {
                if (config.holderColor.subSequence(1, 3) == "00") {
                    rv.setViewVisibility(R.id.widget_little_trans_holder, View.GONE)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        return rv

    }

    /**
     * 初始化必要的数据
     * 1.todayClasses -> 当天的所有课程
     * 2.index        -> 当前应上课程在todayClasses的index
     * 3.currentClass -> 当前应上的课程
     */
    private fun initData(context: Context) {

        val list = getTodayCourse(context)
            ?: getErrorCourseList()

        //TODO 逻辑好像有点点问题
        currentClass = list.firstOrNull {
            Calendar.getInstance() < getStartCalendarByNum(it.hash_lesson)
        }?.let {
            val title = formatTime(getStartCalendarByNum(it.hash_lesson))
            val courseName = it.course!!
            val roomId = filterClassRoom(it.classroom!!)
            LittleWidgetState(title = title, courseName = courseName, classRoomNum = roomId)
        } ?: emptyLittleWidgetState()


    }


    /*override fun getRemoteViews(context: Context, courseStatus: CourseStatus.Course?, timeTv: String): RemoteViews {
        val rv = RemoteViews(context.packageName, getLayoutResId())

        if (courseStatus == null) {
            rv.setTextViewText(getTitleResId(), getWeekDayChineseName(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)))
            rv.setTextViewText(getCourseNameResId(), "今天没有课~")
            rv.setTextViewText(getRoomResId(), "")
        } else {
            rv.setTextViewText(getTitleResId(), timeTv)
            rv.setTextViewText(getCourseNameResId(), courseStatus.course)
            rv.setTextViewText(getRoomResId(), filterClassRoom(courseStatus.classroom!!))
            //course和room设置点击事件到Activity，title设置刷新，没课才设置点击事件
            rv.setOnClickPendingIntent(getCourseNameResId(),
                    getClickPendingIntent(context,getCourseNameResId(),"``````````````````````````````btn.start.com``````````````````````````````",javaClass))
            rv.setOnClickPendingIntent(getRoomResId(),
                    getClickPendingIntent(context,getRoomResId(),"btn.start.com",javaClass))
            rv.setOnClickPendingIntent(getTitleResId(),
                    getClickPendingIntent(context,getTitleResId(),"btn.text.com",javaClass))
        }


        //设置用户自定义定义配置

        try {//这个tryCatch防止用户输入的颜色有误,parseColor报错
            //标题（时间）
            val config = TransConfig.getUserConfig(context)
            rv.setTextColor(getTitleResId(), Color.parseColor(config.timeTextColor))
            rv.setTextViewTextSize(getTitleResId(), TypedValue.COMPLEX_UNIT_SP, config.timeTextSize.toFloat())

            //课程名
            rv.setTextColor(getCourseNameResId(), Color.parseColor(config.courseTextColor))
            rv.setTextViewTextSize(getCourseNameResId(), TypedValue.COMPLEX_UNIT_SP, config.courseTextSize.toFloat())

            //教室
            rv.setTextColor(getRoomResId(), Color.parseColor(config.roomTextColor))
            rv.setTextViewTextSize(getRoomResId(), TypedValue.COMPLEX_UNIT_SP, config.roomTextSize.toFloat())

            rv.setViewVisibility(R.id.widget_little_trans_holder, View.VISIBLE)

            //装饰，用ARGB_8888是为了设置透明度
            val holderBm = Bitmap.createBitmap(300, 1, Bitmap.Config.ARGB_8888)
            holderBm.eraseColor(Color.parseColor(config.holderColor))
            rv.setImageViewBitmap(R.id.widget_little_trans_holder, holderBm)

            //这个方法不能运行，上面的代码性能不行，来个人优化下？
//        rv.setInt(R.id.widget_little_trans_holder,"setBackgroundColor",Color.parseColor(config.holderColor))

            if (config.holderColor.length == 9) {
                if (config.holderColor.subSequence(1, 3) == "00") {
                    rv.setViewVisibility(R.id.widget_little_trans_holder, View.GONE)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return rv
    }*/
}