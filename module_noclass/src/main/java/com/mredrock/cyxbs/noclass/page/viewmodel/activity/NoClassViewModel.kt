package com.mredrock.cyxbs.noclass.page.viewmodel.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.api.course.ILessonService
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.noclass.bean.NoClassSpareTime
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.bean.toSpareTime
import io.reactivex.rxjava3.core.Observable.fromIterable


/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui.viewmodel
 * @ClassName:      NoClassViewModel
 * @Author:         Yan
 * @CreateDate:     2022年08月11日 16:11:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */


class NoClassViewModel : BaseViewModel() {
  /**
   * 没课时段
   */
  val noclassData : LiveData<HashMap<Int, NoClassSpareTime>> get() = _noclassData
  private val _noclassData : MutableLiveData<HashMap<Int, NoClassSpareTime>> = MutableLiveData()
  
  fun getLessons(stuNumList: List<String>,students : List<Student>){
    val studentsLessons =  mutableMapOf<Int,List<ILessonService.Lesson>>()
    fromIterable(stuNumList)
      .flatMap {
          ILessonService::class.impl
            .getStuLesson(it)
            .toObservable()
      }
      .safeSubscribeBy (
        onNext = {
          //将课程对应学号的索引 对应的studentsLessons设置为it  []里面是获取学号对应传入list的索引的
          studentsLessons[stuNumList.indexOf(it[0].stuNum)] = it
        },
        onComplete = {
          //将new的studentsLessons变成空闲时间对象
          _noclassData.postValue(studentsLessons.toSpareTime().apply {
            val mMap = hashMapOf<String,String>()
            students.forEach {
              //学号和姓名的映射表
              mMap[it.id] = it.name
            }
            forEach {
              it.value.mIdToNameMap = mMap
            }
          })
        },
        onError = {
          it.printStackTrace()
          toast("网络似乎开小差了~")
        }
      )
  }
  
}