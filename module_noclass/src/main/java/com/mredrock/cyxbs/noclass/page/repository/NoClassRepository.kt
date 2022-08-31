package com.mredrock.cyxbs.noclass.page.repository

import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.bean.NoclassGroupId
import com.mredrock.cyxbs.noclass.net.NoclassApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.http.Field

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.repository
 * @ClassName:      NoClassRepository
 * @Author:         Yan
 * @CreateDate:     2022年08月29日 03:11:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */
object NoClassRepository {

    /**
     * 获得全部分组数据
     */
    fun getNoclassGroupDetail() : Single<ApiWrapper<List<NoclassGroup>>>{
        return NoclassApiService
            .INSTANCE
            .getGroupAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 上传分组
     */
    fun postNoclassGroup(
        name : String,
        stuNums : String,
    ) : Single<ApiWrapper<NoclassGroupId>> {
        return NoclassApiService
            .INSTANCE
            .postGroup(name,stuNums)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 更新分组
     */
    fun updateGroup(
        @Field("group_id") groupId: String,
        @Field("name") name: String,
        @Field("is_top") isTop: String,
    ): Single<ApiStatus>{
        return NoclassApiService
            .INSTANCE
            .updateGroup(groupId, name, isTop)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 删除分组
     */
    fun deleteGroup(
        @Field("group_ids") groupIds : String
    ): Single<ApiStatus> {
        return NoclassApiService
            .INSTANCE
            .deleteGroup(groupIds)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 删除分组中的人
     */
    fun deleteNoclassGroupMember(groupId : String, stuNum : String): Single<ApiStatus> {
        return NoclassApiService
            .INSTANCE
            .deleteGroupMember(groupId, stuNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 增加分组中的人
     */
    fun addNoclassGroupMember(groupId: String, stuNum: String): Single<ApiStatus> {
        return NoclassApiService
            .INSTANCE
            .addGroupMember(groupId, stuNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}