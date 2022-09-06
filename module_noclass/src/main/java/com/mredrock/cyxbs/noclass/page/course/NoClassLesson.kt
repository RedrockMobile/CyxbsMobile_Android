package com.mredrock.cyxbs.noclass.page.course

import android.content.Context
import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.item.IOverlapItem
import com.mredrock.cyxbs.lib.course.item.AbstractLesson

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
class NoClassLesson(val data: NoClassLessonData) : AbstractLesson(data){
  override val lp: NoClassLessonLayoutParams
    get() = NoClassLessonLayoutParams(data)
  
  override fun createView(context: Context, parentStartRow: Int, parentEndRow: Int): View {
    return NoClassLesson.newInstance(context,data)
  }
  
  override fun compareTo(other: IOverlapItem): Int {
    return 0
  }
  
  private class NoClassLesson private constructor(
    context: Context
  ) : NoClassItemView(context){
    companion object {
      fun newInstance(context: Context, data: NoClassLessonData): NoClassLesson {
        return NoClassLesson(context).apply {
          NoClassLesson(data)
        }
      }
    }
  
//    private val mAmTextColor = R.color.course_am_lesson_tv.color
//    private val mPmTextColor = R.color.course_pm_lesson_tv.color
//    private val mNightTextColor = R.color.course_night_lesson_tv.color
//    private val mAmBgColor = R.color.course_am_lesson_bg.color
//    private val mPmBgColor = R.color.course_pm_lesson_bg.color
//    private val mNightBgColor = R.color.course_night_lesson_bg.color
  
    fun setLessonData(data: NoClassLessonData) {
//      setColor(data.timeType)
//      setText(data.course, data.classroom)
    }
  
    private fun setColor(type: NoClassLessonData.Type) {
//      when (type) {
//        LessonData.Type.AM -> {
//          mTvTitle.setTextColor(mAmTextColor)
//          mTvContent.setTextColor(mAmTextColor)
//          setCardBackgroundColor(mAmBgColor)
//          setOverlapTagColor(mAmTextColor)
//        }
//        LessonData.Type.PM -> {
//          mTvTitle.setTextColor(mPmTextColor)
//          mTvContent.setTextColor(mPmTextColor)
//          setCardBackgroundColor(mPmBgColor)
//          setOverlapTagColor(mPmTextColor)
//        }
//        LessonData.Type.NIGHT -> {
//          mTvTitle.setTextColor(mNightTextColor)
//          mTvContent.setTextColor(mNightTextColor)
//          setCardBackgroundColor(mNightBgColor)
//          setOverlapTagColor(mNightTextColor)
//        }
//      }
    }
  }
  
}