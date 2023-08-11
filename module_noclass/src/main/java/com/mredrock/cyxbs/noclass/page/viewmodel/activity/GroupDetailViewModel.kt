package com.mredrock.cyxbs.noclass.page.viewmodel.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository
import io.reactivex.rxjava3.core.Flowable

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.viewmodel
 * @ClassName:      GroupDetailViewModel
 * @Author:         Yan
 * @CreateDate:     2022年08月25日 04:35:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    具体分组页面ViewModel
 */
class GroupDetailViewModel : BaseViewModel() {
  
  /**
   * 更新标题
   */
  val isUpdateSuccess : LiveData<Boolean>
    get() = _isUpdateSuccess
  private val _isUpdateSuccess : MutableLiveData<Boolean> = MutableLiveData()
  
  /**
   * 查找学生
   */
  val students : LiveData<List<Student>> get() = _students
  private val _students = MutableLiveData<List<Student>>()
  
  /**
   * 保存成员变化
   */
  val saveState : LiveData<Boolean> get() = _saveState
  private val _saveState = MutableLiveData<Boolean>()
  
  /**
   * 用来更新标题
   */
  fun updateGroup(
    groupId: String,
    name: String,
    isTop: String,
  ){
    NoClassRepository
      .updateGroup(groupId, name, isTop)
      .doOnError {
        _isUpdateSuccess.postValue(false)
      }.safeSubscribeBy {
        _isUpdateSuccess.postValue(true)
      }
  }
  
  /**
   * 查询任务
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
  
  fun addAndDeleteStu(groupId: String,addSet : Set<Student>,deleteSet : Set<Student>){
    var addStu = ""
    var deleteStu = ""
    for ((index,stu) in addSet.withIndex()){
      addStu += stu.id
      if (index != addSet.size-1){
        addStu += ","
      }
    }
    for ((index,stu) in deleteSet.withIndex()){
      deleteStu += stu.id
      if (index != deleteSet.size-1){
        deleteStu += ","
      }
    }
    val add =  NoClassRepository.addNoclassGroupMember(groupId,addStu)
    val delete = NoClassRepository.deleteNoclassGroupMember(groupId,deleteStu)
    val result : Flowable<ApiStatus>? =
    if (addStu != "" && deleteStu != ""){
      add.mergeWith(delete)
    }else if (addStu == "" && deleteStu != ""){
      delete.toFlowable()
    }else if (addStu != "" && deleteStu == ""){
      add.toFlowable()
    }else{
      null
    }
    
    if (result == null){
      return
    }
    
    result.doOnError {
    
    }.safeSubscribeBy {
      _saveState.postValue(it.isSuccess())
    }
    
  }
  
}