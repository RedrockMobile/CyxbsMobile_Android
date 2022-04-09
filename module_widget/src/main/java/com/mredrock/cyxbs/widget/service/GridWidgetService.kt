package com.mredrock.cyxbs.widget.service

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.WIDGET_AFFAIR
import com.mredrock.cyxbs.common.config.WIDGET_COURSE
import com.mredrock.cyxbs.common.utils.ClassRoomParse
import com.mredrock.cyxbs.common.utils.Num2CN
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.bean.AffairStatus
import com.mredrock.cyxbs.widget.bean.CourseStatus


class GridWidgetService : RemoteViewsService() {

    companion object {

        fun getCourse(position: Int): CourseStatus.Course? {
            val mPosition = position - 7
            return makeSchedules()?.let { it[mPosition / 7][mPosition % 7]?.get(0) }
        }

        private fun makeSchedules(): Array<Array<MutableList<CourseStatus.Course>?>>? {
            val mSchedulesArray = Array(6) { arrayOfNulls<MutableList<CourseStatus.Course>>(7) }
            val json = BaseApp.appContext.defaultSharedPreferences.getString(WIDGET_COURSE, "")
            val courses = Gson().fromJson(json, CourseStatus::class.java)?.data ?: return null
            val affairJson = BaseApp.appContext.defaultSharedPreferences.getString(WIDGET_AFFAIR, "")
            val affairs = Gson().fromJson(affairJson, AffairStatus::class.java) ?: return null
            val schoolCalendar = SchoolCalendar()

            //下方复用代码，忽视就好
            fun initSchedulesArray(row: Int, column: Int) {
                if (mSchedulesArray[row][column] == null) {
                    mSchedulesArray[row][column] = mutableListOf()
                }
            }

            //遍历添加课表
            for (course in courses) {
                val row = course.hash_lesson
                val column = course.hash_day

                //如果是整学期将所有数据返回
                if (schoolCalendar.weekOfTerm == 0) {
                    if (course.customType == CourseStatus.COURSE) {
                        initSchedulesArray(row, column)
                        mSchedulesArray[row][column]?.add(course)
                    }
                    continue
                }
                //如果不是整学期的做如下判断
                course.week?.let {
                    //如果本周在这个课所在的周数list当中
                    if (schoolCalendar.weekOfTerm in it && course.customType == CourseStatus.COURSE) {
                        initSchedulesArray(row, column)
                        mSchedulesArray[row][column]?.add(course)
                        //针对三节课做处理
                        if (course.period == 3) {
                            initSchedulesArray(row + 1, column)
                            mSchedulesArray[row + 1][column]?.add(course.copy().apply { period = 1 })
                        } else if (course.period == 4) {
                            initSchedulesArray(row + 1, column)
                            mSchedulesArray[row + 1][column]?.add(course)
                        }
                    }
                }
            }

            //遍历添加事务
            for (affair in affairs) {
                val row = affair.hash_lesson
                val column = affair.hash_day
                //将事务转换成课表，方便复用
                val affairToCourse = CourseStatus.Course(affair.hash_day,
                        affair.hash_lesson,
                        affair.begin_lesson,
                        "",
                        "",
                        affair.course,
                        "",
                        "",
                        affair.classroom,
                        "",
                        "",
                        affair.weekBegin,
                        affair.weekEnd,
                        affair.type,
                        affair.period,
                        affair.week, affair.customType)
                //如果是整学期将所有数据返回
                if (schoolCalendar.weekOfTerm == 0) {
                    if (affair.customType == CourseStatus.COURSE) {
                        initSchedulesArray(row, column)
                        mSchedulesArray[row][column]?.add(
                                affairToCourse)
                    }
                    continue
                }
                //如果不是整学期的做如下判断
                affair.week.let {
                    //如果本周在这个课所在的周数list当中
                    if (schoolCalendar.weekOfTerm in it && affair.customType == CourseStatus.AFFAIR) {
                        initSchedulesArray(row, column)
                        mSchedulesArray[row][column]?.add(affairToCourse)
                        //针对三节课做处理
                        if (affair.period == 3) {
                            initSchedulesArray(row + 1, column)
                            mSchedulesArray[row + 1][column]?.add(affairToCourse.copy().apply { period = 1 })
                        } else if (affair.period == 4) {
                            initSchedulesArray(row + 1, column)
                            mSchedulesArray[row + 1][column]?.add(affairToCourse)
                        }
                    }
                }
            }
            return mSchedulesArray
        }
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return GridRemoteViewsFactory(this, intent)
    }

    private inner class GridRemoteViewsFactory(val mContext: Context, val intent: Intent) : RemoteViewsFactory {

        private var mSchedulesArray: Array<Array<MutableList<CourseStatus.Course>?>>? = null

        private fun initCourse() {
            mSchedulesArray = makeSchedules()
        }

        override fun getViewAt(position: Int): RemoteViews? {
            //获取当前时间
            val time = intent.getIntExtra("time", 0)
            //如果是前7个说明是显示今天是星期几
            if (position < 7) {
                //获取当前item是否应该高亮
                val id = if (time == position)
                    R.layout.widget_grid_view_day_of_week_selected_item
                else R.layout.widget_grid_view_day_of_week_item
                //返回对应的item
                return RemoteViews(mContext.packageName, id).apply {
                    setTextViewText(R.id.tv_day_of_week, "周${
                    if (position != 6) Num2CN.number2ChineseNumber((position + 1).toLong()) else "日"
                    }")
                }
            }
            mSchedulesArray?.let { mSchedulesArray ->
                val mPosition = position - 7
                val course = mSchedulesArray[mPosition / 7][mPosition % 7]?.get(0)
                if (course == null) {
                    return RemoteViews(mContext.packageName, R.layout.widget_grid_view_item_blank)
                } else {
                    /** 如果是事务，设置事务的背景*/
                    val remoteViews: RemoteViews = if (course.customType == 1) {
                        RemoteViews(mContext.packageName, R.layout.widget_grid_view_item_affair)
                    } else {
                        when {
                            /**
                             *根据早上，下午，晚上的课设置不同背景
                             * */
                            mPosition < 14 && course.period == 1 -> RemoteViews(mContext.packageName, R.layout.widget_grid_view_item_half_moring)
                            mPosition < 14 -> RemoteViews(mContext.packageName, R.layout.widget_grid_view_moring_item)
                            mPosition < 28 && course.period == 1 -> RemoteViews(mContext.packageName, R.layout.widget_grid_view_item_half_afternoon)
                            mPosition < 28 -> RemoteViews(mContext.packageName, R.layout.widget_grid_view_afternoon_item)
                            course.period == 1 -> RemoteViews(mContext.packageName, R.layout.widget_grid_view_item_half_night)
                            else -> RemoteViews(mContext.packageName, R.layout.widget_grid_view_night_item)
                        }
                    }
                    remoteViews.setTextViewText(R.id.top, "${course.course}")
                    remoteViews.setTextViewText(R.id.bottom, ClassRoomParse.parseClassRoom(course.classroom
                            ?: ""))

                    remoteViews.setOnClickFillInIntent(R.id.background, Intent().putExtra("POSITION", position))
                    return remoteViews
                }
            }
            return null
        }

        /**
         * 初始化GridView的数据
         * @author skywang
         */
        private fun initGridViewData() {
            initCourse()
        }

        override fun onCreate() {
            initGridViewData()
        }

        override fun getCount(): Int { // 返回“集合视图”中的数据的总数
            return 7 * 7
        }

        override fun getItemId(position: Int): Long { // 返回当前项在“集合视图”中的位置
            return position.toLong()
        }

        override fun getLoadingView() = RemoteViews(mContext.packageName, R.layout.widget_grid_view_item_blank)

        override fun getViewTypeCount() = 10

        override fun hasStableIds() = true

        override fun onDataSetChanged() {}
        override fun onDestroy() {}
    }
}