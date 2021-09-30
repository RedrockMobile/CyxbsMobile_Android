package com.cyxbsmobile_single.module_todo.util

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import com.cyxbsmobile_single.module_todo.model.bean.DateBeen
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.TODO_WEEK_MONTH_ARRAY
import com.mredrock.cyxbs.common.config.TODO_YEAR_OF_WEEK_MONTH_ARRAY
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.toast
import java.text.DateFormat
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

fun repeatString2Num(repeatString: String): Int = when (repeatString) {
    "每天" -> RemindMode.DAY
    "每年" -> RemindMode.YEAR
    "每周" -> RemindMode.WEEK
    "每月" -> RemindMode.MONTH
    else -> RemindMode.NONE
}

/**
 * 根据remindMode生成提醒日期
 * 仅重复不提醒       -> 返回下一次提醒当天的凌晨00:00
 * 重复+提醒 | 仅提醒  -> 返回下一次提醒的时间
 * 不重复不提醒       -> 返回""
 */
fun repeatMode2RemindTime(remindMode: RemindMode): String {
    if (remindMode.notifyDateTime.isNullOrEmpty() && remindMode.repeatMode == RemindMode.NONE) {
        LogUtils.d("RayleighZ", "handle todo")
        return ""
    }

    //今天的日历
    val calendar = Calendar.getInstance()
    val remindTime = if (remindMode.notifyDateTime.isNullOrEmpty()) {
        //只要没设置提醒时间，就返回""，ui不展示
        "00:00"
    } else {
        //提醒那一天的日历
        val remindDateCalender = getNotifyDayCandler(remindMode)
        "${numToString(remindDateCalender.get(Calendar.HOUR_OF_DAY))}:${
            numToString(
                remindDateCalender.get(Calendar.MINUTE)
            )
        }"
    }

//    fun getTrueRemindDay(calendar: Calendar): String {
//        val notifyCandler = getNotifyDayCandler(remindMode)
//        return if (calendar.timeInMillis < notifyCandler.timeInMillis) {
//            "${calendar.get(Calendar.MONTH) + 1}月${calendar.get(Calendar.DAY_OF_MONTH)}日 $remindTime"
//        } else {
//            "${calendar.get(Calendar.MONTH) + 1}月${calendar.get(Calendar.DAY_OF_MONTH)}日 $remindTime"
//        }
//    }

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
            repeat(12) {
                while (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) < remindMode.day.last()) {
                    //判定为这个月的日期数不足，需要切换到下个月
                    //防止出现诸如2月31号这种情况
                    calendar.add(Calendar.MONTH, 1)
                }

                for (day in remindMode.day) {
                    if (calendar.get(Calendar.DAY_OF_MONTH) <= day) {
                        //如果今日日期要早于提醒时间，就展示提为day
                        return "${calendar.get(Calendar.MONTH) + 1}月${day}日 $remindTime"
                    }
                }

                //走到这里说明这个月的时间都超了，需要到下个月
                //这里就remake到下个月月初
                calendar.add(Calendar.MONTH, 1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
            }
        }

        RemindMode.WEEK -> {
            remindMode.week.sort()
            for (weekDay in remindMode.week) {
                if (calendar.get(Calendar.DAY_OF_WEEK) <= weekDay) {
                    //判定为今天是早于下一次提醒的时间的，所以说可以进行提醒
                    val dif = weekDay - calendar.get(Calendar.DAY_OF_WEEK)
                    calendar.add(Calendar.DAY_OF_WEEK, dif + 1)
                    return "${calendar.get(Calendar.MONTH) + 1}月${calendar.get(Calendar.DAY_OF_MONTH)}日 $remindTime"
                }
            }

            //到这里说明这周已经没得了，需要切换到下一周
            val dif = 7 - (calendar.get(Calendar.DAY_OF_WEEK) - remindMode.week[0])
            calendar.add(Calendar.DAY_OF_WEEK, dif + 1)
            return "${calendar.get(Calendar.MONTH) + 1}月${calendar.get(Calendar.DAY_OF_MONTH)}日 $remindTime"
        }

        RemindMode.YEAR -> {
            for ((index, day) in remindMode.date.withIndex()) {
                val monthAndDay = day.split('.').map {
                    Integer.parseInt(it)
                }
                val notifyCalender = Calendar.getInstance()
                notifyCalender.set(Calendar.MONTH, monthAndDay[0])
                notifyCalender.set(Calendar.DAY_OF_MONTH, monthAndDay[1])
                if (calendar.timeInMillis < notifyCalender.timeInMillis || index == remindMode.date.size - 1) {
                    //判定为当前日期早于第一个提醒日期
                    return "${monthAndDay[0]}月${monthAndDay[1]}日 $remindTime"
                }
            }
        }

        RemindMode.NONE -> {
            //走到这里可以保证是存在notifyDateTime的
            val remindDateCalender = Calendar.getInstance()
            //这里可以保证已经是可以解析的了
            val format = SimpleDateFormat("yy年MM月dd日hh:mm", Locale.CHINA)
            LogUtils.d("RayleighZ", "notifyTime = ${remindMode.notifyDateTime}")
            val remindDate = format.parse(remindMode.notifyDateTime)
            remindDateCalender.time = remindDate
            return "${remindDateCalender.get(Calendar.MONTH) + 1}月${remindDateCalender.get(Calendar.DAY_OF_MONTH)}日 $remindTime"
        }
    }
    return ""
}

fun getNextNotifyDay(remindMode: RemindMode): DateBeen {
    val repeatString = repeatMode2RemindTime(remindMode)
    //表示永远不会提醒
    if (repeatString == "") return DateBeen(0, 0, 0, 0, 0)

    //这里已经是经过处理的提醒时间，所以是不含年份的
    val date = SimpleDateFormat("MM月dd日 hh:mm", Locale.CHINA).parse(repeatString)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return DateBeen(
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.DAY_OF_WEEK),
        DateBeen.NORMAL,
        calendar.get(Calendar.YEAR)
    )
}

//返回未来四年的年份数据
fun getYearDateSting(): ArrayList<ArrayList<DateBeen>> {
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
        val dateBeanArrayAsFourYears = ArrayList<ArrayList<DateBeen>>()
        for (i in 0..4) {
            //未来四年
            val dateBeanArray = ArrayList<DateBeen>()
            for (day in 0 until calendar.getActualMaximum(Calendar.DAY_OF_YEAR)) {
                dateBeanArray.add(
                    DateBeen(
                        month = calendar.get(Calendar.MONTH) + 1,
                        day = calendar.get(Calendar.DAY_OF_MONTH),
                        week = getRealWeek(calendar.get(Calendar.DAY_OF_WEEK)),
                        type = DateBeen.NORMAL,
                        year = calendar.get(Calendar.YEAR)
                    )
                )
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            dateBeanArrayAsFourYears.add(dateBeanArray)
            //后置一年
            calendar.set(Calendar.MONTH, 0)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
        }
        val arrayJson = Gson().toJson(dateBeanArrayAsFourYears)
        BaseApp.context.defaultSharedPreferences
            .editor {
                putString(TODO_WEEK_MONTH_ARRAY, arrayJson)
            }
        val todayCalendar = Calendar.getInstance()
        dateBeanArrayAsFourYears[0].subList(0, todayCalendar.get(Calendar.DAY_OF_YEAR) - 1).clear()
        return dateBeanArrayAsFourYears
    } else {
        //使用本地缓存
        val list = Gson().fromJson(
            dateArrayJson,
            object : TypeToken<ArrayList<ArrayList<DateBeen>>>() {}.type
        ) as ArrayList<ArrayList<DateBeen>>
        val todayCalendar = Calendar.getInstance()
        LogUtils.d("RayleighZ", "${todayCalendar.get(Calendar.DAY_OF_YEAR)}")
        list[0].subList(0, todayCalendar.get(Calendar.DAY_OF_YEAR) - 1).clear()
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
                "每周${weekStringList[it - 1]}"
            }
        }

        RemindMode.YEAR -> {
            return remindMode.date.map {
                val monthAndDay = it.split('.')
                "每年${monthAndDay[0]}月${monthAndDay[1]}日"
            }
        }

        RemindMode.MONTH -> {
            return remindMode.day.map {
                "每月${it}日"
            }
        }

        else -> {
            return emptyList()
        }
    }
}

fun hideKeyboard(context: Context, v: View) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(v.windowToken, 0)
}

fun numToString(num: Int): String = if (num < 10) "0$num" else num.toString()

fun numToString(num: String): String = if (Integer.parseInt(num) < 10) "0$num" else num

private fun getNotifyDayCandler(remindMode: RemindMode): Calendar {
    //提醒那一天的日历
    val remindDateCalender = Calendar.getInstance()
    //这里可以保证已经是可以解析的了
    val format = SimpleDateFormat("yy年MM月dd日hh:mm", Locale.CHINA)
    LogUtils.d("RayleighZ", "notifyTime = ${remindMode.notifyDateTime}")
    val remindDate = format.parse(remindMode.notifyDateTime)
    remindDateCalender.time = remindDate
    return remindDateCalender
}

//返回后面的四年
fun getNextFourYears(): List<String> {
    val thisYear = Calendar.getInstance().get(Calendar.YEAR)
    return listOf(
        thisYear.toString(),
        (thisYear + 1).toString(),
        (thisYear + 2).toString(),
        (thisYear + 3).toString()
    )
}

private fun getRealWeek(rawWeekNum: Int): Int =
    if (rawWeekNum == 1)
        7
    else rawWeekNum - 1

//判断用户设置的提醒是否过期
fun isOutOfTime(todo: Todo): Boolean{
    val repeatString = repeatMode2RemindTime(todo.remindMode)
    //表示永远不会提醒
    if (repeatString == "") return false

    //这里已经是经过处理的提醒时间，所以是不含年份的
    val date = SimpleDateFormat("MM月dd日 hh:mm", Locale.CHINA).parse(repeatString)

    return date.time <= System.currentTimeMillis()
}

fun getString(id: Int): String = BaseApp.context.getString(id)

fun getColor(id: Int): Int = ContextCompat.getColor(BaseApp.context, id)