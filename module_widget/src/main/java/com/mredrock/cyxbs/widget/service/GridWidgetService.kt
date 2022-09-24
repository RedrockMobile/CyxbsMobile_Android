package com.mredrock.cyxbs.widget.service

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.mredrock.cyxbs.api.course.utils.parseClassRoom
import com.mredrock.cyxbs.widget.R
import com.mredrock.cyxbs.widget.repo.bean.AffairEntity
import com.mredrock.cyxbs.widget.repo.bean.LessonEntity
import com.mredrock.cyxbs.widget.repo.database.AffairDatabase
import com.mredrock.cyxbs.widget.util.*
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

        private fun makeSchedules(): Array<Array<LessonEntity?>> {
            val weekOfTerm = SchoolCalendar().weekOfTerm
            myLessons = getMyLessons(weekOfTerm)
            otherLessons = getOthersStuNum(weekOfTerm).schedule()
            affairs =
                AffairDatabase.INSTANCE.getAffairDao()
                    .queryAllAffair(weekOfTerm).schedule()
            return myLessons.schedule()
        }

        private fun List<LessonEntity>.schedule(): Array<Array<LessonEntity?>> {
            val mScheduleLessons = Array(8) { arrayOfNulls<LessonEntity>(8) }
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
                    val column = it.day + 1
                    //处理beginLesson特殊的情况
                    if (it.beginLesson % 2 == 0 && it.beginLesson != -2) {//从偶数节课开始
                        val evenAffair = it.copy(period = 1, beginLesson = it.beginLesson)
                        mScheduleAffairs[it.beginLesson / 2][column] = evenAffair
                    } else if (it.beginLesson == -1) {//中午
                        mScheduleAffairs[2][column] =
                            it.copy(period = 1, beginLesson = it.beginLesson)
                    } else if (it.beginLesson == -2) {//晚上
                        mScheduleAffairs[5][column] =
                            it.copy(period = 1, beginLesson = it.beginLesson)
                    }
                    addSpecialAffair(it, mScheduleAffairs)
                    addLongAffair(it.copy(period = it.period - 1,
                        beginLesson = it.beginLesson + 1), mScheduleAffairs)
                }
            }
            return mScheduleAffairs
        }

        //处理跨域中午，下午的事务
        private fun addSpecialAffair(
            affairEntity: AffairEntity,
            mScheduleAffairs: Array<Array<AffairEntity?>>,
        ) {
            if (affairEntity.beginLesson <= 4) {
                if (affairEntity.period + affairEntity.beginLesson > 5) {
                    mScheduleAffairs[2][affairEntity.day + 1] =
                        affairEntity.copy(period = 1, beginLesson = -1)
                } else if (affairEntity.period + affairEntity.beginLesson > 9) {
                    mScheduleAffairs[2][affairEntity.day + 1] =
                        affairEntity.copy(period = 5, beginLesson = -2)
                }
            } else if (affairEntity.beginLesson <= 8 && affairEntity.period + affairEntity.beginLesson > 9) {
                mScheduleAffairs[2][affairEntity.day + 1] =
                    affairEntity.copy(period = 5, beginLesson = -2)
            }
        }

        //递归处理period大的事务
        private fun addLongAffair(
            affairEntity: AffairEntity,
            mScheduleAffairs: Array<Array<AffairEntity?>>,
        ) {
            if (affairEntity.period > 1) {
                val evenAffair = affairEntity.copy(period = 2)
                mScheduleAffairs[affairEntity.beginLesson / 2][affairEntity.day + 1] = evenAffair
                addLongAffair(affairEntity.copy(period = affairEntity.period - 2,
                    beginLesson = affairEntity.beginLesson + 2), mScheduleAffairs)
            } else if (affairEntity.period == 1) {
                mScheduleAffairs[affairEntity.beginLesson / 2][affairEntity.day + 1] =
                    affairEntity.copy(period = 1, beginLesson = affairEntity.beginLesson + 1)
            }
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
                    .getInt(POSITION, -1)
            if (position == 0) //显示月份
                return RemoteViews(mContext.packageName,
                    R.layout.widget_grid_view_month).apply {
                    setTextViewText(R.id.tv_month,
                        SchoolCalendar().month.toString() + "月")
                }
            //如果等于0，说明是第1列的，该显示课程是第几节
            if (position % 8 == 0)
                return getLessonNumRv(position)
            //如果是第1到7个说明是显示今天是星期几
            if (position in 1..7) return getWeekDayRv(position)
            if (position in 24..31 || position in 48..55) return getHalfAffairRv(position)
            val remoteViews = mScheduleLessons?.let { mSchedulesArray ->
                val mPosition = position - 8
                val lesson = mSchedulesArray[mPosition / 8][mPosition % 8]
                if (lesson == null) {
                    if (otherLessons[mPosition / 8][mPosition % 8] != null)
                    /** 如果有同学的课，设置other_course的背景*/
                        getOtherLessonRV(mPosition, position)
                    else if (affairs[mPosition / 8][mPosition % 8] != null)
                    /** 如果是事务，设置事务的背景*/
                        getAffairRv(mPosition)
                    else
                    /** 无事务也无课*/
                    {
                        RemoteViews(mContext.packageName,
                            R.layout.widget_grid_view_item_blank)
                    }
                } else {
                    val remoteViews: RemoteViews =
                        when {
                            /**根据早上，下午，晚上的课设置不同背景*/
                            mPosition < 16 && lesson.period == 1 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_item_half_moring)
                            mPosition < 16 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_moring_item)
                            mPosition < 40 && lesson.period == 1 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_item_half_afternoon)
                            mPosition < 40 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_afternoon_item)
                            lesson.period == 1 -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_item_half_night)
                            else -> RemoteViews(mContext.packageName,
                                R.layout.widget_grid_view_night_item)
                        }
                    if (lesson.period == 1) decoratedSingleLessonWithAffair(remoteViews,
                        mPosition) else decoratedLessonWithAffair(remoteViews, mPosition)
                    decoratedLessonWithOtherLesson(remoteViews, mPosition)
                    remoteViews.setTextViewText(R.id.top, lesson.course)
                    remoteViews.setTextViewText(R.id.bottom,
                        ClassRoomParse.parseClassRoom(lesson.classroom))
                    remoteViews.setOnClickFillInIntent(R.id.background,
                        Intent().putExtra(POSITION, position))
                    return remoteViews
                }
            }
            return remoteViews
        }

        /**如果有单节课，且有事务，根据事务的beginLesson和在课程上面添加装饰*/
        private fun decoratedSingleLessonWithAffair(remoteViews: RemoteViews, mPosition: Int) {
            val affair = affairs[mPosition / 8][mPosition % 8]
            affair?.let {
                if (affair.period == 2) {
                    remoteViews.setViewVisibility(R.id.affair, View.VISIBLE)
                    remoteViews.setViewVisibility(R.id.half_affair_title, View.VISIBLE)
                    remoteViews.setTextViewText(R.id.half_affair_title, affair.title)
                } else if (affair.beginLesson % 2 == 0) {
                    remoteViews.setViewVisibility(R.id.half_affair_title, View.VISIBLE)
                    remoteViews.setTextViewText(R.id.half_affair_title, affair.title)
                } else if (affair.beginLesson % 2 == 1) {
                    remoteViews.setViewVisibility(R.id.affair, View.VISIBLE)
                }
            }
        }

        /**如果有关联他人的课表，则在课程上面添加装饰*/
        private fun decoratedLessonWithOtherLesson(remoteViews: RemoteViews, mPosition: Int) {
            if (otherLessons[mPosition / 8][mPosition % 8] != null) {
                remoteViews.setViewVisibility(R.id.other_lesson, View.VISIBLE)
            }
        }

        /**如果有事务，则在上面添加小红点*/
        private fun decoratedLessonWithAffair(remoteViews: RemoteViews, mPosition: Int) {
            if (affairs[mPosition / 8][mPosition % 8] != null) {
                remoteViews.setViewVisibility(R.id.affair, View.VISIBLE)
            }
        }

        /**生成事务rv*/
        private fun getAffairRv(mPosition: Int): RemoteViews {
            val affair = affairs[mPosition / 8][mPosition % 8]
            return if (affair!!.period == 1) RemoteViews(mContext.packageName,
                R.layout.widget_grid_view_item_half_affair).apply {
                setTextViewText(R.id.half_affair_title, affair.title)
            } else RemoteViews(mContext.packageName, R.layout.widget_grid_view_item_affair).apply {
                setTextViewText(R.id.top, affair.title)
                setTextViewText(R.id.bottom, parseClassRoom(affair.content))
            }
        }


        /**生成中午，下午的半长事务或者空白rv*/
        private fun getHalfAffairRv(position: Int) =
            if (affairs[(position - 8) / 8][(position - 8) % 8] != null) RemoteViews(mContext.packageName,
                R.layout.widget_grid_view_item_half_affair).apply {
                affairs[(position - 8) / 8][(position - 8) % 8]?.let {
                    setTextViewText(R.id.top, it.title)
                }
            } else {
                RemoteViews(mContext.packageName,
                    R.layout.widget_grid_view_item_half_blank)
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
                    if (position != 7) Num2CN.number2ChineseNumber((position).toLong()) else "日"
                }")
            }
        }

        /**生成课表显示第几节课以及中午、下午的rv*/
        private fun getLessonNumRv(position: Int): RemoteViews {
            return if (position == 8 || position == 16) RemoteViews(mContext.packageName,
                R.layout.widget_grid_view_item_lesson_num).apply {
                setTextViewText(R.id.lesson_num_top, (position / 8 * 2 - 1).toString())
                setTextViewText(R.id.lesson_num_bottom, (position / 8 * 2).toString())
            }
            else if (position == 24) //显示中午
                RemoteViews(mContext.packageName,
                    R.layout.widget_grid_view_item_lesson_num_afternoon)
            else if (position == 32 || position == 40)
                RemoteViews(mContext.packageName,
                    R.layout.widget_grid_view_item_lesson_num).apply {
                    setTextViewText(R.id.lesson_num_top,
                        ((position - 8) / 8 * 2 - 1).toString())
                    setTextViewText(R.id.lesson_num_bottom, ((position - 8) / 8 * 2).toString())
                }
            else if (position == 48) //显示晚上
                RemoteViews(mContext.packageName,
                    R.layout.widget_grid_view_item_lesson_num_night)
            else RemoteViews(mContext.packageName,
                R.layout.widget_grid_view_item_lesson_num).apply {
                setTextViewText(R.id.lesson_num_top, ((position - 16) / 8 * 2 - 1).toString())
                setTextViewText(R.id.lesson_num_bottom, ((position - 16) / 8 * 2).toString())
            }
        }

        /**生成同学课表对应的RemoteView
         * */
        private fun getOtherLessonRV(mPosition: Int, position: Int): RemoteViews {
            val remoteViews =
                RemoteViews(mContext.packageName, R.layout.widget_grid_view_item_other_course)
            val lesson = otherLessons[mPosition / 8][mPosition % 8]
            remoteViews.setTextViewText(R.id.top, lesson!!.course)
            remoteViews.setTextViewText(R.id.bottom,
                ClassRoomParse.parseClassRoom(lesson.classroom))
            decoratedLessonWithAffair(remoteViews, mPosition)
            remoteViews.setOnClickFillInIntent(R.id.background,
                Intent().putExtra(POSITION, position))
            return remoteViews
        }

        override fun onCreate() {
            /**初始化GridView的数据,设计数据库存储，所以开了个线程*/
            thread { mScheduleLessons = makeSchedules() }
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

        override fun onDataSetChanged() {
            thread { mScheduleLessons = makeSchedules() }
        }

        override fun onDestroy() {}
    }
}