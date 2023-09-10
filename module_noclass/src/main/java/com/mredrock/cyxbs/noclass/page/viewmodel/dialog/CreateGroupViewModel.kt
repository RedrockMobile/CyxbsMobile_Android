package com.mredrock.cyxbs.noclass.page.viewmodel.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.noclass.bean.NoclassGroupId
import com.mredrock.cyxbs.noclass.page.repository.NoClassRepository

/**
 * 将创建分组这个ViewModel单独拿出来了,因为课表界面要用，固定分组fragment中也要用
 */
class CreateGroupViewModel : BaseViewModel(){

    //是否创建成功
    val isCreateSuccess : LiveData<NoclassGroupId> get() = _isCreateSuccess
    private val _isCreateSuccess = MutableLiveData<NoclassGroupId>()

    /**
     * 上传分组
     */
    fun postNoclassGroup(
        name : String,
        stuNums : String,
    ){
        NoClassRepository
            .postNoclassGroup(name,stuNums)
            .map {
                // 如果重名，就返回-2
                if (it.info == "exist") NoclassGroupId(-2) else it.data
            }
            .doOnError {
                _isCreateSuccess.postValue(NoclassGroupId(-1))
            }.safeSubscribeBy {
                _isCreateSuccess.postValue(it)
            }
    }

}