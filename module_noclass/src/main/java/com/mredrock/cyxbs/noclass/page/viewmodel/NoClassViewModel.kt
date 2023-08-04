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
          studentsLessons[stuNumList.indexOf(it[0].stuNum)] = it   //todo 将课程对应学号的索引 对应的studentsLessons设置为it  []里面是获取学号对应传入list的索引的
        },
        onComplete = {
          _noclassData.postValue(studentsLessons.toSpareTime().apply {   //todo 将new的studentsLessons变成空闲时间对象
            val mMap = hashMapOf<String,String>()
            members.forEach {
              mMap[it.stuNum] = it.stuName    //todo 学号和姓名的映射表
            }
            forEach {
              it.value.mIdToNameMap = mMap   //todo 每一位学生的映射表获取了所有同学的学号姓名映射表？
            }
          })
        },
        onError = {
          toast("网络似乎开小差了~")
        }
      )
  }
  
}