package com.mredrock.cyxbs.noclass.page.viewmodel.activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.bean.NoclassGroupId
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.viewmodel
 * @ClassName:      GroupManagerViewModel
 * @Author:         Yan
 * @CreateDate:     2022年08月15日 03:07:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    分组管理ViewModel
 */


class GroupManagerViewModel : BaseViewModel() {
  //是否创建成功
  val isCreateSuccess : LiveData<NoclassGroupId> get() = _isCreateSuccess
  private val _isCreateSuccess = MutableLiveData<NoclassGroupId>()
  
  //是否更新成功
  val isUpdateSuccess : LiveData<Pair<String,Boolean>> get() = _isUpdateSuccess
  private val _isUpdateSuccess = MutableLiveData<Pair<String,Boolean>>()
  
  //是否删除成功
  val isDeleteSuccess : LiveData<Pair<String,Boolean>> get() = _isDeleteSuccess
  private val _isDeleteSuccess = MutableLiveData<Pair<String,Boolean>>()
  
  //分组
  val groupList : LiveData<List<NoclassGroup>> get() = _groupList
  private val _groupList = MutableLiveData<List<NoclassGroup>>()
  
  /**
   * 获得所有分组
   */
  fun getNoclassGroupDetail(){
    NoClassRepository
      .getNoclassGroupDetail()
      .mapOrInterceptException {
        Log.e("ListNoclassApiError",it.toString())
      }.doOnError {
        Log.e("ListNoclassApiError",it.toString())
      }.safeSubscribeBy {
        _groupList.postValue(it)
      }
  }
  
  /**
   * 上传分组
   */
  fun postNoclassGroup(
    name : String,
    stuNums : String,
  ){
    NoClassRepository
      .postNoclassGroup(name,stuNums)
      .mapOrInterceptException {
      
      }.doOnError {
        _isCreateSuccess.postValue(NoclassGroupId(-1))
        Log.e("ListGroupError",it.toString())
      }.safeSubscribeBy {
        _isCreateSuccess.postValue(it)
      }
  }
  
  /**
   * 更新分组
   */
  fun updateGroup(
    groupId: String,
    name: String,
    isTop: String,
  ){
    NoClassRepository
      .updateGroup(groupId, name, isTop)
      .doOnError {
        _isUpdateSuccess.postValue(Pair(groupId,false))
      }.safeSubscribeBy {
        _isUpdateSuccess.postValue(Pair(groupId,it.isSuccess()))
      }
  }
  
  
  /**
   * 删除分组
   */
  fun deleteGroup(
    groupIds : String
  ){
    NoClassRepository
      .deleteGroup(groupIds)
      .doOnError {
        _isDeleteSuccess.postValue(Pair(groupIds,false))
      }.safeSubscribeBy {
        _isDeleteSuccess.postValue(Pair(groupIds,true))
      }
  }
  
}