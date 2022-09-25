package com.mredrock.cyxbs.noclass.bean

import com.mredrock.cyxbs.api.course.ICourseService
import com.mredrock.cyxbs.api.course.ILessonService
import java.io.Serializable

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.bean
 * @ClassName:      NoClassSpareTime
 * @Author:         Yan
 * @CreateDate:     2022年09月06日 23:43:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    用于记录所有人的没课情况
 */

data class NoClassSpareTime (
    //记录一天的空闲时间
    val spareDayTime : HashMap<Int,SpareLineTime>
) : Serializable {
  
  companion object{
    val EMPTY_PAGE = HashMap<Int, NoClassSpareTime>().apply {
      (0 .. ICourseService.maxWeek).forEach{
        this[it] = getNewEmptySpareTime()
      }
    }
  }
  
  //一个学生的map 用于学号与姓名之间的转换
  var mIdToNameMap : HashMap<String,String> = hashMapOf()
  
  //记录从上当下一行的空闲时间
  data class SpareLineTime(
    //此时有空闲时间的学号
    val SpareItem: ArrayList<SpareIds>,
    //此时是否空闲 false代表没课
    val isSpare: HashMap<Int,Boolean>,
  ): Serializable {
    //每格格子上空闲的人数
    data class SpareIds(
      val spareId : ArrayList<String>
    ) : Serializable
  }
}

  /**
   * 将返回出来的数据变为SpareTime格式
   */
fun Map<Int, List<ILessonService.Lesson>>.toSpareTime() : HashMap<Int, NoClassSpareTime>{
  val studentSpareTimes: HashMap<Int, NoClassSpareTime> = HashMap()
  //所有学生的id
  val stuIds = arrayListOf<String>()
  this.forEach {
    stuIds.add(it.value[it.key].stuNum)
  }
  studentSpareTimes[0] = getNewSpareTime(stuIds)
  //整个学期的没课约
  val semesterStu = studentSpareTimes[0]!!
  //学生为划分
  this.forEach { entry ->
    //每一节课为划分
    entry.value.forEach { lesson ->
      //得到当前lesson页面的SpareTime
      if (studentSpareTimes[lesson.week] == null) {
        studentSpareTimes[lesson.week] = getNewSpareTime(stuIds)
      }
      //当前页面的
      val stu: NoClassSpareTime = studentSpareTimes[lesson.week]!!
      //当前页面第几竖列
      val line = stu.spareDayTime[lesson.hashDay]
      //整个学期页面的第几行
      val semesterLine = semesterStu.spareDayTime[lesson.hashDay]
      (0 until lesson.period).map {
        //得到了当前一整竖行的数据
        //检查移除该格子的空闲人
        if (line!!.SpareItem[lesson.beginLesson + it].spareId.contains(lesson.stuNum)) {
          line.SpareItem[lesson.beginLesson + it].spareId.remove(lesson.stuNum)
          semesterLine!!.SpareItem[lesson.beginLesson + it].spareId.remove(lesson.stuNum)
        }
        //如果这个格子上已经没人了，就标记为有课
        if(line.SpareItem[lesson.beginLesson + it].spareId.isEmpty())
        line.isSpare[lesson.beginLesson + it] = true
        //整个学期的有没课状态
        if(semesterLine!!.SpareItem[lesson.beginLesson + it].spareId.isEmpty())
          semesterLine.isSpare[lesson.beginLesson + it] = true
      }
    }
  }
    //检查全部包含进去
    if (studentSpareTimes.size <= ICourseService.maxWeek){
      (0 .. ICourseService.maxWeek).map{
        if (studentSpareTimes[it] == null){
          studentSpareTimes[it] = getNewSpareTime(stuIds)
        }
      }
    }

    return studentSpareTimes
}

/**
 * 一个新的SpareTime对象
 */
private fun getNewSpareTime(stuIds : ArrayList<String>): NoClassSpareTime {
  return NoClassSpareTime(hashMapOf()).apply {
    // 0 .. 6 指星期数
    (0..6).map {
      spareDayTime[it] =
        NoClassSpareTime.SpareLineTime(
          ArrayList<NoClassSpareTime.SpareLineTime.SpareIds>(13).apply {
            // 13 是总的行数，为上午 4 行，中午 1 行，下午 4 行，傍晚 1 行，晚上 4 行
            (0..13).forEach { _ ->
              add(NoClassSpareTime.SpareLineTime.SpareIds((stuIds.clone() as ArrayList<String>)))
            }
          },
          hashMapOf<Int, Boolean>().apply {
            (0..13).forEach{ index ->
              this[index] = false
            }
          }
        )
    }
  }
}


private fun getNewEmptySpareTime(): NoClassSpareTime {
  return NoClassSpareTime(hashMapOf()).apply {
    (0..6).map {
      spareDayTime[it] =
        NoClassSpareTime.SpareLineTime(
          ArrayList<NoClassSpareTime.SpareLineTime.SpareIds>(13).apply {
            (0..13).forEach { _ ->
              add(NoClassSpareTime.SpareLineTime.SpareIds(arrayListOf()))
            }
          },
          hashMapOf<Int, Boolean>().apply {
            (0..13).forEach{ index ->
              this[index] = false
            }
          }
        )
    }
  }
}

