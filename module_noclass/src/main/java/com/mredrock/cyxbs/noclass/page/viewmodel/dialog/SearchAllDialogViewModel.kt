package com.mredrock.cyxbs.noclass.page.viewmodel.dialog

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository

class SearchAllDialogViewModel : BaseViewModel(){

    /**
     * 组内添加成员，前面是要增加的学生的学号，后面是是否添加
     */
    private val _addMembers = MutableLiveData<Pair<Set<String>,Boolean>>()
    val addMembers get() = _addMembers

    /**
     * 添加成员
     * @param groupId 组id
     */
    fun addMembers(groupId: String, addSet: Set<Student>) {
        val addStu = concatSet(addSet)
        NoClassRepository.addNoclassGroupMember(groupId, addStu).doOnError {
            toast("网络异常")
            _addMembers.postValue(
                setOf(
                    "2022211293",
//                    "2022211292",
//                    "2022211294",
//                    "2022211295",
                ) to true
            )
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