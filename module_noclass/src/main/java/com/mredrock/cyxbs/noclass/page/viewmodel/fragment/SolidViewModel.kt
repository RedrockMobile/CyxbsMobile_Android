package com.mredrock.cyxbs.noclass.page.viewmodel.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.noclass.bean.NoclassGroupId
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository
import io.reactivex.rxjava3.core.Flowable

class SolidViewModel : BaseViewModel() {

    //是否创建成功，返回-1为创建失败，其它为成功
    val isCreateSuccess: LiveData<NoclassGroupId> get() = _isCreateSuccess
    private val _isCreateSuccess = MutableLiveData<NoclassGroupId>()

    //是否更新成功
    val isUpdateSuccess: LiveData<Pair<String, Boolean>> get() = _isUpdateSuccess
    private val _isUpdateSuccess = MutableLiveData<Pair<String, Boolean>>()

    //是否删除成功
    val isDeleteSuccess: LiveData<Pair<String, Boolean>> get() = _isDeleteSuccess
    private val _isDeleteSuccess = MutableLiveData<Pair<String, Boolean>>()

    //搜索结果
    private val _searchStudent = MutableLiveData<ApiWrapper<List<Student>>>()
    val searchStudent get() = _searchStudent

    //保存成员变化
    val saveState : LiveData<Boolean> get() = _saveState
    private val _saveState = MutableLiveData<Boolean>()
    /**
     * 获得搜索的结果
     */
    fun getSearchResult(content: String) {
        NoClassRepository.searchStudent(content)
            .doOnError {
                Log.d("lx", "getSearchResultError:${it} ")
            }
            .safeSubscribeBy {
                _searchStudent.postValue(ApiWrapper(data,10000,"success"))
            }
    }

    /**
     * 上传分组
     */
    fun postNoclassGroup(
        name: String,
        stuNums: String,
    ) {
        NoClassRepository
            .postNoclassGroup(name, stuNums)
            .mapOrInterceptException {

            }.doOnError {
                _isCreateSuccess.postValue(NoclassGroupId(-1))
                Log.e("ListGroupError", it.toString())
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
    ) {
        NoClassRepository
            .updateGroup(groupId, name, isTop)
            .doOnError {
                _isUpdateSuccess.postValue(Pair(groupId, false))
            }.safeSubscribeBy {
                _isUpdateSuccess.postValue(Pair(groupId, it.isSuccess()))
            }
    }

    /**
     * 删除分组
     */
    fun deleteGroup(
        groupIds: String
    ) {
        NoClassRepository
            .deleteGroup(groupIds)
            .doOnError {
                _isDeleteSuccess.postValue(Pair(groupIds, false))
            }.safeSubscribeBy {
                _isDeleteSuccess.postValue(Pair(groupIds, true))
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


    val data =  listOf(
        Student(
            classNum = "04082201",
            gender = "男",
            grade = "21",
            major = "数据科学与大数据技术",
            "喵喵",
            id = "2022231392"
        ),
        Student(
            classNum = "04082202",
            gender = "女",
            grade = "23",
            major = "大数据管理与应用",
            "狗狗",
            id = "2021241393"
        ),
        Student(
            classNum = "04082203",
            gender = "男",
            grade = "21",
            major = "数据科学与大数据技术",
            "汪汪",
            id = "2022231394"
        ),
        Student(
            classNum = "04082204",
            gender = "女",
            grade = "23",
            major = "大数据管理与应用",
            "呱呱",
            id = "2021241395"
        ),
        Student(
            classNum = "04082206",
            gender = "男",
            grade = "21",
            major = "数据科学与大数据技术",
            "叽叽",
            id = "2022231396"
        ),
        Student(
            classNum = "04082207",
            gender = "女",
            grade = "23",
            major = "大数据管理与应用",
            "芜湖",
            id = "2021241397"
        )
    )
}