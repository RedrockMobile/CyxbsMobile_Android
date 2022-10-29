package com.mredrock.cyxbs.noclass.page.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.api.course.ILessonService
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.noclass.bean.NoClassSpareTime
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.bean.toSpareTime
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository
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
  
  init {
    getNoclassGroupDetail()
  }
  
  /**
   * 没课约所有分组详情界面的 Livedata
   */
  val groupList: LiveData<List<NoclassGroup>> get() = _groupList
  private val _groupList = MutableLiveData<List<NoclassGroup>>()
  
  /**
   * 查找学生
   */
  val students : LiveData<List<Student>> get() = _students
  private val _students = MutableLiveData<List<Student>>()
  
  /**
   * 没课时段
   */
  val noclassData : LiveData<HashMap<Int, NoClassSpareTime>> get() = _noclassData
  private val _noclassData : MutableLiveData<HashMap<Int, NoClassSpareTime>> = MutableLiveData()
  
  /**
   * 获得全部分组数据
   * NoClassActivity只需要再init里请求一次
   */
  private fun getNoclassGroupDetail() {
    NoClassRepository
      .getNoclassGroupDetail()
      .mapOrInterceptException {
        ApiException { }
      }.doOnError {
        _groupList.postValue(emptyList())
      }.safeSubscribeBy {
        _groupList.postValue(it)
      }
  }
  
  /**
   * 查询学生
   */
  fun searchStudent(stu : String){
    NoClassRepository
      .searchStudent(stu)
      .mapOrInterceptException {
      
      }
      .safeSubscribeBy {
        _students.postValue(it)
      }
  }
  
  fun getLessons(stuNumList: List<String>,members : List<NoclassGroup.Member>){
    val studentsLessons =  mutableMapOf<Int,List<ILessonService.Lesson>>()
    fromIterable(stuNumList)
      .flatMap {
          ILessonService::class.impl
            .getStuLesson(it)
            .toObservable()
      }
      .safeSubscribeBy (
        onNext = {
          studentsLessons[stuNumList.indexOf(it[0].stuNum)] = it
        },
        onComplete = {
          _noclassData.postValue(studentsLessons.toSpareTime().apply {
            val mMap = hashMapOf<String,String>()
            members.forEach {
              mMap[it.stuNum] = it.stuName
            }
            forEach {
              it.value.mIdToNameMap = mMap
            }
          })
        },
        onError = {
          toast("网络似乎开小差了~")
        }
      )
  }
  
}