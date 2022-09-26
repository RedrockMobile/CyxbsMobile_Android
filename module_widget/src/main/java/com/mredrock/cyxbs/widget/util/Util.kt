package com.mredrock.cyxbs.widget.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.IdRes
import com.google.gson.Gson
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.base.BaseApp
import com.mredrock.cyxbs.lib.utils.extensions.CyxbsToast
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.widget.repo.bean.LessonEntity
import com.mredrock.cyxbs.widget.repo.database.LessonDatabase
import com.mredrock.cyxbs.widget.activity.InfoActivity
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by zia on 2018/10/10.
 * 精力憔悴，这些方法直接揉在一起了*/

const val ACTION_FLUSH = "flush"
const val ACTION_CLICK = "btn.start.com"
const val POSITION = "position"
const val CLICK_LESSON = "btn.click.lesson"
const val CLICK_AFFAIR = "btn.click.affair"

fun getClickPendingIntent(
    context: Context,
    @IdRes resId: Int,
    action: String,
    clazz: Class<*>,
): PendingIntent {
    val intent = Intent()
    intent.setClass(context, clazz)
    intent.action = action
    intent.data = Uri.parse("id:$resId")

    return PendingIntent.getBroadcast(context, 0, intent, getPendingIntentFlags())
}

/**实现lesson点击跳转到显示课程详情界面*/
fun getLessonClickPendingIntent(
    context: Context,
    @IdRes resId: Int,
    action: String,
    clazz: Class<*>,
    lesson: LessonEntity
): PendingIntent {
    val intent = Intent()
    intent.setClass(context, clazz)
    intent.action = action
    intent.data = Uri.parse("id:$resId")
    intent.putExtra(CLICK_LESSON, gson.toJson(lesson))
    return PendingIntent.getBroadcast(context, 0, intent, getPendingIntentFlags())
}

val gson by lazy { Gson() }

//给按钮返回PendingIntent
fun getClickIntent(
    context: Context,
    widgetId: Int,
    viewId: Int,
    requestCode: Int,
    action: String,
    clazz: Class<*>,
): PendingIntent? {
    //pendingintent中需要的intent，绑定这个类和当前context
    val i = Intent(context, clazz)
    //设置action，方便在onReceive中区别点击事件
    i.action = action //设置更新动作
    //设置bundle
    val bundle = Bundle()
    //将widgetId放进bundle
    bundle.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
    //放进需要设置的viewId
    bundle.putInt("Button", viewId)
    i.putExtras(bundle)
    return PendingIntent.getBroadcast(context, requestCode, i, getPendingIntentFlags())
}

fun filterClassRoom(classRoom: String): String {
    return if (classRoom.length > 8) {
        classRoom.replace(Regex("[\\u4e00-\\u9fa5()（）]"), "")
    } else {
        classRoom
    }
}

fun startOperation(lesson: LessonEntity) {
    if (IAccountService::class.impl.getVerifyService().isLogin()) {
        CyxbsToast.show(BaseApp.baseApp, "请登录之后再点击查看详细信息", Toast.LENGTH_SHORT)
    } else {
/*        ARouter.getInstance().build(MAIN_MAIN).navigation()
//        Todo,此处等郭神提供课表的接口*/
    }
}

fun getLessonByCalendar(context: Context, calendar: Calendar): ArrayList<LessonEntity>? {
    val weekOfTerm = SchoolCalendar().weekOfTerm
    val myStuNum =
        defaultSp.getString(LessonDatabase.MY_STU_NUM, "")
    val lesson = LessonDatabase.INSTANCE.getLessonDao()
        .queryAllLessons(myStuNum!!, weekOfTerm)
    if (lesson.isEmpty()) return null
    /*
    * 转换表，老外从周日开始计数,orz
    * 7 1 2 3 4 5 6 老外
    * 1 2 3 4 5 6 7 Calendar.DAY_OF_WEEK
    * 6 0 1 2 3 4 5 需要的结果(hash_day)
    * */
    val hashDay = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7

    val list = ArrayList<LessonEntity>()
    lesson.forEach {
        if (it.hashDay == hashDay && it.week == weekOfTerm) {
            list.add(it)
        }
    }
    list.sortBy { it.beginLesson }
    return list
}

fun getErrorLessonList(): ArrayList<LessonEntity> {
    val data = LessonEntity(course = "数据异常，请刷新")
    val list = ArrayList<LessonEntity>()
    list.add(data)
    return list
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

/**获取登录用户的本周所有Lessont*/
fun getMyLessons(weekOfTerm: Int): List<LessonEntity> {
    val myStuNum =
        defaultSp.getString(LessonDatabase.MY_STU_NUM, "")
    return LessonDatabase.INSTANCE.getLessonDao()
        .queryAllLessons(myStuNum!!, weekOfTerm)
}

fun getOthersStuNum(weekOfTerm: Int): List<LessonEntity> {
    val othersStuNum =
        defaultSp.getString(LessonDatabase.OTHERS_STU_NUM, "")
    return LessonDatabase.INSTANCE.getLessonDao()
        .queryAllLessons(othersStuNum!!, weekOfTerm)
}

/**打开activity，展示课程详情*/
fun showLessonInfo(lesson: String) {
    val intent = Intent(appContext, InfoActivity::class.java)
    intent.putExtra(CLICK_LESSON, lesson)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
    appContext.startActivity(intent)
}

/**展示事务详情*/
fun showAffairInfo(affair: String) {
    val intent = Intent(appContext, InfoActivity::class.java)
    intent.putExtra(CLICK_AFFAIR, affair)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
    appContext.startActivity(intent)
}


