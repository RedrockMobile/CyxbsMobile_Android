package com.mredrock.cyxbs.noclass.net

import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.net
 * @ClassName:      NoclassApiService
 * @Author:         Yan
 * @CreateDate:     2022年08月23日 02:02:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */
interface NoclassApiService {
    companion object{
        val INSTANCE by lazy {
            ApiGenerator.getApiService(NoclassApiService::class)
        }
    }

    /**
     * 没课约获取全部分组信息
     */
    @GET("/magipoke-jwzx/no_class/group/all")
    fun getGroupAll() : Single<ApiWrapper< List<NoclassGroup> >>

    /**
     * 没课约上传分组
     */
    @FormUrlEncoded
    @POST("/magipoke-jwzx/no_class/group")
    fun postGroup(
        @Field("name") name : String,
        @Field("stu_nums") stuNums : String,
    ) : Single<ApiWrapper<Int>>

}