package com.mredrock.cyxbs.course.page.course.network

import com.mredrock.cyxbs.course.page.course.bean.StuLessonBean
import com.mredrock.cyxbs.course.page.course.bean.TeaLessonBean
import com.mredrock.cyxbs.lib.utils.network.IApi
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/2 18:18
 */
interface CourseApiServices : IApi {
  
  @POST("/magipoke-jwzx/kebiao")
  @FormUrlEncoded
  fun getStuLesson(
    @Field("stu_num")
    stuNum: String
  ) : Single<StuLessonBean>
  
  @POST("/magipoke-teakb/api/teaKb")
  @FormUrlEncoded
  fun getTeaLesson(
    @Field("tea")
    teaNum: String
  ) : Single<TeaLessonBean>
}