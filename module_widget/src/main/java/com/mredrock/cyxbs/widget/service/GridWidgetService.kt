package com.mredrock.cyxbs.widget.service

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.mredrock.cyxbs.lib.base.BaseApp
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.repo.bean.AffairEntity
import com.mredrock.cyxbs.widget.repo.bean.LessonEntity
import com.mredrock.cyxbs.widget.repo.database.AffairDatabase
import com.mredrock.cyxbs.widget.util.*
import java.util.*
import kotlin.concurrent.thread

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 */
class GridWidgetService : RemoteViewsService() {

    companion object {
        private lateinit var myLessons: List<LessonEntity>
        private lateinit var otherLessons: Array<Array<LessonEntity?>>
        private lateinit var affairs: Array<Array<AffairEntity?>>

        fun getLesson(position: Int): LessonEntity? {
            val mPosition = position - 7
            return makeSchedules(BaseApp.baseApp).let { it[mPosition / 7][mPosition % 7] }
        }

        private fun makeSchedules(context: Context): Array<Array<LessonEntity?>> {
            val weekOfTerm = SchoolCalendar().weekOfTerm
            myLessons = getMyLessons(weekOfTerm)
            otherLessons = getOthersStuNum(weekOfTerm).schedule()
            affairs =
                AffairDatabase.INSTANCE.getAffairDao()
                    .queryAllAffair(weekOfTerm).schedule()
            return myLessons.schedule()
        }

        private fun List<LessonEntity>.schedule(): Array<Array<LessonEntity?>> {
            val mScheduleLessons = Array(6) { arrayOfNulls<LessonEntity>(7) }
            for (i in 0 until size) {
                get(i).let {
                    val row = if (it.beginLesson < 4)
                        it.beginLesson / 2
                    else if (it.beginLesson < 8)
                        it.beginLesson / 2 + 1
                    else
                        it.beginLesson / 2 + 2
                    val column = it.hashDay + 1
                    mScheduleLessons[row][column] = it
                    if (it.period == 3) {
                        mScheduleLessons[row + 1][column] = it.copy(period = 1)
                    } else if (it.period == 4) {
                        mScheduleLessons[row + 1][column] = it
                    }
                }
            }
            return mScheduleLessons
        }

        private fun List<AffairEntity>.schedule(): Array<Array<AffairEntity?>> {
            val mScheduleAffairs = Array(8) { arrayOfNulls<AffairEntity>(8) }
            for (i in 0 until size) {
                get(i).let {
                    val row = if (it.beginLesson < 4)
                        it.beginLesson / 2
                    else if (it.beginLesson < 8)
                        it.beginLesson / 2 + 1
                    else
                        it.beginLesson / 2 + 2
                    val column = it.day + 1
                    mScheduleAffairs[row][column] = it
                    if (it.period == 3) {
                        mScheduleAffairs[row + 1][column] = it.copy(period = 1)
                    } else if (it.period == 4) {
                        mScheduleAffairs[row + 1][column] = it
                    }
                }
            }
            return mScheduleAffairs
        }
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return GridRemoteViewsFactory(this, intent)
    }

    private inner class GridRemoteViewsFactory(val mContext: Context, val intent: Intent) :
        RemoteViewsFactory {

        private var mScheduleLessons: Array<Array<LessonEntity?>>? = null

        override fun getViewAt(position: Int): RemoteViews? {
            //获取当前时间
            val lastClickPosition =
                mContext.getSharedPreferences("module_widget", Context.MODE_PRIVATE)
                    .getInt("position", -1)
            //如果等于0，说明是第1列的，该显示课程是第几节
            if (position % 8 == 0)
                return getLessonNumRv(position)
            //如果是第1到8个说明是显示今天是星期几
            if (position in 1..7) return getWeekDayRv(position)
            val remoteViews = mScheduleLessons?.let { mSchedulesArray ->
                val mPosition = position - 7
                val lesson = mSchedulesArray[mPosition / 7][mPosition % 7]
                if (lesson == null) {
                    if (otherLessons[mPosition / 7][mPosition % 7] != null)
                    /** 如果有同学的课，设置other_course的背景*/
                        getOtherLessonRV(mPosition, position)
                    else if (affairs[mPosition / 7][mPosition % 7] != null)
                    /** 如果是事务，设置事务的背景*/
                        RemoteViews(mContext.packageName, R.layout.widget_grid_view_item_affair)
                    else
                    /** 无事务也无课*/
                    {
                        /**新增事务，显示加号*/
                        if (mPosition == lastClickPosition) {
                            RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_item_blank_add_affair).apply {
                                intent.putExtra("position", mPosition)
                                setOnClickFillInIntent(R.id.background, intent)
                            }
                        } else {
                            RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_item_blank).apply {
                                intent.putExtra("position", mPosition)
                                setOnClickFillInIntent(R.id.background, intent)
                            }
                        }
                    }
                } else {
                    val remoteViews: RemoteViews =
                        when {
                            /**根据早上，下午，晚上的课设置不同背景*/
                            mPosition < 14 && lesson.period == 1 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_item_half_moring)
                            mPosition < 14 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_moring_item)
                            mPosition < 28 && lesson.period == 1 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_item_half_afternoon)
                            mPosition < 28 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_afternoon_item)
                            lesson.period == 1 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_item_half_night)
                            else -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_night_item)
                        }
                    /**如果有事务，则在上面添加小红点*/
                    remoteViews.setViewVisibility(R.id.affair, View.GONE)
                    if (affairs[mPosition / 7][mPosition % 7] != null) {
                        remoteViews.setViewVisibility(R.id.affair, View.VISIBLE)
                    }
                    remoteViews.setTextViewText(R.id.top, lesson.course)
                    remoteViews.setTextViewText(R.id.bottom,
                        ClassRoomParse.parseClassRoom(lesson.classroom))
                    remoteViews.setOnClickFillInIntent(R.id.background,
                        Intent().putExtra("POSITION", position))
                    return remoteViews
                }
            }
            return remoteViews
        }

        /**生成显示星期几的rv*/
        private fun getWeekDayRv(position: Int): RemoteViews {
            val time = intent.getIntExtra("time", 0)
            //获取当前item是否应该高亮
            val id = if (time == position - 1)
                R.layout.widget_grid_view_day_of_week_selected_item
            else R.layout.widget_grid_view_day_of_week_item
            //返回对应的item
            return RemoteViews(mContext.packageName, id).apply {
                setTextViewText(R.id.tv_day_of_week, "周${
                    if (position != 6) Num2CN.number2ChineseNumber((position + 1).toLong()) else "日"
                }")
            }
        }

        /**生成课表显示第几节课以及中午、下午的rv*/
        private fun getLessonNumRv(position: Int): RemoteViews {
            return if (position == 0) //显示月份
                RemoteViews(mContext.packageName,
                    R.layout.widget_grid_view_month).apply {
                    setTextViewText(R.id.tv_month,
                        Calendar.getInstance()[Calendar.MONTH].toString() + "月")
                }
            else if (position == 8 || position == 16) RemoteViews(mContext.packageName,
                R.layout.widget_grid_view_item_lesson_num_afternoon).apply {
                setTextViewText(R.id.lesson_num_top, (position / 8 * 2 + 1).toString())
                setTextViewText(R.id.lesson_num_bottom, (position / 8 * 2 + 2).toString())
            }
            else if (position == 24) //显示中午
                RemoteViews(mContext.packageName,
                    R.layout.widget_grid_view_item_lesson_num_afternoon)
            else if (position == 32 || position == 40)
                RemoteViews(mContext.packageName,
                    R.layout.widget_grid_view_item_lesson_num_afternoon).apply {
                    setTextViewText(R.id.lesson_num_top, ((position - 8) / 8 * 2 + 1).toString())
                    setTextViewText(R.id.lesson_num_bottom, ((position - 8) / 8 * 2 + 2).toString())
                }
            else if (position == 48) //显示晚上
                RemoteViews(mContext.packageName,
                    R.layout.widget_grid_view_item_lesson_num_night)
            else RemoteViews(mContext.packageName,
                R.layout.widget_grid_view_item_lesson_num_night).apply {
                setTextViewText(R.id.lesson_num_top, ((position - 16) / 8 * 2 + 1).toString())
                setTextViewText(R.id.lesson_num_bottom, ((position - 16) / 8 * 2 + 2).toString())
            }
        }

        /**生成同学课表对应的RemoteView
         * */
        private fun getOtherLessonRV(mPosition: Int, position: Int): RemoteViews {
            val remoteViews =
                RemoteViews(mContext.packageName, R.layout.widget_grid_view_item_other_course)
            val lesson = otherLessons[mPosition / 7][mPosition % 7]
            /**如果有事务，则在上面添加小红点*/
            remoteViews.setViewVisibility(R.id.affair, View.GONE)
            if (affairs[mPosition / 7][mPosition % 7] != null) {
                remoteViews.setViewVisibility(R.id.affair, View.VISIBLE)
            }
            remoteViews.setTextViewText(R.id.top, lesson!!.course)
            remoteViews.setTextViewText(R.id.bottom,
                ClassRoomParse.parseClassRoom(lesson.classroom))
            remoteViews.setOnClickFillInIntent(R.id.background,
                Intent().putExtra("POSITION", position))
            return remoteViews
        }

        override fun onCreate() {
            /**初始化GridView的数据,设计数据库存储，所以开了个线程*/
            thread { mScheduleLessons = makeSchedules(mContext) }
        }

        override fun getCount(): Int { // 返回“集合视图”中的数据的总数
            return 8 * 9
        }

        override fun getItemId(position: Int): Long { // 返回当前项在“集合视图”中的位置
            return position.toLong()
        }

        override fun getLoadingView() =
            RemoteViews(mContext.packageName, R.layout.widget_grid_view_item_blank)

        override fun getViewTypeCount() = 11

        override fun hasStableIds() = true

        override fun onDataSetChanged() {}
        override fun onDestroy() {}
    }
}