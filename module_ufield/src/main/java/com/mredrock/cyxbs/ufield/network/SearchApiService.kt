package com.mredrock.cyxbs.ufield.network

import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.ufield.bean.ItemActivityBean
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *  description :负责搜索界面的网络请求的apiService
 *  author : lytMoon
 *  date : 2023/8/22 11:15
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
interface SearchApiService {
    companion object {
        val INSTANCE by lazy { ApiGenerator.getApiService(SearchApiService::class) }
    }

    /**
     * 涉及到四个搜索接口，这里用一个方法
     * activity_type：活动类型  all(所有), culture(文娱), sports(体育), education(教育)
     * activity_num：活动数量
     * order_by：按照什么顺序返回数据 支持create_timestamp(发布时间, 时间戳大的在返回数组的前面)和watch(想看人数, 人数多的在前面)还有start_timestamp(开始时间, 时间戳大的在前)。
     * contain_keyword：搜索拿到的关键词
     */
    @GET("/magipoke-ufield/activity/search/")
    fun getSearchData(
        @Query("activity_type") activityType: String,
        @Query("activity_num") activityNum: Int,
        @Query("order_by") orderBy: String,
        @Query("contain_keyword") containKeyword: String
    ): Single<ApiWrapper<List<ItemActivityBean.ItemAll>>>
}