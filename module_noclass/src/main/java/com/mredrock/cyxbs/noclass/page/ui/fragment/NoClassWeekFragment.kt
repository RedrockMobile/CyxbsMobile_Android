package com.mredrock.cyxbs.noclass.page.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import com.mredrock.cyxbs.lib.course.helper.CourseNowTimeHelper
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil
import com.mredrock.cyxbs.noclass.bean.NoClassSpareTime
import com.mredrock.cyxbs.noclass.page.course.NoClassLesson
import com.mredrock.cyxbs.noclass.page.course.NoClassLessonData
import java.util.*
import kotlin.collections.HashMap

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui.fragment
 * @ClassName:      NoClassWeekFragment
 * @Author:         Yan
 * @CreateDate:     2022年09月06日 10:26:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */
class NoClassWeekFragment : CoursePageFragment(){
  
  
  companion object {
    fun newInstance(week: Int,data:NoClassSpareTime): NoClassWeekFragment {
      return NoClassWeekFragment().apply {
        arguments = bundleOf(
          this::mNoClassSpareTime.name to data,
          this::mWeek.name to week
        )
      }
    }
  }
  private lateinit var mNameMap : HashMap<String,String>
  private val mNoClassSpareTime by arguments<NoClassSpareTime>()
  private val mWeek by arguments<Int>()
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setWeekNum()
    addLessons(mNoClassSpareTime)
  }
  
  /**
   * 添加课程
   */
  private fun addLessons(noClassSpareTime: NoClassSpareTime){
    mNameMap = mNoClassSpareTime.mIdToNameMap
    (0..6).map {
      val lineSpareTime = noClassSpareTime.spareDayTime[it]
      addLessonsByLine(lineSpareTime, it)
    }
  }
  
  /**
   * 添加每一竖行的课程
   *
   * @param lineTime 这一竖行的data
   * @param line 第几行
   */
  private fun addLessonsByLine(lineTime: NoClassSpareTime.SpareLineTime, line: Int){
    addLessonAm(lineTime,line)
    addLessonNoon(lineTime,line)
    addLessonPm(lineTime,line)
    addLessonDusk(lineTime,line)
    addLessonNight(lineTime,line)
  }
  
  private fun addLessonAm(lineTime: NoClassSpareTime.SpareLineTime,line : Int) {
    val week = line + 1
    val id1 = lineTime.SpareId[1]
    val id2 = lineTime.SpareId[2]
    val id3 = lineTime.SpareId[3]
    val id4 = lineTime.SpareId[4]
    val spare1 = !lineTime.isSpare[1]
    val spare2 = !lineTime.isSpare[2]
    val spare3 = !lineTime.isSpare[3]
    val spare4 = !lineTime.isSpare[4]
    addLessonByJudge(0,week,id1, id2, spare1, spare2)
    addLessonByJudge(2,week,id3, id4, spare3, spare4)
  }
  private fun addLessonNoon(lineTime: NoClassSpareTime.SpareLineTime,line : Int){

  }
  private fun addLessonPm(lineTime: NoClassSpareTime.SpareLineTime, line: Int){
    val week = line + 1
    val id1 = lineTime.SpareId[5]
    val id2 = lineTime.SpareId[6]
    val id3 = lineTime.SpareId[7]
    val id4 = lineTime.SpareId[8]
    val spare1 = !lineTime.isSpare[5]
    val spare2 = !lineTime.isSpare[6]
    val spare3 = !lineTime.isSpare[7]
    val spare4 = !lineTime.isSpare[8]
    addLessonByJudge(5,week,id1, id2, spare1, spare2)
    addLessonByJudge(7,week,id3, id4, spare3, spare4)
  }
  private fun addLessonDusk(lineTime: NoClassSpareTime.SpareLineTime, line: Int){
  
  }
  private fun addLessonNight(lineTime: NoClassSpareTime.SpareLineTime, line: Int){
    val week = line + 1
    val id1 = lineTime.SpareId[9]
    val id2 = lineTime.SpareId[10]
    val id3 = lineTime.SpareId[11]
    val id4 = lineTime.SpareId[12]
    val spare1 = !lineTime.isSpare[9]
    val spare2 = !lineTime.isSpare[10]
    val spare3 = !lineTime.isSpare[11]
    val spare4 = !lineTime.isSpare[12]
    addLessonByJudge(10,week,id1, id2, spare1, spare2)
    addLessonByJudge(12,week,id3, id4, spare3, spare4)
  }
  
  /**
   * 在两个区间中通过添加没课状态
   * 如某一行[0,1]中通过判断0的数据和1的数据
   * 进行添加没课约的课表
   */
  private fun addLessonByJudge(
    begin: Int,
    week: Int,
    id1: List<String>,
    id2: List<String>,
    spare1: Boolean,
    spare2: Boolean
  ){
    if (id1 == id2 && spare1 && spare2) {
      addLesson(NoClassLesson(NoClassLessonData(week, begin, 2, id1.map { mNameMap[it]!! }.toString().let { it.substring(1,it.length-1)})))
    } else if (id1 != id2 && spare1 && spare2) {
      addLesson(NoClassLesson(NoClassLessonData(week, begin, 1, id1.map { mNameMap[it]!! }.toString())))
      addLesson(NoClassLesson(NoClassLessonData(week, begin+1, 1, id2.map { mNameMap[it]!! }.toString())))
    } else {
      if (spare1) {
        addLesson(NoClassLesson(NoClassLessonData(week, begin, 1, id1.map { mNameMap[it]!! }.toString())))
      }
      if (spare2) {
        addLesson(NoClassLesson(NoClassLessonData(week, begin+1, 1, id2.map { mNameMap[it]!! }.toString())))
      }
    }
  }
  /**
   * 设置星期数
   */
  private fun setWeekNum() {
    val calendar = SchoolCalendarUtil.getFirstMonDayOfTerm()
    if (calendar != null) {
      calendar.add(Calendar.DATE, (mWeek - 1) * 7)
      val startTimestamp = calendar.timeInMillis
      setMonth(calendar)
      calendar.add(Calendar.DATE, 7)
      onIsInThisWeek(System.currentTimeMillis() in startTimestamp .. calendar.timeInMillis)
    } else {
      onIsInThisWeek(false)
    }
  }
  
  private val mCourseNowTimeHelper by lazy {
    CourseNowTimeHelper.attach(this)
  }
  
  /**
   * 今天是否在本周内的回调
   */
  private fun onIsInThisWeek(boolean: Boolean) {
    mCourseNowTimeHelper.setVisible(boolean)
    if (boolean) {
      val calendar = Calendar.getInstance()
      val weekNum = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1
      /*
      * 星期天：1 -> 7
      * 星期一：2 -> 1
      * 星期二：3 -> 2
      * 星期三：4 -> 3
      * 星期四：5 -> 4
      * 星期五：6 -> 5
      * 星期六：7 -> 6
      *
      * 左边一栏是 Calendar.get(Calendar.DAY_OF_WEEK) 得到的数字，
      * 右边一栏是 weekNum 对应的数字
      * */
      showToday(weekNum)
    }
  }
  
}