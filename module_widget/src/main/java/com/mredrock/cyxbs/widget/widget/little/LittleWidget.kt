package com.mredrock.cyxbs.widget.widget.little

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.api.main.MAIN_MAIN
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.util.*
import com.mredrock.cyxbs.widget.widget.little.bean.LittleWidgetState
import com.mredrock.cyxbs.widget.widget.little.bean.emptyLittleWidgetState
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by zia on 2018/10/10.
 * rebuild by ZhiQiang Tu 2022/4/21
 * 小型部件，不透明
 */



//必要数据
private var todayClasses = listOf<LittleWidgetState>()
private var index: Int by Delegates.observable(-1) { _, _, new ->
    currentClass = if (new == -1) {  emptyLittleWidgetState() } else { todayClasses[new] }
}
private var currentClass: LittleWidgetState = emptyLittleWidgetState()

//小组件会被多次初始化,也就是每次刷新LittleWidget对象都是重新new的，这些值是不会变动的,所以申明为顶层也不算太离谱。
//id
private val layoutId = R.layout.widget_little
private val upId = R.id.widget_little_up
private val downId = R.id.widget_little_down
private val titleId = R.id.widget_little_title
private val courseNameId = R.id.widget_little_courseName
private val roomId = R.id.widget_little_room
private val refreshId = R.id.widget_little_refresh

//action
private const val packageName = "com.mredrock.cyxbs.widget.widget.little.LittleWidget"
//供给用户的刷新事件
private const val actionRefresh = "${packageName}.refresh"
//供给用户的up事件
private const val actionUp = "${packageName}.up"
//供给用户的down事件
private const val actionDown = "${packageName}.down"
//供给用户的start事件
private const val actionStart = "${packageName}.start"
//提供给外部对该组件进行刷新的事件
private const val actionInit = "${packageName}.init"


class LittleWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        initData(context)
        refresh(context)
    }

    /**
     * 刷新小组件
     */
    private fun refresh(context: Context) {
        refreshRemoteView(context,null,null,initRemoteView(context))
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
        rv.setOnClickPendingIntent(
            upId,
            getClickPendingIntent(context, upId, actionUp, javaClass)
        )
        rv.setOnClickPendingIntent(
            downId,
            getClickPendingIntent(context, downId, actionDown, javaClass)
        )
        rv.setOnClickPendingIntent(
            refreshId,
            getClickPendingIntent(context, refreshId, actionRefresh, javaClass)
        )


        rv.setTextViewText(titleId, currentClass.title)
        rv.setTextViewText(courseNameId, currentClass.courseName)
        rv.setTextViewText(roomId, currentClass.classRoomNum)

        if (index != -1) {
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

        todayClasses = list.map {
            val title = formatTime(getStartCalendarByNum(it.hash_lesson))
            val courseName = it.course!!
            val roomId = filterClassRoom(it.classroom!!)
            LittleWidgetState(title = title, courseName = courseName, classRoomNum = roomId)
        }

        //TODO 逻辑好像有点点问题
        //寻找当下需要展示的课程的index，并依据index去实例化一个LittleWidgetState
        index = list.indexOfFirst {
            Calendar.getInstance() < getStartCalendarByNum(it.hash_lesson)
        }

    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            actionRefresh -> {
                onUpdate(context, null, null)
                Toast.makeText(context, "已刷新", Toast.LENGTH_SHORT).show()
            }
            actionUp -> {
                if (index == -1) {
                    Toast.makeText(context, "今天没课~", Toast.LENGTH_SHORT).show()
                    return
                }
                if (index - 1 < 0) {
                    Toast.makeText(context, "已经是第一节课了~", Toast.LENGTH_SHORT).show()
                    return
                }
                index--
                refresh(context)
            }
            actionDown -> {
                if (index == -1) {
                    Toast.makeText(context, "今天没课~", Toast.LENGTH_SHORT).show()
                    return
                }
                if (index + 1 > todayClasses.size - 1) {
                    Toast.makeText(context, "已近是最后一节课了~", Toast.LENGTH_SHORT).show()
                    return
                }
                index++
                refresh(context)
            }
            actionStart -> {
                ARouter.getInstance().build(MAIN_MAIN).navigation()
            }
            actionInit ->{
                onUpdate(context,null,null)
                Toast.makeText(context, "刷新", Toast.LENGTH_SHORT).show()
            }
        }
    }
}