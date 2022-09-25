package com.mredrock.cyxbs.lib.utils.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.icu.util.TimeZone
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import android.text.TextUtils
import androidx.annotation.NonNull


/**
 * author: WhiteNight(1448375249@qq.com)
 * date: 2022/9/13
 * description: 处理系统日历的工具类,可以添加事件和删除事件
 */

/**
 * 使用前一定要申请权限! READ_CALENDAR 和 WRITE_CALENDAR
 *
 */
object CalendarUtils {
  private var calenderURL: String? = null
  private var calenderEventURL: String? = null
  private var calenderReminderURL: String? = null
  private const val CALENDARS_NAME = "XXXX"
  private const val CALENDARS_ACCOUNT_NAME = "XXXX"
  private const val CALENDARS_ACCOUNT_TYPE = "XXXXX"
  private const val CALENDARS_DISPLAY_NAME = "XXXXX"

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

  /**
   * 向日历中添加一个事件(重复事件)
   * @param context
   * @param calendar_id （必须参数）
   * @param title
   * @param description
   * @param beginTime 事件开始时间，以从公元纪年开始计算的协调世界时毫秒数表示。 （必须参数）
   * @param duration 持续时间。 例:PT1H45M0S 指定了一小时四十五分零秒的时间间隔 （重复事件：必须参数）
   * @param rrule 重复规则。  例:FREQ=DAILY;COUNT=10 每天发生一次，重复10次 （重复事件：必须参数）
   * @return
   */
  private fun insertCalendarEvent(
    context: Context,
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
    event.put(CalendarContract.Events.DTSTART, beginTime) //必须有
    event.put(CalendarContract.Events.DURATION, duration) //持续时间
    event.put(CalendarContract.Events.RRULE, rrule)
    event.put(CalendarContract.Events.HAS_ALARM, 1) //设置有闹钟提醒
    event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id) //这个是时区，必须有，
    //添加事件
    return context.contentResolver.insert(Uri.parse(calenderEventURL), event)
  }

  /**
   * 向日历中添加一个事件(重复事件)
   * @param context
   * @param calendar_id （必须参数）
   * @param title
   * @param description
   * @param beginTime 事件开始时间，以从公元纪年开始计算的协调世界时毫秒数表示。 （必须参数）
   * @param endTime 事件结束时间，以从公元纪年开始计算的协调世界时毫秒数表示。（非重复事件：必须参数）
   * @return
   */
  private fun insertCalendarEvent(
    context: Context,
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
    event.put(CalendarContract.Events.DTSTART, beginTime) //必须有
    event.put(CalendarContract.Events.DTEND, endTime) //非重复事件：必须有
    event.put(CalendarContract.Events.HAS_ALARM, 1) //设置有闹钟提醒
    event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id) //这个是时区，必须有，
    //添加事件
    return context.contentResolver.insert(Uri.parse(calenderEventURL), event)
  }

  /**
   * 查询日历事件
   * @param context
   * @param title 事件标题
   * @return 事件id,查询不到则返回""
   */
  private fun queryCalendarEvent(
    context: Context,
    calendar_id: Long,
    title: String,
    description: String,
    start_time: Long,
  ): String {
    val cursor: Cursor =
      context.contentResolver.query(Uri.parse(calenderEventURL), null, null, null, null)!!
    var tmpTitle: String
    var tmpDesc: String?
    var tempCalendarId: Long
    if (cursor.moveToFirst()) {
      do {
        tmpTitle = cursor.getString(cursor.getColumnIndex("title"))
        tmpDesc = cursor.getString(cursor.getColumnIndex("description"))
        tempCalendarId = cursor.getLong(cursor.getColumnIndex("calendar_id"))
        val dtstart: Long = cursor.getLong(cursor.getColumnIndex("dtstart"))
        if (TextUtils.equals(title, tmpTitle) && TextUtils.equals(
            description,
            tmpDesc
          ) && calendar_id == tempCalendarId && dtstart == start_time
        ) {
          return cursor.getString(cursor.getColumnIndex("_id"))
        }
      } while (cursor.moveToNext())
    }
    return ""
  }

  /**
   * 添加日历提醒：标题、描述、开始时间共同标定一个单独的提醒事件
   * @param context
   * @param title 日历提醒的标题,不允许为空
   * @param description 日历的描述（备注）信息
   * @param beginTime 事件开始时间，以从公元纪年开始计算的协调世界时毫秒数表示。
   * @param endTime 事件结束时间，以从公元纪年开始计算的协调世界时毫秒数表示。
   * @param remind_minutes 提前remind_minutes分钟发出提醒
   * @param callback 添加提醒是否成功结果监听
   */
  fun addCalendarEventRemind(
    context: Context,
    @NonNull title: String,
    description: String,
    beginTime: Long,
    endTime: Long,
    remind_minutes: Int,
    callback: OnCalendarRemindListener?
  ) {
    val calendarId = checkAndAddCalendarAccounts(context).toLong()
    if (calendarId < 0) {
      // 获取日历失败直接返回
      callback?.onFailed(OnCalendarRemindListener.Status._CALENDAR_ERROR)
      return
    }
    //根据标题、描述、开始时间查看提醒事件是否已经存在
    var eventId = queryCalendarEvent(context, calendarId, title, description, beginTime)
    //如果提醒事件不存在，则新建事件
    if (TextUtils.isEmpty(eventId)) {
      val newEvent: Uri? =
        insertCalendarEvent(context, calendarId, title, description, beginTime, endTime)
      if (newEvent == null) {
        // 添加日历事件失败直接返回
        callback?.onFailed(OnCalendarRemindListener.Status._EVENT_ERROR)
        return
      }
      eventId = ContentUris.parseId(newEvent).toString() + ""
    }
    //为事件设定提醒
    val values = ContentValues()
    values.put(CalendarContract.Reminders.EVENT_ID, eventId)
    // 提前remind_minutes分钟有提醒
    values.put(CalendarContract.Reminders.MINUTES, remind_minutes)
    values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
    val uri: Uri? = context.contentResolver.insert(Uri.parse(calenderReminderURL), values)
    if (uri == null) {
      // 添加提醒失败直接返回
      callback?.onFailed(OnCalendarRemindListener.Status._REMIND_ERROR)
      return
    }

    //添加提醒成功
    callback?.onSuccess()
  }

  /**
   * 添加日历提醒：标题、描述、开始时间共同标定一个单独的提醒事件
   * @param context
   * @param title 日历提醒的标题,不允许为空
   * @param description 日历的描述（备注）信息
   * @param beginTime 事件开始时间，以从公元纪年开始计算的协调世界时毫秒数表示。
   * @param remind_minutes 提前remind_minutes分钟发出提醒
   * @param callback 添加提醒是否成功结果监听
   */
  fun addCalendarEventRemind(
    context: Context,
    @NonNull title: String,
    description: String,
    beginTime: Long,
    duration: String,
    rrule: String,
    remind_minutes: Int,
    callback: OnCalendarRemindListener?
  ) {
    val calendarId = checkAndAddCalendarAccounts(context).toLong()
    if (calendarId < 0) {
      // 获取日历失败直接返回
      callback?.onFailed(OnCalendarRemindListener.Status._CALENDAR_ERROR)
      return
    }
    //根据标题、描述、开始时间查看提醒事件是否已经存在
    var eventId = queryCalendarEvent(context, calendarId, title, description, beginTime)
    //如果提醒事件不存在，则新建事件
    if (TextUtils.isEmpty(eventId)) {
      val newEvent: Uri? =
        insertCalendarEvent(context, calendarId, title, description, beginTime, duration,rrule)
      if (newEvent == null) {
        // 添加日历事件失败直接返回
        callback?.onFailed(OnCalendarRemindListener.Status._EVENT_ERROR)
        return
      }
      eventId = ContentUris.parseId(newEvent).toString() + ""
      //为事件设定提醒
      val values = ContentValues()
      values.put(CalendarContract.Reminders.EVENT_ID, eventId)
      // 提前remind_minutes分钟有提醒
      values.put(CalendarContract.Reminders.MINUTES, remind_minutes)
      values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
      val uri: Uri? = context.contentResolver.insert(Uri.parse(calenderReminderURL), values)
      if (uri == null) {
        // 添加提醒失败直接返回
        callback?.onFailed(OnCalendarRemindListener.Status._REMIND_ERROR)
        return
      }
    }
    //添加提醒成功
    callback?.onSuccess()
  }

  /**
   * 删除日历提醒事件：根据标题、描述和开始时间来定位日历事件
   * @param context
   * @param title 提醒的标题
   * @param description 提醒的描述：deeplink URI
   * @param startTime 事件的开始时间
   * @param callback 删除成功与否的监听回调
   */
  fun deleteCalendarEventRemind(
    context: Context,
    title: String,
    description: String,
    startTime: Long,
    callback: OnCalendarRemindListener?
  ) {
    val eventCursor: Cursor? =
      context.contentResolver.query(Uri.parse(calenderEventURL), null, null, null, null)
    try {
      if (eventCursor == null) //查询返回空值
        return
      if (eventCursor.count > 0) {
        //遍历所有事件，找到title、description、startTime跟需要查询的title、descriptio、dtstart一样的项
        eventCursor.moveToFirst()
        while (!eventCursor.isAfterLast) {
          val eventTitle: String = eventCursor.getString(eventCursor.getColumnIndex("title"))
          val eventDescription: String? =
            eventCursor.getString(eventCursor.getColumnIndex("description"))
          val dtstart: Long = eventCursor.getLong(eventCursor.getColumnIndex("dtstart"))
          if (!TextUtils.isEmpty(title) && title == eventTitle  && dtstart == startTime) {
            val id: Int =
              eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID)) //取得id
            val deleteUri: Uri =
              ContentUris.withAppendedId(Uri.parse(calenderEventURL), id.toLong())
            val rows: Int = context.contentResolver.delete(deleteUri, null, null)
            if (rows == -1) {
              // 删除提醒失败直接返回
              callback?.onFailed(OnCalendarRemindListener.Status._REMIND_ERROR)
              return
            }
            //删除提醒成功
            callback?.onSuccess()
          }
          eventCursor.moveToNext()
        }
      }
    } finally {
      eventCursor?.close()
    }
  }

  /**
   * 日历提醒添加成功与否监控器
   */
  interface OnCalendarRemindListener {
    enum class Status {
      _CALENDAR_ERROR, _EVENT_ERROR, _REMIND_ERROR
    }

    fun onFailed(error_code: Status?)
    fun onSuccess()
  }

  /**
   * 初始化uri
   */
  init {
    if (Build.VERSION.SDK_INT >= 8) {
      calenderURL = "content://com.android.calendar/calendars"
      calenderEventURL = "content://com.android.calendar/events"
      calenderReminderURL = "content://com.android.calendar/reminders"
    } else {
      calenderURL = "content://calendar/calendars"
      calenderEventURL = "content://calendar/events"
      calenderReminderURL = "content://calendar/reminders"
    }
  }
}