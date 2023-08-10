package com.mredrock.cyxbs.noclass.page.viewmodel.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.bean.NoclassGroupId
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository

class SolidViewModel : BaseViewModel() {

    //是否创建成功
    val isCreateSuccess : LiveData<NoclassGroupId> get() = _isCreateSuccess
    private val _isCreateSuccess = MutableLiveData<NoclassGroupId>()

    //是否更新成功
    val isUpdateSuccess : LiveData<Pair<String,Boolean>> get() = _isUpdateSuccess
    private val _isUpdateSuccess = MutableLiveData<Pair<String,Boolean>>()

    //是否删除成功
    val isDeleteSuccess : LiveData<Pair<String,Boolean>> get() = _isDeleteSuccess
    private val _isDeleteSuccess = MutableLiveData<Pair<String,Boolean>>()

    //搜索结果
    private val _searchStudent = MutableLiveData<List<Student>>()
    val searchStudent get() = _searchStudent

    //获得所有分组
    val groupList : LiveData<List<NoclassGroup>> get() = _groupList
    private val _groupList = MutableLiveData<List<NoclassGroup>>()

    /**
     * 获得所有分组
     */
    fun getAllGroup(){
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
     * 获得搜索的结果
     */
    fun getSearchResult(content: String) {
        NoClassRepository.searchStudent(content)
            .mapOrInterceptException {
                it.printStackTrace()
                toast("网络异常，请重试~")
            }
            .safeSubscribeBy {
                _searchStudent.postValue(it)
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