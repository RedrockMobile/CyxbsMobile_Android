package com.mredrock.cyxbs.noclass.page.ui.fragment


import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import com.mredrock.cyxbs.lib.course.helper.affair.CreateAffairDispatcher
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ICreateAffairConfig
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchAffairItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.noclass.bean.NoClassSpareTime
import com.mredrock.cyxbs.noclass.page.course.NoClassLesson
import com.mredrock.cyxbs.noclass.page.course.NoClassLessonData
import com.mredrock.cyxbs.noclass.page.course.NoClassTouchAffairItem
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

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
  // 第几个星期，默认为0，也就是整个学期的课表
  protected open val mWeek : Int = 0

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
    addLessonPm(lineTime,line)
    addLessonNight(lineTime,line)
  }

  private fun addLessonAm(lineTime: NoClassSpareTime.SpareLineTime,line : Int) {
    val week = line + 1  //周几
    val id1 = lineTime.SpareItem[1].spareId   //每一个格子空闲的所有学号
    val id2 = lineTime.SpareItem[2].spareId
    val id3 = lineTime.SpareItem[3].spareId
    val id4 = lineTime.SpareItem[4].spareId
    addLessonByJudge(0,week,id1, id2)
    addLessonByJudge(2,week,id3, id4)
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
    val isId1Busy = id1.size != mNameMap.size
    val isId2Busy = id2.size != mNameMap.size

    if (id1 == id2) {
      if (isId1Busy) {
        addLesson(getLesson(begin, week, 2, id1))
      }
    } else {
      if (isId1Busy) {
        addLesson(getLesson(begin, week, 1, id1))
      }
      if (isId2Busy) {
        addLesson(getLesson(begin + 1, week, 1, id2))
      }
    }
  }

  private fun getLesson(
    begin: Int,
    week: Int,  //周几
    length : Int,
    gatheringIdList: List<String>,  //空闲的id
  ) : NoClassLesson{
    // 学号，姓名，是否空闲，空闲为true
    val stuMap = HashMap<Pair<String,String>,Boolean>()
    mNameMap.keys.forEach {
      stuMap[it to (mNameMap[it] ?: "无名")] = it in gatheringIdList
    }
    return NoClassLesson(NoClassLessonData(week, begin, length, gatheringIdList.showText()),stuMap,Pair(begin,begin+length),mWeek)
  }

  /**
   * 根据忙碌人数展示忙碌*人
   */
  private fun List<String>.showText(): String {
    if (this.size == mNameMap.size)  //如果空闲人id的集合为所有，那么就不显示忙碌
      return ""
    //改成几人忙碌
    if (this.isEmpty()){
      return "全员\n忙碌"
    }
    return "${mNameMap.size - this.size}人\n忙碌"
  }

  /**
   * 点击空白处
   */
  protected fun initCreateAffair() {
    val dispatcher = CreateAffairDispatcher(
      this,
      object : ICreateAffairConfig {
        override fun createTouchAffairItem(
          course: ICourseViewGroup,
          event: IPointerEvent
        ): ITouchAffairItem {
          return NoClassTouchAffairItem(course.getContext(),mNameMap,mWeek) // 支持长按移动 item 的 ITouchAffairItem
        }
      }
    )
    course.addPointerDispatcher(dispatcher)
  }


}