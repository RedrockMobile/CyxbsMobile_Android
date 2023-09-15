package com.mredrock.cyxbs.noclass.page.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.noclass.bean.Cls
import com.mredrock.cyxbs.noclass.bean.NoClassGroup
import com.mredrock.cyxbs.noclass.bean.NoClassTemporarySearch
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository

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

    private val _searchAll = MutableLiveData<ApiWrapper<NoClassTemporarySearch>>()
    val searchAll get() = _searchAll

    /**
     * 组内删除成员 ，前面是要删除的学生的学号，后面是是否删除成功
     */
    private val _deleteMembers = MutableLiveData<Pair<String,Boolean>>()
    val deleteMembers get() = _deleteMembers

    //临时分组页面搜索全部
    fun getSearchAllResult(content: String) {
        NoClassRepository.searchAll(content)
            .doOnError {
                _searchAll.postValue(
                    ApiWrapper(NoClassTemporarySearch(
                        Cls("", listOf(),""),
                    NoClassGroup("",false, listOf(),""), listOf(), listOf()
                ),50000,"net error")
                )
            }
            .safeSubscribeBy {
                _searchAll.postValue(it)
            }
    }

    /**
     * 删除成员
     */
    fun deleteMembers(groupId: String, deleteStudent: Student) {
        // 目前组内管理一次仅删除一个，如日后有需要，可调用concatSet方法拼接，类似addMembers
        val deleteStuId = deleteStudent.id
        NoClassRepository.deleteNoclassGroupMember(groupId, deleteStuId).doOnError {
            toast("网络异常")
        }.safeSubscribeBy {
            _deleteMembers.postValue(deleteStuId to it.isSuccess())
        }
    }

}