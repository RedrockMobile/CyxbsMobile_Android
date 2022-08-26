package com.mredrock.cyxbs.course.page.link.network

import com.mredrock.cyxbs.course.page.link.bean.DeleteLinkBean
import com.mredrock.cyxbs.course.page.link.bean.LinkStudent
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.lib.utils.network.IApi
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/24 17:07
 */
interface LinkApiServices : IApi {
  
  // 得到我的关联
  @GET("/magipoke-jwzx/kebiao/relevance/")
  fun getLinkStudent(): Single<ApiWrapper<LinkStudent>>
  
  // 修改我的关联
  @PUT("/magipoke-jwzx/kebiao/relevance/")
  @FormUrlEncoded
  fun changeLinkStudent(
    @Field("stuNum")
    stuNum: String // 注意：这是被关联人的学号
  ): Single<ApiWrapper<LinkStudent>>
  
  // 删除我的关联
  @DELETE("/magipoke-jwzx/kebiao/relevance/")
  fun deleteLinkStudent(): Single<ApiWrapper<DeleteLinkBean>>
}