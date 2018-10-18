package com.mredrock.cyxbs.course.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.CursorIndexOutOfBoundsException
import android.database.sqlite.SQLiteException
import android.provider.CalendarContract.Calendars
import android.provider.CalendarContract.Events
import android.provider.CalendarContract.Reminders
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.network.Course
import java.util.*

/**
 * This class is used to add the [com.mredrock.cyxbs.course.network.Affair] to the phone's calendar.
 *
 * Created by anriku on 2018/10/11.
 */
class AffairToCalendar(private val mActivity: FragmentActivity, private val mAffairs: List<Course>) {

    companion object {
        private const val TAG = "AffairToCalendar"
        private const val AFFAIR_DESCRIPTION = "来自掌上重邮的备忘"
    }

    private var mCalendarId: Long = 0
    private val mProjection = arrayOf(Calendars._ID)
    private val mProjectionId = 0
    // mGetIdSelection and mGetIdSelectionArgs are used to get the calendar which can be modified by
    // user.
    private val mGetIdSelection = "((" + Calendars.VISIBLE + " = ?) AND (" +
            Calendars.CALENDAR_ACCESS_LEVEL + " >= ?))"
    private val mGetIdSelectionArgs = arrayOf("1", Calendars.CAL_ACCESS_CONTRIBUTOR.toString())
    // mDeleteSelection and mDeleteSelectionArgs are used to get the calendar's affair added by
    // Zhangyou
    private val mDeleteSelection = "((" + Events.DESCRIPTION + " = ?))"
    private val mDeleteSelectionArgs = arrayOf(AFFAIR_DESCRIPTION)


    /**
     * Check the permission to insert.
     */
    fun getPermissionToInsert() {
        mActivity.doPermissionAction(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR) {

            reason = mActivity.getString(R.string.course_calendar_permission_reason)

            doAfterGranted {
                queryCalendarId()
                deleteAffairFromCalendar()
                insertAffairToCalendar(mAffairs)
            }

            doAfterRefused {
                Toast.makeText(mActivity, mActivity.getString(R.string.course_refuse_calendar_permission),
                        Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * This method is used to query the Calendar which can modify it events.
     */
    @SuppressLint("MissingPermission")
    private fun queryCalendarId() {
        val cr = mActivity.contentResolver
        try {
            val cursor = cr.query(Calendars.CONTENT_URI, mProjection, mGetIdSelection, mGetIdSelectionArgs,
                    Calendars._ID + " ASC")
            cursor.moveToFirst()
            mCalendarId = cursor.getLong(mProjectionId)
            cursor.close()
        } catch (e: CursorIndexOutOfBoundsException){
            Toast.makeText(mActivity, mActivity.getString(R.string.course_fail_get_calendar_id), Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * Traverse all the affairs execute the concrete add action.
     *
     * @param affairs All affairs going to be added.
     */
    private fun insertAffairToCalendar(affairs: List<Course>) {
        for (affair in affairs) {
            affair.week?.let {
                for (week in it) {
                    val affairTimeInMillis = getAffairTimeMillis(week, affair.hashDay,
                            affair.hashLesson)
                    insert(affair, affairTimeInMillis)
                }
            }
        }
    }

    /**
     * Delete the events in the local calendar.
     */
    @SuppressLint("MissingPermission")
    private fun deleteAffairFromCalendar() {
        val cr = mActivity.contentResolver
        cr.delete(Events.CONTENT_URI, mDeleteSelection, mDeleteSelectionArgs)
    }


    /**
     * This method is used to insert the affairs to the local calendar.
     *
     * @param affair Single affair going to be add as a event to the calendar.
     * @param affairTimeInMillis A class record the event's start time and end time.
     */
    @SuppressLint("MissingPermission")
    private fun insert(affair: Course, affairTimeInMillis: AffairTimeInMillis) {
        val cr = mActivity.contentResolver
        val eventValues = ContentValues()
        eventValues.put(Events.DTSTART, affairTimeInMillis.startTimeInMillis)
        eventValues.put(Events.DTEND, affairTimeInMillis.endTimeInMillis)
        eventValues.put(Events.TITLE, affair.course)
        eventValues.put(Events.DESCRIPTION, AFFAIR_DESCRIPTION)
        eventValues.put(Events.CALENDAR_ID, mCalendarId)
        eventValues.put(Events.EVENT_TIMEZONE, "UTC/GMT+08:00")

        val uri = cr.insert(Events.CONTENT_URI, eventValues)
        val eventId = uri.lastPathSegment
        // Add reminder
        val remindValues = ContentValues()
        remindValues.put(Reminders.MINUTES, affair.affairTime?.toInt() ?: 0)
        remindValues.put(Reminders.EVENT_ID, eventId)
        remindValues.put(Reminders.METHOD, Reminders.METHOD_ALERT)

        try {
            cr.insert(Reminders.CONTENT_URI, remindValues)
        } catch (e: SQLiteException){
            Toast.makeText(mActivity, mActivity.getString(R.string.course_fail_get_calendar_id), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Convert the affair's start time and end time to the [AffairTimeInMillis]
     *
     * @param week The week of the affair.
     * @param hashDay The range of hashDay is 0~6. 0 represent Monday, 6 represent Sunday.
     * @param classX The range of classX is 0~5. 0 represent 1,2 class, 5 represent 11,12 class.
     *
     * @return ...
     */
    private fun getAffairTimeMillis(week: Int, hashDay: Int, classX: Int): AffairTimeInMillis {
        val calendar = SchoolCalendar(week, hashDay + 1)

        val courseTimeParse = CourseTimeParse(classX)
        val startTime = courseTimeParse.parseStartCourseTime()
        val endTime = courseTimeParse.parseEndCourseTime()

        val startTimeCalendar = Calendar.getInstance()
        startTimeCalendar.set(calendar.year, calendar.month - 1, calendar.day,
                startTime.hour.toInt(), startTime.minute.toInt())

        val endTimeCalendar = Calendar.getInstance()
        endTimeCalendar.set(calendar.year, calendar.month - 1, calendar.day,
                endTime.hour.toInt(), endTime.minute.toInt())

        return AffairTimeInMillis(startTimeCalendar.timeInMillis, endTimeCalendar.timeInMillis)
    }

    /**
     * A class contain a affair's startTime and endTime as millis.
     */
    data class AffairTimeInMillis(val startTimeInMillis: Long, val endTimeInMillis: Long)
}