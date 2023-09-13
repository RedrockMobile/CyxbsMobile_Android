package com.mredrock.cyxbs.noclass.page.course

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mredrock.cyxbs.api.affair.DateJson
import com.mredrock.cyxbs.api.course.utils.getBeginLesson
import com.mredrock.cyxbs.lib.course.item.lesson.BaseLessonLayoutParams
import com.mredrock.cyxbs.lib.course.item.lesson.ILessonItem
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.page.ui.dialog.NoClassGatherDialog

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.item
 * @ClassName:      NoClassLesson
 * @Author:         Yan
 * @CreateDate:     2022年09月06日 17:23:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */
class NoClassLesson(
  val data: NoClassLessonData,
  private val mNumNameIsSpare : HashMap<Pair<String,String>,Boolean>,
  private val mLastingTime : Pair<Int,Int>,
  private val mWeek : Int  //第几周
) : ILessonItem{

  override fun initializeView(context: Context): View {   //添加item到课表中
    return NoClassLesson.newInstance(context,data ,mNumNameIsSpare ,mLastingTime,mWeek)
  }

  override val lp: BaseLessonLayoutParams
    get() = NoClassLessonLayoutParams(data).apply {
      leftMargin = 1.2F.dp2px
      rightMargin = 1.2F.dp2px
      topMargin = 1.2F.dp2px
      bottomMargin = 1.2F.dp2px
    }
  // 星期几
  override val weekNum: Int
    get() = data.weekNum
  override val startNode: Int
    get() = data.startNode
  override val length: Int
    get() = data.length

  private class NoClassLesson private constructor(
    context: Context
  ) : NoClassItemView(context){
    companion object {
      //mNumNameIsSpare   <<id,name>,isSpare>
      fun newInstance(context: Context, data: NoClassLessonData,mNumNameIsSpare : HashMap<Pair<String,String>,Boolean> ,mLastingTime : Pair<Int,Int>,mWeek: Int): NoClassLesson {
        return NoClassLesson(context).apply {
          val sparePeopleNameList = mNumNameIsSpare.filter { it.value }.keys.map { it.second }
          val noSparePeopleNameList = mNumNameIsSpare.filter { !it.value }.keys.map { it.second }
          val busyMode = if (noSparePeopleNameList.isEmpty()){
            // 如果没有忙碌的人，那么NAN
            BusyMode.NAN
          }else if (sparePeopleNameList.isEmpty()){
            BusyMode.ALL
          }else if (sparePeopleNameList.size > noSparePeopleNameList.size){
            BusyMode.LESS
          }else{
            BusyMode.MORE
          }
          setColor(busyMode)
          setText(names = data.names,height = data.length)
          setOnClickListener {
            val duration = mLastingTime.second - mLastingTime.first
            //开始与结束序列
            val begin = mLastingTime.first
            val end = mLastingTime.second - 1

            val beginTime = com.mredrock.cyxbs.api.course.utils.getShowStartTimeStr(begin)
            val endTime = com.mredrock.cyxbs.api.course.utils.getShowEndTimeStr(end)

            val beginLesson =  if(mLastingTime.first >= 10) mLastingTime.first - 1 else if (mLastingTime.first<=3) mLastingTime.first + 1 else mLastingTime.first

            val month = when(data.weekNum){
               1 -> "周一"
               2 -> "周二"
               3 -> "周三"
               4 -> "周四"
               5 -> "周五"
               6 -> "周六"
               7 -> "周日"
              else -> ""
            }
            // 中午吃饭时间是-1 下午吃饭时间是-2，所以要传入特殊的beginLesson
            val specialBeginLesson = getBeginLesson(begin)

            val timeText =when (specialBeginLesson){
              -1 -> "中午"
              -2 -> "傍晚"
              else -> "${beginLesson}-${beginLesson + duration - 1}"
            }

            val textTime = "${month} $timeText ${beginTime}-${endTime}"
            val dateJson = DateJson(specialBeginLesson,data.weekNum,duration,mWeek)
            NoClassGatherDialog(dateJson,mNumNameIsSpare, textTime).show((context as AppCompatActivity).supportFragmentManager, "NoClassGatherDialog")
          }
        }
      }
    }


    private val mAllSpare = R.color.noclass_course_all_spare_lesson_color.color
    private val mAllBusyBg = R.color.noclass_course_all_busy_lesson_bg.color
    private val mAllBusyTextColor = R.color.noclass_course_all_busy_lesson_color.color
    private val mMoreSpareBg = R.color.noclass_course_more_spare_lesson_bg.color
    private val mMoreSpareTextColor = R.color.noclass_course_more_spare_lesson_color.color
    private val mMoreBusyBg = R.color.noclass_course_more_busy_lesson_bg.color
    private val mMoreBusyTextColor = R.color.noclass_course_more_busy_lesson_color.color

    private fun setColor(busyMode : BusyMode) {
      when (busyMode) {
        BusyMode.NAN -> {
          mTvNames.setTextColor(mAllSpare)
          setCardBackgroundColor(mAllSpare)
          setOverlapTagColor(mAllSpare)
        }
        BusyMode.LESS -> {
          mTvNames.setTextColor(mMoreSpareTextColor)
          setCardBackgroundColor(mMoreSpareBg)
          setOverlapTagColor(mMoreSpareTextColor)
        }
        BusyMode.MORE -> {
          mTvNames.setTextColor(mMoreBusyTextColor)
          setCardBackgroundColor(mMoreBusyBg)
          setOverlapTagColor(mMoreBusyTextColor)
        }
        BusyMode.ALL -> {
          mTvNames.setTextColor(mAllBusyTextColor)
          setCardBackgroundColor(mAllBusyBg)
          setOverlapTagColor(mAllBusyTextColor)
        }
      }
    }
    enum class BusyMode{
      NAN,LESS,MORE,ALL
    }
  }

}