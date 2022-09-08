package com.mredrock.cyxbs.noclass.page.ui.fragment

import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import com.mredrock.cyxbs.noclass.bean.NoClassSpareTime
import com.mredrock.cyxbs.noclass.page.course.NoClassLesson
import com.mredrock.cyxbs.noclass.page.course.NoClassLessonData

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui.fragment
 * @ClassName:      NoClassPageFragment
 * @Author:         Yan
 * @CreateDate:     2022年09月08日 21:19:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */
abstract class NoClassPageFragment: CoursePageFragment() {
  
  private lateinit var mNameMap : HashMap<String,String>
  private lateinit var mNoClassSpareTime : NoClassSpareTime
  
  /**
   * 添加课程
   */
  protected fun addLessons(noClassSpareTime: NoClassSpareTime){
    mNoClassSpareTime = noClassSpareTime
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
      addLesson(NoClassLesson(NoClassLessonData(week, begin, 2, id1.idListToNames())))
    } else if (id1 != id2 && spare1 && spare2) {
      addLesson(NoClassLesson(NoClassLessonData(week, begin, 1, id1.idListToNames())))
      addLesson(NoClassLesson(NoClassLessonData(week, begin+1, 1, id2.idListToNames())))
    } else {
      if (spare1) {
        addLesson(NoClassLesson(NoClassLessonData(week, begin, 1, id1.idListToNames())))
      }
      if (spare2) {
        addLesson(NoClassLesson(NoClassLessonData(week, begin+1, 1, id2.idListToNames())))
      }
    }
  }
  
  /**
   * 将id的list转化为课表上的文字
   */
  private fun List<String>.idListToNames() : String{
    if (this.size == mNameMap.size)
      return "全体\n成员"
    val nameList = this.map { mNameMap[it]!! }.toString().let { it.substring(1, it.length-1) }
    var names = ""
    nameList.split(",").forEachIndexed { index, s ->
      names += s
      if(index != size-1){
        names += "\n"
      }
    }
    return names
  }
  
  
}