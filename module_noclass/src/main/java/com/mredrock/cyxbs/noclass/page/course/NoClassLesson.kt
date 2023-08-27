package com.mredrock.cyxbs.noclass.page.course

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
  private val mGatheringList : List<String>,
  private val mNoGatheringList: List<String>,
  private val mLastingTime : Pair<Int,Int>
) : ILessonItem{
  
  override fun initializeView(context: Context): View {
    return NoClassLesson.newInstance(context,data ,mGatheringList,mNoGatheringList,mLastingTime)
  }
  
  override val lp: BaseLessonLayoutParams
    get() = NoClassLessonLayoutParams(data).apply {
      leftMargin = 1.2F.dp2px
      rightMargin = 1.2F.dp2px
      topMargin = 1.2F.dp2px
      bottomMargin = 1.2F.dp2px
    }
  
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
      fun newInstance(context: Context, data: NoClassLessonData, mGatheringList : List<String>, mNoGatheringList: List<String>,mLastingTime : Pair<Int,Int>): NoClassLesson {
        return NoClassLesson(context).apply {
          setColor(data.timeType)
          setText(names = data.names,height = data.length)
          setOnClickListener {
            //所有学生列表
            //没课的在前面
            val stuList = arrayListOf<Pair<String,Boolean>>()
            mGatheringList.forEach {
              stuList.add(Pair(it, true))
            }
            mNoGatheringList.forEach {
              stuList.add(Pair(it,false))
            }
            val duration = mLastingTime.second - mLastingTime.first
            //开始与结束序列
            val begin = mLastingTime.first
            val end = mLastingTime.second - 1
            
            val beginTime = com.mredrock.cyxbs.api.course.utils.getShowStartTimeStr(begin)
            val endTime = com.mredrock.cyxbs.api.course.utils.getShowEndTimeStr(end)
            
            val beginLesson =  if(mLastingTime.first >= 10) mLastingTime.first - 1 else if (mLastingTime.first<=3) mLastingTime.first + 1 else mLastingTime.first

            val month = when(data.weekNum){
               1 -> " 周日"
               2 -> " 周一"
               3 -> " 周二"
               4 -> " 周三"
               5 -> " 周四"
               6 -> " 周五"
               7 -> " 周六"
              else -> ""
            }
            val textTime = "时间：${month} ${beginLesson}-${beginLesson + duration - 1} ${beginTime}-${endTime}"
            NoClassGatherDialog(stuList, textTime).show((context as AppCompatActivity).supportFragmentManager, "NoClassGatherDialog")
          }
        }
      }
    }
  

    private val mAmBgColor = R.color.noclass_course_am_lesson_color.color
    private val mPmBgColor = R.color.noclass_course_pm_lesson_color.color
    private val mNightBgColor = R.color.noclass_course_night_lesson_color.color
    private val mNoonBgColor = R.color.noclass_course_noon_lesson_color.color
    private val mDuskBgColor = R.color.noclass_course_dusk_lesson_color.color
    
    private val mAmTextColor = R.color.noclass_course_am_text_color.color
    private val mPmTextColor = R.color.noclass_course_pm_text_color.color
    private val mNightTextColor = R.color.noclass_course_night_text_color.color
    private val mNoonTextColor = R.color.noclass_course_noon_text_color.color
    private val mDuskTextColor = R.color.noclass_course_dusk_text_color.color
  
    private fun setColor(type: NoClassLessonData.Type) {
      when (type) {
        NoClassLessonData.Type.AM -> {
          mTvNames.setTextColor(mAmTextColor)
          setCardBackgroundColor(mAmBgColor)
          setOverlapTagColor(mAmTextColor)
        }
        NoClassLessonData.Type.NOON -> {
          mTvNames.setTextColor(mNoonTextColor)
          setCardBackgroundColor(mNoonBgColor)
          setOverlapTagColor(mNoonTextColor)
        }
        NoClassLessonData.Type.PM -> {
          mTvNames.setTextColor(mPmTextColor)
          setCardBackgroundColor(mPmBgColor)
          setOverlapTagColor(mPmTextColor)
        }
        NoClassLessonData.Type.DUSK -> {
          mTvNames.setTextColor(mDuskTextColor)
          setCardBackgroundColor(mDuskBgColor)
          setOverlapTagColor(mDuskTextColor)
        }
        NoClassLessonData.Type.NIGHT -> {
          mTvNames.setTextColor(mNightTextColor)
          setCardBackgroundColor(mNightBgColor)
          setOverlapTagColor(mNightTextColor)
        }
      }
    }
  }
  
}