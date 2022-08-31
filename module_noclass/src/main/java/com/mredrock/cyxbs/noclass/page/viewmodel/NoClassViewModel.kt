package com.mredrock.cyxbs.noclass.page.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.mapOrCatchApiException
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
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
        Log.e("NoClassViewModel","startNet")
        getNoclassGroupDetail()
    }

    /**
     * 没课约所有分组详情界面的 Livedata
     */
    val groupList : LiveData<List<NoclassGroup>> get() = _groupList
    private val _groupList = MutableLiveData<List<NoclassGroup>>()

    /**
     * 获得全部分组数据
     * NoClassActivity只需要再init里请求一次
     */
    private fun getNoclassGroupDetail(){
        NoClassRepository
            .getNoclassGroupDetail()
            .mapOrCatchApiException {
                Log.e("ListNoclassApiError",it.toString())
            }.doOnError {
                Log.e("ListNoclassApiError",it.toString())
                _groupList.postValue(emptyList())
            }.safeSubscribeBy {
                _groupList.postValue(it)
            }
    }

    fun deleteNoclassGroupMember(groupId : String, stuNum : String){
        NoClassRepository
            .deleteNoclassGroupMember(groupId, stuNum)
            .doOnError {

            }.safeSubscribeBy {
                if (it.isSuccess()){
                    toast("删除成功")
                }
            }
    }


}