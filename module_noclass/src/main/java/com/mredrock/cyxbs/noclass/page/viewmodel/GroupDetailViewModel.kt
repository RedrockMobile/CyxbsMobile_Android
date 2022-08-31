package com.mredrock.cyxbs.noclass.page.viewmodel

import android.util.Log
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
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
                Log.e("testGroupDetail",it.toString())
            }.safeSubscribeBy {
                Log.e("testGroupDetail",it.toString())
            }
    }

    /**
     * 添加分组中的任务
     */
    fun addNoclassGroupMember(groupId : String, stuNum : String){
        NoClassRepository
            .addNoclassGroupMember(groupId, stuNum)
            .doOnError {

            }.safeSubscribeBy {

            }
    }


    /**
     * 删除分组中的人物
     */
    fun deleteNoclassGroupMember(groupId : String, stuNum : String){
        NoClassRepository
            .deleteNoclassGroupMember(groupId, stuNum)
            .doOnError {

            }.safeSubscribeBy {

            }
    }

}