package com.mredrock.cyxbs.noclass.bean

import android.util.SparseArray
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
    val spareDayTime : SparseArray<SpareLineTime>
) : Serializable {
  //一个学生的map 用于学号与姓名之间的转换
  val mIdToNameMap : HashMap<String,String> = hashMapOf()
  
  //记录从上当下一行的空闲时间
  data class SpareLineTime(
    //此时有空闲时间的学号
    val SpareId: Array<MutableList<String>>,
    //此时是否空闲 false代表没课
    val isSpare: BooleanArray
  ): Serializable {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false
      
      other as SpareLineTime
      
      if (!SpareId.contentEquals(other.SpareId)) return false
      if (!isSpare.contentEquals(other.isSpare)) return false
      
      return true
    }
    
    override fun hashCode(): Int {
      var result = SpareId.contentHashCode()
      result = 31 * result + isSpare.contentHashCode()
      return result
    }
    
  }
}
  /**
   * 将返回出来的数据变为SpareTime格式
   */
fun Map<Int, List<ILessonService.Lesson>>.toSpareTime() : HashMap<Int, NoClassSpareTime>{
  val studentSpareTimes: HashMap<Int, NoClassSpareTime> = HashMap()
  //所有学生的id
  val stuIds = mutableListOf<String>()
  this.forEach {
    stuIds.add(it.value[it.key].stuNum)
  }
  //学生为划分
  this.forEach { entry ->
    //每一节课为划分
    entry.value.forEach { lesson ->
      //得到当前lesson页面的SpareTime
      if (studentSpareTimes[lesson.week] == null) {
        studentSpareTimes[lesson.week] = NoClassSpareTime(SparseArray(7)).apply {
          (0..6).map {
            spareDayTime[it] =
              NoClassSpareTime.SpareLineTime(
                Array(13) { stuIds.toMutableList() },
                BooleanArray(13)
              )
          }
        }
      }
      //当前页面的
      val stu: NoClassSpareTime = studentSpareTimes[lesson.week]!!
      //当前页面第几竖列
      val line = stu.spareDayTime[lesson.hashDay]
      (0 until lesson.period).map {
        //得到了当前一整竖行的数据
        //检查移除该格子的空闲人
        if (line.SpareId[lesson.beginLesson + it].contains(lesson.stuNum)) {
          line.SpareId[lesson.beginLesson + it].remove(lesson.stuNum)
        }
        //标记为有课
        line.isSpare[lesson.beginLesson + it] = true
      }
    }
  }
    if (studentSpareTimes.size <= 22){
      (0..22).map{
        if (studentSpareTimes[it] == null){
          studentSpareTimes[it] = NoClassSpareTime(SparseArray(7)).apply {
            (0..6).map { week ->
              spareDayTime[week] =
                NoClassSpareTime.SpareLineTime(
                  Array(13) { stuIds.toMutableList() },
                  BooleanArray(13)
                )
            }
          }
        }
      }
    }
    return studentSpareTimes
}

