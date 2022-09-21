package lib.course.network

import com.mredrock.cyxbs.lib.utils.network.IApi
import io.reactivex.rxjava3.core.Single
import lib.course.bean.LessonBean
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 17:44
 */
interface LessonApiService : IApi {
  @POST("/magipoke-jwzx/kebiao")
  @FormUrlEncoded
  fun getStuLesson(
    @Field("stu_num")
    stuNum: String
  ) : Single<LessonBean>
}