package com.mredrock.cyxbs.noclass.page.course

import android.content.Context
import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.item.IOverlapItem
import com.mredrock.cyxbs.lib.course.item.AbstractLesson
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.noclass.R

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
          setColor(data.timeType)
          setText(names = data.names,height = data.length)
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