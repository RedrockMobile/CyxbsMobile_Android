package com.mredrock.cyxbs.noclass.page.viewmodel.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository
import io.reactivex.rxjava3.core.Flowable

class SolidViewModel : BaseViewModel() {

    //是否更新成功
    val isUpdateSuccess: LiveData<Pair<String, Boolean>> get() = _isUpdateSuccess
    private val _isUpdateSuccess = MutableLiveData<Pair<String, Boolean>>()

    //是否删除成功
    val isDeleteSuccess: LiveData<Pair<String, Boolean>> get() = _isDeleteSuccess
    private val _isDeleteSuccess = MutableLiveData<Pair<String, Boolean>>()

    //搜索结果
    private val _searchStudent = MutableLiveData<ApiWrapper<List<Student>>>()
    val searchStudent get() = _searchStudent

    /**
     * 组内添加成员，前面是要增加的学生的学号，后面是是否添加
     */
    private val _addMembers = MutableLiveData<Pair<Set<String>,Boolean>>()
    val addMembers get() = _addMembers

    /**
     * 获得搜索的结果
     */
    fun getSearchResult(content: String) {
        NoClassRepository.searchStudent(content)
            .doOnError {
                Log.d("lx", "getSearchResultError:${it} ")
            }
            .safeSubscribeBy {
                _searchStudent.postValue(it)
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

    /**
     * 添加成员
     * @param groupId 组id
     */
    fun addMembers(groupId: String, addSet: Set<Student>) {
        val addStu = concatSet(addSet)
        NoClassRepository.addNoclassGroupMember(groupId, addStu).doOnError {
            toast("网络异常")
        }.safeSubscribeBy {
            _addMembers.postValue(addSet.map { stu -> stu.id }.toSet() to it.isSuccess())
        }

    }

    /**
     * 一个用来拼接set中元素的方法
     */
    private fun concatSet(set: Set<Student>): String {
        var stuNums = ""
        for ((index, stu) in set.withIndex()) {
            stuNums += stu.id
            if (index != set.size - 1) {
                stuNums += ","
            }
        }
        return stuNums
    }
}