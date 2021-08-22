package com.cyxbsmobile_single.module_todo.util

import com.cyxbsmobile_single.module_todo.model.bean.DateBeen
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.TODO_WEEK_MONTH_ARRAY
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * @date 2021-08-14
 * @author Sca RayleighZ
 * TODO模块一些简单的通用工具类
 */

//根据repeatMode生成提醒日期
fun repeatMode2RemindTime(remindMode: RemindMode): String {
    val calendar = Calendar.getInstance()
    calendar.time = Date(System.currentTimeMillis())
    when (remindMode.repeatMode) {
        RemindMode.DAY -> {
            return "${calendar.get(Calendar.MONTH)}月${calendar.get(Calendar.DAY_OF_MONTH)}日 ${remindMode.time}"
        }

        RemindMode.MONTH -> {
            remindMode.day = remindMode.day.sorted()
            if (remindMode.day.last() > 31) {
                BaseApp.context.toast("提醒日期错误")
                return "提醒日期错误"
            }
            while (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) < remindMode.day.last()) {
                //判定为这个月的日期数不足，需要切换到下个月
                calendar.add(Calendar.MONTH, 1)
            }
            for (day in remindMode.day) {
                if (calendar.get(Calendar.DAY_OF_MONTH) <= day) {
                    //如果今日日期要早于提醒时间，就展示提为day
                    return "${calendar.get(Calendar.MONTH)}月${day}日 ${remindMode.time}"
                }
            }
        }

        RemindMode.WEEK -> {
            remindMode.week = remindMode.week.sorted()
            for (weekDay in remindMode.week) {
                if (calendar.get(Calendar.DAY_OF_WEEK) <= weekDay) {
                    //判定为今天是早于下一次提醒的时间的，所以说可以进行提醒
                    val dif = weekDay - calendar.get(Calendar.DAY_OF_WEEK)
                    calendar.add(Calendar.DAY_OF_WEEK, dif)
                    return "${calendar.get(Calendar.MONTH)}月${calendar.get(Calendar.DAY_OF_MONTH)}日 ${remindMode.time}"
                }
            }
        }

        RemindMode.YEAR -> {
            for (day in remindMode.date){
                val date = SimpleDateFormat("MM:dd", Locale.CHINA).parse(day)
                if (calendar.timeInMillis < date.time){
                    //判定为当前日期早于第一个提醒日期
                    calendar.time = date
                    return "${calendar.get(Calendar.MONTH)}月${calendar.get(Calendar.DAY_OF_MONTH)}日 ${remindMode.time}"
                }
            }
        }

        RemindMode.NONE -> {
            //不重复提醒
            val date = SimpleDateFormat("MM:dd", Locale.CHINA).parse(remindMode.date[0])
            calendar.time = date
            return "${calendar.get(Calendar.MONTH)}月${calendar.get(Calendar.DAY_OF_MONTH)}日 ${remindMode.time}"
        }
    }
    return "提醒日期错误"
}

fun getNextNotifyDay(remindMode: RemindMode): DateBeen{
    val repeatString = repeatMode2RemindTime(remindMode)
    //表示永远不会提醒
    if (repeatString == "提醒日期错误") return DateBeen(-1,-1,-1)

    val date = SimpleDateFormat("MM月dd日 hh:mm", Locale.CHINA).parse(repeatString)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return DateBeen(
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.DAY_OF_WEEK)
    )
}

fun getThisYearDateSting(): ArrayList<DateBeen> {
    val dateArrayJson =
        BaseApp.context.defaultSharedPreferences.getString(TODO_WEEK_MONTH_ARRAY, "")
    if (dateArrayJson == "" || dateArrayJson == null) {
        //认定本地还没有对于日期，星期数的缓存，则生成一份
        val timeStamp = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.time = Date(timeStamp)
        val startYear = calendar.get(Calendar.YEAR)
        val dateBeanArray = ArrayList<DateBeen>()
        dateBeanArray.add(
            DateBeen(
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.DAY_OF_WEEK)
            )
        )
        calendar.add(Calendar.DAY_OF_YEAR, 1)//向后推移一天，看明天是不是明年
        var curYear = calendar.get(Calendar.YEAR)
        while (curYear == startYear) {//直到目前还是在今年
            //新增加一个date
            dateBeanArray.add(
                DateBeen(
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.DAY_OF_WEEK)
                )
            )
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            curYear = calendar.get(Calendar.YEAR)
        }
        val arrayJson = Gson().toJson(dateBeanArray)
        BaseApp.context.defaultSharedPreferences
            .editor {
                putString(TODO_WEEK_MONTH_ARRAY, arrayJson)
            }
        return dateBeanArray
    } else {
        //使用本地缓存
        return Gson().fromJson(dateArrayJson, object : TypeToken<ArrayList<DateBeen>>() {}.type)
    }
}

fun needTodayDone(remindMode: RemindMode): Boolean{
    val calendar = Calendar.getInstance()
    calendar.time = Date(System.currentTimeMillis())

    val nextDay = getNextNotifyDay(remindMode)

    return nextDay.day == calendar.get(Calendar.DAY_OF_MONTH) && nextDay.month == calendar.get(Calendar.MONTH)
}

val weekStringList = listOf(
    "日",
    "一",
    "二",
    "三",
    "四",
    "五",
    "六"
)