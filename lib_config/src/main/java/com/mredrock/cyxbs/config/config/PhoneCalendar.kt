package com.mredrock.cyxbs.config.config

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.icu.util.TimeZone
import android.net.Uri
import android.provider.CalendarContract
import com.mredrock.cyxbs.config.ConfigApplicationWrapper.application
import java.util.*

/**
 * https://developer.android.google.cn/guide/topics/providers/calendar-provider?hl=zh-cn
 *
 * @author 985892345
 * @date 2022/9/24 20:01
 */
object PhoneCalendar {
  
  private val context = application
  
  private var calenderURL: String? = null
  private var calenderEventURL: String? = null
  private var calenderReminderURL: String? = null
  private const val CALENDARS_NAME = "掌上重邮"
  private const val CALENDARS_ACCOUNT_NAME = "掌上重邮"
  private const val CALENDARS_ACCOUNT_TYPE = "掌上重邮"
  private const val CALENDARS_DISPLAY_NAME = "掌上重邮"
  
  fun add(data: Data): Int? {
    val calendarId = checkAndAddCalendarAccounts(context).toLong()
    if (calendarId < 0) {
      // 获取日历失败直接返回
      return null
    }
    return null
  }
  
  fun delete(onlyId: Int){
  
  }
  
  fun find(onlyId: Int) {
  
  }
  
  fun update(onlyId: Int) {
  
  }
  
  /**
   * 获取日历ID
   * @param context
   * @return 日历ID
   */
  private fun checkAndAddCalendarAccounts(context: Context): Int {
    val oldId = checkCalendarAccounts(context)
    return if (oldId >= 0) {
      oldId
    } else {
      val addId = addCalendarAccount(context)
      if (addId >= 0) {
        checkCalendarAccounts(context)
      } else {
        -1
      }
    }
  }
  
  /**
   * 检查是否存在日历账户
   * @param context
   * @return
   */
  private fun checkCalendarAccounts(context: Context): Int {
    val userCursor: Cursor? = context.contentResolver.query(
      Uri.parse(calenderURL),
      null,
      null,
      null,
      CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " ASC "
    )
    return try {
      if (userCursor == null) //查询返回空值
        return -1
      val count: Int = userCursor.count
      if (count > 0) { //存在现有账户，取第一个账户的id返回
        userCursor.moveToLast()
        userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID))
      } else {
        -1
      }
    } finally {
      userCursor?.close()
    }
  }
  
  /**
   * 添加一个日历账户
   * @param context
   * @return
   */
  private fun addCalendarAccount(context: Context): Long {
    val timeZone: TimeZone = TimeZone.getDefault()
    val value = ContentValues()
    value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME)
    value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
    value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
    value.put(
      CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
      CALENDARS_DISPLAY_NAME
    )
    value.put(CalendarContract.Calendars.VISIBLE, 1)
    value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE)
    value.put(
      CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
      CalendarContract.Calendars.CAL_ACCESS_OWNER
    )
    value.put(CalendarContract.Calendars.SYNC_EVENTS, 1)
    value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.id)
    value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME)
    value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0)
    var calendarUri: Uri = Uri.parse(calenderURL)
    calendarUri = calendarUri.buildUpon()
      .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
      .appendQueryParameter(
        CalendarContract.Calendars.ACCOUNT_NAME,
        CALENDARS_ACCOUNT_NAME
      )
      .appendQueryParameter(
        CalendarContract.Calendars.ACCOUNT_TYPE,
        CALENDARS_ACCOUNT_TYPE
      )
      .build()
    val result: Uri? = context.contentResolver.insert(calendarUri, value)
    return if (result == null) -1 else ContentUris.parseId(result)
  }
  
  sealed interface Data
  
  data class OneData(
    val title: String,
    val description: String,
    val startTime: Calendar,
    val endTime: Calendar,
    val isRemind: Boolean
  ) : Data
  
  data class RepeatData(
    val title: String,
    val description: String,
    val startTime: Calendar,
    val duration: String,
    val rrule: String,
    val isRemind: Boolean
  )
}