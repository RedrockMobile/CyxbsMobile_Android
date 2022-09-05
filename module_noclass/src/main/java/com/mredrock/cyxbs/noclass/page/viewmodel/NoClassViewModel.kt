package com.mredrock.cyxbs.noclass.page.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository


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
    Log.e("NoClassViewModel", "startNet")
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
   * 添加成员
   */
  val addState : LiveData<Pair<Boolean,String>> get() = _addState
  private val _addState = MutableLiveData<Pair<Boolean,String>>()
  
  /**
   * 删除成员
   */
  val deleteState : LiveData<Pair<Boolean,String>> get() = _deleteState
  private val _deleteState = MutableLiveData<Pair<Boolean,String>>()
  
  /**
   * 获得全部分组数据
   * NoClassActivity只需要再init里请求一次
   */
  private fun getNoclassGroupDetail() {
    NoClassRepository
      .getNoclassGroupDetail()
      .mapOrInterceptException {
        ApiException { }
        Log.e("ListNoclassApiError", it.toString())
      }.doOnError {
        Log.e("ListNoclassApiError", it.toString())
        _groupList.postValue(emptyList())
      }.safeSubscribeBy {
        _groupList.postValue(it)
      }
  }
  
  /**
   * 添加成员进族中
   */
  fun addNoclassGroupMember(groupId : String, stuNum: String){
    NoClassRepository
      .addNoclassGroupMember(groupId, stuNum)
      .doOnError {
        _addState.postValue(Pair(false,"-2"))
      }.safeSubscribeBy {
        _addState.postValue(Pair(it.isSuccess(),groupId))
      }
  }
  /**
   * 删除组中成员
   */
  fun deleteNoclassGroupMember(groupId: String, stuNum: String) {
    NoClassRepository
      .deleteNoclassGroupMember(groupId, stuNum)
      .doOnError {
        _deleteState.postValue(Pair(false,"-2"))
      }.safeSubscribeBy {
        _deleteState.postValue(Pair(it.isSuccess(),groupId))
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
  
  
}