package com.cyxbsmobile_single.module_todo.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import com.cyxbsmobile_single.module_todo.model.bean.DateBeen
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.TODO_WEEK_MONTH_ARRAY
import com.mredrock.cyxbs.common.config.TODO_YEAR_OF_WEEK_MONTH_ARRAY
import com.mredrock.cyxbs.common.utils.LogUtils
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

val weekStringList = listOf(
    "一",
    "二",
    "三",
    "四",
    "五",
    "六",
    "日"
)

//根据remindMode生成提醒日期
//如果仅仅加以重复而不提醒，则返回提醒那天的
fun repeatMode2RemindTime(remindMode: RemindMode): String {
    if (remindMode.notifyDateTime == "") return ""
    //今天的日历
    val calendar = Calendar.getInstance()
    calendar.time = Date(System.currentTimeMillis())
    //提醒那一天的日历
    val remindDateCalender = Calendar.getInstance()
    val format = SimpleDateFormat("yy年MM月dd日hh:mm", Locale.CHINA)
    val remindDate = format.parse(remindMode.notifyDateTime)
    remindDateCalender.time = remindDate
    val remindTime =
        "${remindDateCalender.get(Calendar.HOUR_OF_DAY)}:${remindDateCalender.get(Calendar.MINUTE)}"
    when (remindMode.repeatMode) {
        RemindMode.DAY -> {
            return "${calendar.get(Calendar.MONTH) + 1}月${calendar.get(Calendar.DAY_OF_MONTH)}日 $remindTime"
        }

        RemindMode.MONTH -> {
            remindMode.day.sort()
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
                    return "${calendar.get(Calendar.MONTH) + 1}月${day}日 $remindTime"
                }
            }
        }

        RemindMode.WEEK -> {
            remindMode.week.sort()
            for (weekDay in remindMode.week) {
                if (calendar.get(Calendar.DAY_OF_WEEK) <= weekDay) {
                    //判定为今天是早于下一次提醒的时间的，所以说可以进行提醒
                    val dif = weekDay - calendar.get(Calendar.DAY_OF_WEEK)
                    calendar.add(Calendar.DAY_OF_WEEK, dif)
                    return "${calendar.get(Calendar.MONTH) + 1}月${calendar.get(Calendar.DAY_OF_MONTH)}日 $remindTime"
                }
            }
        }

        RemindMode.YEAR -> {
            for (day in remindMode.date) {
                val monthAndDay = day.split('.').map {
                    Integer.parseInt(it)
                }
                val notifyCalender = Calendar.getInstance()
                notifyCalender.set(Calendar.MONTH, monthAndDay[0])
                notifyCalender.set(Calendar.DAY_OF_MONTH, monthAndDay[1])
                if (calendar.timeInMillis < notifyCalender.timeInMillis) {
                    //判定为当前日期早于第一个提醒日期
                    return "${monthAndDay[0]}月${monthAndDay[1]}日 $remindTime"
                }
            }
        }

        RemindMode.NONE -> {
            return "${remindDateCalender.get(Calendar.MONTH) + 1}月${remindDateCalender.get(Calendar.DAY_OF_MONTH)}日 $remindTime"
        }
    }
    return "提醒日期错误"
}

fun getNextNotifyDay(remindMode: RemindMode): DateBeen {
    val repeatString = repeatMode2RemindTime(remindMode)
    //表示永远不会提醒
    if (repeatString == "提醒日期错误") return DateBeen(-1, -1, -1)

    val date = SimpleDateFormat("MM月dd日 hh:mm", Locale.CHINA).parse(repeatString)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return DateBeen(
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.DAY_OF_WEEK)
    )
}

fun getThisYearDateSting(): ArrayList<DateBeen> {
    val dateArrayJson =
        BaseApp.context.defaultSharedPreferences.getString(TODO_WEEK_MONTH_ARRAY, "")
    val thisYear = Calendar.getInstance().apply {
        time = Date(System.currentTimeMillis())
    }.get(Calendar.YEAR)
    val listYear = BaseApp.context.defaultSharedPreferences.getInt(TODO_YEAR_OF_WEEK_MONTH_ARRAY, 0)
    if (dateArrayJson == "" || dateArrayJson == null || thisYear == listYear) {
        //认定本地还没有对于日期，星期数的缓存，则生成一份
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, 0)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val dateBeanArray = ArrayList<DateBeen>()
        for (day in 0 until calendar.getActualMaximum(Calendar.DAY_OF_YEAR)) {
            dateBeanArray.add(
                DateBeen(
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.DAY_OF_WEEK)
                )
            )
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        val arrayJson = Gson().toJson(dateBeanArray)
        BaseApp.context.defaultSharedPreferences
            .editor {
                putString(TODO_WEEK_MONTH_ARRAY, arrayJson)
            }
        val todayCalendar = Calendar.getInstance()
        dateBeanArray.subList(0, todayCalendar.get(Calendar.DAY_OF_YEAR) - 1).clear()
        return dateBeanArray
    } else {
        //使用本地缓存
        val list = Gson().fromJson(
            dateArrayJson,
            object : TypeToken<ArrayList<DateBeen>>() {}.type
        ) as ArrayList<DateBeen>
        val todayCalendar = Calendar.getInstance()
        LogUtils.d("RayleighZ", "${todayCalendar.get(Calendar.DAY_OF_YEAR)}")
        list.subList(0, todayCalendar.get(Calendar.DAY_OF_YEAR) - 1).clear()
        return list
    }
}

fun needTodayDone(remindMode: RemindMode): Boolean {
    val calendar = Calendar.getInstance()
    calendar.time = Date(System.currentTimeMillis())

    val nextDay = getNextNotifyDay(remindMode)

    return nextDay.day == calendar.get(Calendar.DAY_OF_MONTH) && nextDay.month == calendar.get(
        Calendar.MONTH
    )
}

fun setMargin(
    view: View,
    top: Int = view.marginTop,
    left: Int = view.marginLeft,
    right: Int = view.marginRight,
    bottom: Int = view.marginBottom
) {
    val params = view.layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(left, top, right, bottom)
    view.layoutParams = params
    view.requestLayout()
}

fun remindMode2RemindList(remindMode: RemindMode): List<String> {
    when (remindMode.repeatMode) {
        RemindMode.NONE -> {
            return listOf("设置提醒时间")
        }

        RemindMode.DAY -> {
            return listOf("每天")
        }

        RemindMode.WEEK -> {
            return remindMode.week.map {
                "每周$it"
            }
        }

        RemindMode.YEAR -> {
            return remindMode.date.map {
                val monthAndDay = it.split('.')
                "每年${monthAndDay[0]}月${monthAndDay[1]}日"
            }
        }

        else -> {
            return listOf("设置提醒时间")
        }
    }
}