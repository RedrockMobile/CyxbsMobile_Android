package com.mredrock.cyxbs.course.page.find.network

import com.mredrock.cyxbs.course.page.find.bean.FindStuBean
import com.mredrock.cyxbs.course.page.find.bean.FindTeaBean
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.lib.utils.network.IApi
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/8 19:35
 */
interface FindApiServices : IApi {

  // 搜索学生信息
  @GET("/magipoke-jwzx/search/people")
  fun getStudents(
    @Query("stu")
    stu: String // 不完整的学号或者姓名
  ): Single<ApiWrapper<List<FindStuBean>>>

  // 搜索老师信息
  @POST("/magipoke-teakb/api/teaSearch")
  @FormUrlEncoded
  fun getTeachers(
    @Field("teaName")
    tea: String // 不完整的教师号或者姓名
  ): Single<ApiWrapper<List<FindTeaBean>>>
}