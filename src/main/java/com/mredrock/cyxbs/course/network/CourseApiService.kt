package com.mredrock.cyxbs.course.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by anriku on 2018/8/18.
 */
interface CourseApiService {

    /**
     * 获取服务器课程信息
     *
     * @param stuNum 学生学号
     * @param idNum 学生身份证后六位。可以不传值
     * @param week 获取哪一周的课程信息。可以不传值
     */
    @FormUrlEncoded
    @Headers("API_APP: android")
    @POST(CourseUrls.API_GET_COURSE)
    fun getCourse(@Field("stuNum") stuNum: String,
                  @Field("idNum") idNum: String = "",
                  @Field("week") week: String = "",
                  @Field("force_fetch") isForceFetch: Boolean = false): Observable<CourseApiWrapper<List<Course>>>


    /**
     * 获取服务器课程信息
     *
     * @param teaNum 学生学号
     * @param teaName 学生身份证后六位。可以不传值
     */
    @FormUrlEncoded
//    @Headers("API_APP: android")
    @POST(CourseUrls.API_GET_TEA_COURSE)
    fun getTeaCourse(@Field("tea") teaNum: String,
                     @Field("teaName") teaName: String): Observable<CourseApiWrapper<List<Course>>>


    /**
     * 获取服务器中的事务信息
     *
     * @param stuNum 学生学号
     * @param idNum 学生身份证后六位
     */
    @FormUrlEncoded
    @POST(CourseUrls.API_GET_AFFAIR)
    fun getAffair(@Field("stuNum") stuNum: String,
                  @Field("idNum") idNum: String = ""):
            Observable<AffairApiWrapper<List<Affair>>>

    /**
     * 向服务器上添加事务
     *
     * @param id 用于区分不同事务的id。掌邮后端中用时间戳+一个四位数组成id。
     * @param stuNum 学生学号
     * @param idNum 学生身份证后六位
     * @param date 事务日期
     * @param time 事务时间
     * @param title 事务标题
     * @param content 事务内容
     */
    @FormUrlEncoded
    @POST(CourseUrls.API_ADD_AFFAIR)
    fun addAffair(@Field("id") id: String,
                  @Field("stuNum") stuNum: String,
                  @Field("idNum") idNum: String,
                  @Field("date") date: String,
                  @Field("time") time: Int,
                  @Field("title") title: String,
                  @Field("content") content: String): Observable<RedrockApiWrapper<Unit>>

    /**
     * 编辑事务
     *
     * @param id 用于区分不同事务的id。掌邮后端中用时间戳+一个四位数组成id。
     * @param stuNum 学生学号
     * @param idNum 学生身份证后六位
     * @param date 事务日期
     * @param time 事务时间
     * @param title 事务标题
     * @param content 事务内容
     */
    @FormUrlEncoded
    @POST(CourseUrls.API_MODIFY_AFFAIR)
    fun modifyAffair(@Field("id") id: String,
                     @Field("stuNum") stuNum: String,
                     @Field("idNum") idNum: String,
                     @Field("date") date: String,
                     @Field("time") time: Int,
                     @Field("title") title: String,
                     @Field("content") content: String): Observable<RedrockApiWrapper<Unit>>


    /**
     * 删除事务
     *
     * @param stuNum 学生学号
     * @param idNum 学生身份证后六位
     * @param id 事务id
     */
    @FormUrlEncoded
    @POST(CourseUrls.API_DELETE_AFFAIR)
    fun deleteAffair(@Field("stuNum") stuNum: String,
                     @Field("idNum") idNum: String,
                     @Field("id") id: String): Observable<RedrockApiWrapper<Unit>>

    @GET(CourseUrls.API_GET_TITLE_CANDIDATE)
    fun getTitleCandidate():Observable<Candidate>



    /**
     * 获取某个课程的学生名单
     *
     * @param teacher 老师姓名
     * @param stuNum 学生学号
     * @param classRoom 教室号
     */
    @GET(CourseUrls.API_GET_STUDENT_LIST)
    fun getStudentList(@Query("teacher") teacher: String,
                       @Query("stuNum") stuNum: String,
                       @Query("classroom") classRoom: String): Observable<StudentApiWrapper>
}