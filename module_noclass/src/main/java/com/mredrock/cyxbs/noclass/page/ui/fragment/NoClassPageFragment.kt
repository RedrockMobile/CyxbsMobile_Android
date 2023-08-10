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
  
  private lateinit var mNameMap : HashMap<String,String>   //学号和姓名的映射表
  private lateinit var mNoClassSpareTime : NoClassSpareTime
  
  /**
   * 添加课程
   */
  protected fun addLessons(noClassSpareTime: NoClassSpareTime){
    mNoClassSpareTime = noClassSpareTime
    mNameMap = mNoClassSpareTime.mIdToNameMap
    (0..6).map {
      val lineSpareTime = noClassSpareTime.spareDayTime[it]!!
      addLessonsByLine(lineSpareTime, it)
    }
  }
  
  /**
   * 添加每一竖行的课程
   *
   * @param lineTime 这一竖行的data
   * @param line 第几列
   */
  private fun addLessonsByLine(lineTime: NoClassSpareTime.SpareLineTime, line: Int){
    addLessonAm(lineTime,line)
    addLessonNoon(line)
    addLessonPm(lineTime,line)
    addLessonDusk(line)
    addLessonNight(lineTime,line)
  }
  
  private fun addLessonAm(lineTime: NoClassSpareTime.SpareLineTime,line : Int) {
    val week = line + 1
    val id1 = lineTime.SpareItem[1].spareId   //每一个格子空闲的所有学号
    val id2 = lineTime.SpareItem[2].spareId
    val id3 = lineTime.SpareItem[3].spareId
    val id4 = lineTime.SpareItem[4].spareId
    addLessonByJudge(0,week,id1, id2)
    addLessonByJudge(2,week,id3, id4)
  }
  private fun addLessonNoon(line: Int){
    addLesson(getFullStuLesson(4,line+1))
  }
  private fun addLessonPm(lineTime: NoClassSpareTime.SpareLineTime, line: Int){
    val week = line + 1
    val id1 = lineTime.SpareItem[5].spareId
    val id2 = lineTime.SpareItem[6].spareId
    val id3 = lineTime.SpareItem[7].spareId
    val id4 = lineTime.SpareItem[8].spareId
    addLessonByJudge(5,week,id1, id2)
    addLessonByJudge(7,week,id3, id4)
  }
  private fun addLessonDusk(line: Int){
    addLesson(getFullStuLesson(9,line+1))
  }
  private fun addLessonNight(lineTime: NoClassSpareTime.SpareLineTime, line: Int){
    val week = line + 1
    val id1 = lineTime.SpareItem[9].spareId
    val id2 = lineTime.SpareItem[10].spareId
    val id3 = lineTime.SpareItem[11].spareId
    val id4 = lineTime.SpareItem[12].spareId
    addLessonByJudge(10,week,id1, id2)
    addLessonByJudge(12,week,id3, id4)
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
  ){
    if (id1 == id2) {
      addLesson(getLesson(begin,week,2,id1))
    } else if (id1 != id2) {
      addLesson(getLesson(begin,week,1,id1))
      addLesson(getLesson(begin+1,week,1,id2))
    }
  }
  
  /**
   * 获得满员的课
   */
  private fun getFullStuLesson(
    begin: Int,
    week: Int,
  ) : NoClassLesson{
    val list = mNameMap.map{
      it.key
    }
    return getLesson(begin,week,1,list)
  }
  
  private fun getLesson(
    begin: Int,
    week: Int,
    length : Int,
    gatheringIdList: List<String>,  //空闲的id
  ) : NoClassLesson{
    val noGatheringIdList : List<String> = mNameMap.map { it.key }.toMutableList().apply {  //忙碌的id
      gatheringIdList.forEach {
        this.remove(it)
      }
    }
    val gatheringList : List<String> = gatheringIdList.map { mNameMap[it]!! }   //空闲的人名
    val noGatheringList : List<String> = noGatheringIdList.map { mNameMap[it]!! }   //忙碌的人名
    return NoClassLesson(NoClassLessonData(week, begin, length, gatheringIdList.showText()),gatheringList,noGatheringList,Pair(begin,begin+length))
  }

  /**
   * 根据忙碌人数展示忙碌*人
   */
  private fun List<String>.showText(): String {
    if (this.size == mNameMap.size)  //如果空闲人id的集合为所有，那么就不显示忙碌
      return ""
    //改成几人忙碌
    return "${mNameMap.size - this.size}人\n忙碌"
  }
  
  
}