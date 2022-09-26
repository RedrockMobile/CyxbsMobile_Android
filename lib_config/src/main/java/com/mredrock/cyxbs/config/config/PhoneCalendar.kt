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

/**
 * https://developer.android.google.cn/guide/topics/providers/calendar-provider?hl=zh-cn
 *
 * @author 985892345
 * @date 2022/9/24 20:01
 */
object PhoneCalendar {

  private val context = application

  private var calenderURL: String? = "content://com.android.calendar/calendars"
  private var calenderEventURL: String? = "content://com.android.calendar/events"
  private var calenderReminderURL: String? = "content://com.android.calendar/reminders"
  private const val CALENDARS_NAME = "RedRock"
  private const val CALENDARS_ACCOUNT_NAME = "CYXBS"
  private const val CALENDARS_ACCOUNT_TYPE = "Android"
  private const val CALENDARS_DISPLAY_NAME = "重邮小帮手"

  /**
   * 添加事件,成功返回事件Id,失败返回null
   */
  fun add(data: Data): Long? {
    val calendarId = checkAndAddCalendarAccounts(context).toLong()
    if (calendarId < 0) {
      // 获取日历失败直接返回
      return null
    }
    val eventId: Long?
    when (data) {
      is RepeatData -> {
        val newEvent: Uri? =
          insertCalendarEvent(
            calendarId,
            data.title,
            data.description,
            data.beginTime,
            data.duration,
            data.rrule
          )
        if (newEvent == null) {
          // 添加日历事件失败直接返回
          return null
        }
        eventId = newEvent.lastPathSegment?.toLong()
        addRemind(eventId!!, data.remind)
      }
      is OnceData -> {
        val newEvent: Uri? =
          insertCalendarEvent(
            calendarId,
            data.title,
            data.description,
            data.beginTime,
            data.endTime,
          )
        if (newEvent == null) {
          // 添加日历事件失败直接返回
          return null
        }
        eventId = newEvent.lastPathSegment?.toLong()
        if (!addRemind(eventId!!, data.remind)) {
          // 添加提醒失败
          return null
        }
      }
    }
    return eventId
  }

  /**
   * 添加事件,成功返回true,失败返回false
   */
  fun delete(affairId: Long): Boolean {
    val eventCursor: Cursor? =
      context.contentResolver.query(Uri.parse(calenderEventURL), null, null, null, null)
    try {
      if (eventCursor == null) //查询返回空值
        return false
      if (eventCursor.count > 0) {
        //遍历所有事件，找到title、description、startTime跟需要查询的title、descriptio、dtstart一样的项
        eventCursor.moveToFirst()
        while (!eventCursor.isAfterLast) {
          val tmpAffairId: String =
            eventCursor.getString(eventCursor.getColumnIndex("_id"))
          if (tmpAffairId.toLong() == affairId) {
            val deleteUri: Uri =
              ContentUris.withAppendedId(Uri.parse(calenderEventURL), affairId)
            val rows: Int = context.contentResolver.delete(deleteUri, null, null)
            if (rows == -1) {
              // 删除提醒失败直接返回
              return false
            }
            return true
          }
          eventCursor.moveToNext()
        }
      }
    } finally {
      eventCursor?.close()
    }
    return false
  }

  /**
   * 更新事件,成功返回true,失败返回false
   */
  fun update(data: Data, affairID: Long): Boolean {
    val calendarId = checkAndAddCalendarAccounts(context).toLong()
    if (calendarId < 0) {
      // 获取日历失败直接返回
      return false
    }
    //根据事务Id查看提醒事件是否已经存在
    val isExist = queryCalendarEvent(calendarId, affairID)
    val eventId: String?
    when (data) {
      is RepeatData -> {
        //如果事件不存在，则新建事件
        if (!isExist) {
          val newEvent: Uri? =
            insertCalendarEvent(
              calendarId,
              data.title,
              data.description,
              data.beginTime,
              data.duration,
              data.rrule
            )
          if (newEvent == null) {
            // 添加日历事件失败直接返回
            return false
          }
          // 获取唯一Id
          eventId = newEvent.lastPathSegment
        } else {
          // 如果事件存在，则更新事件
          val values = ContentValues().apply {
            // 更新数据
            put("title", data.title)
            put("description", data.description)
            put(CalendarContract.Events.DTSTART, data.beginTime) // 开始时间
            put(CalendarContract.Events.DURATION, data.duration) // 持续时间
            put(CalendarContract.Events.RRULE, data.rrule) // 重复规则
            put(CalendarContract.Events.HAS_ALARM, 1) // 设置提醒
          }
          eventId = affairID.toString()
          val updateUri: Uri =
            ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId.toLong())
          val rows: Int = context.contentResolver.update(updateUri, values, null, null)
        }
        if (!addRemind(eventId!!.toLong(), data.remind)) {
          // 添加提醒失败直接返回
          return false
        }
        return true
      }
      is OnceData -> {
        //如果事件不存在，则新建事件
        if (!isExist) {
          val newEvent: Uri? =
            insertCalendarEvent(
              calendarId,
              data.title,
              data.description,
              data.beginTime,
              data.endTime
            )
          if (newEvent == null) {
            // 添加日历事件失败直接返回
            return false
          }
          // 获取唯一Id
          eventId = newEvent.lastPathSegment
        } else {
          // 如果事件存在，则更新事件
          val values = ContentValues().apply {
            // 更新数据
            put("title", data.title)
            put("description", data.description)
            put(CalendarContract.Events.DTSTART, data.beginTime) // 开始时间
            put(CalendarContract.Events.DTEND, data.endTime) // 结束时间
            put(CalendarContract.Events.HAS_ALARM, 1) // 设置提醒
          }
          eventId = affairID.toString()
          val updateUri: Uri =
            ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId.toLong())
          val rows: Int = context.contentResolver.update(updateUri, values, null, null)
        }
        if (!addRemind(eventId!!.toLong(), data.remind)) {
          // 添加提醒失败直接返回
          return false
        }
        return true
      }
    }
  }

  /**
   * 为事件设定提醒
   */
  private fun addRemind(affairId: Long, remind: Int): Boolean {
    val values = ContentValues()
    values.put(CalendarContract.Reminders.EVENT_ID, affairId)
    // 提前remind_minutes分钟有提醒
    values.put(CalendarContract.Reminders.MINUTES, remind)
    values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
    val uri: Uri? = context.contentResolver.insert(Uri.parse(calenderReminderURL), values)
    if (uri == null) {
      // 添加提醒失败直接返回
      return false
    }
    return true
  }

  /**
   * 查询日历事件
   * @return 事件id,查询不到则返回""
   */
  private fun queryCalendarEvent(
    calendar_id: Long,
    affairId: Long,
  ): Boolean {
    val cursor: Cursor =
      context.contentResolver.query(Uri.parse(calenderEventURL), null, null, null, null)!!
    var tempCalendarId: Long
    if (cursor.moveToFirst()) {
      do {
        tempCalendarId = cursor.getLong(cursor.getColumnIndex("calendar_id"))
        val tempAffairId: String = cursor.getString(cursor.getColumnIndex("_id"))
        if (calendar_id == tempCalendarId && affairId == tempAffairId.toLong()
        ) {
          return true
        }
      } while (cursor.moveToNext())
    }
    return false
  }

  /**
   * 向日历中添加一个事件(重复事件)
   */
  private fun insertCalendarEvent(
    calendar_id: Long,
    title: String,
    description: String,
    beginTime: Long,
    duration: String,
    rrule: String,
  ): Uri? {
    val event = ContentValues()
    event.put("title", title)
    event.put("description", description)
    // 插入账户的id
    event.put("calendar_id", calendar_id)
    event.put(CalendarContract.Events.DTSTART, beginTime) //开始时间
    event.put(CalendarContract.Events.DURATION, duration) //持续时间
    event.put(CalendarContract.Events.RRULE, rrule) //重复规则
    event.put(CalendarContract.Events.HAS_ALARM, 1) //设置提醒
    event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id) //设置时区
    //添加事件
    return context.contentResolver.insert(Uri.parse(calenderEventURL), event)
  }

  /**
   * 向日历中添加一个事件(一次性事件)
   */
  private fun insertCalendarEvent(
    calendar_id: Long,
    title: String,
    description: String,
    beginTime: Long,
    endTime: Long
  ): Uri? {
    val event = ContentValues()
    event.put("title", title)
    event.put("description", description)
    // 插入账户的id
    event.put("calendar_id", calendar_id)
    event.put(CalendarContract.Events.DTSTART, beginTime) // 开始时间
    event.put(CalendarContract.Events.DTEND, endTime) // 结束时间
    event.put(CalendarContract.Events.HAS_ALARM, 1) //设置有提醒
    event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id) //设置时区
    //添加事件
    return context.contentResolver.insert(Uri.parse(calenderEventURL), event)
  }

  /**
   * 获取日历ID
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

  /**
   * 一次性事件
   * @param beginTime 事件开始时间,以从公元纪年开始计算的世界时毫秒数表示.
   * @param remind 提醒时间,单位:分钟
   */
  data class OnceData(
    val title: String,
    val description: String,
    val beginTime: Long,
    val endTime: Long,
    val remind: Int // 分钟
  ) : Data

  /**
   * 重复事件
   * @param beginTime 事件开始时间,以从公元纪年开始计算的世界时毫秒数表示.
   * @param duration 持续时间. 例:PT1H45M0S 指定了一小时四十五分零秒的时间间隔
   * @param rrule 重复规则. 例:FREQ=DAILY;COUNT=10 每天发生一次，重复10次
   * @param remind 提醒时间,单位:分钟
   */
  data class RepeatData(
    val title: String,
    val description: String,
    val beginTime: Long,
    val duration: String,
    val rrule: String,
    val remind: Int // 分钟
  ) : Data
}