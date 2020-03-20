package com.mredrock.cyxbs.discover.grades.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.grades.bean.Exam
import com.mredrock.cyxbs.discover.grades.bean.Grade
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
interface ApiService {

    @FormUrlEncoded
    @POST("/234/newapi/examGrade")
    fun getGrades(@Field("stuNum") stuNum: String,
                  @Field("idNum") idNum: String,
                  @Field("forceFetch") fetch: Boolean = true): Observable<RedrockApiWrapper<List<Grade>>>

    /**
     * 获取考试信息（不含补考）
     */
    @FormUrlEncoded
    @POST("/api/examSchedule")
    fun getExam(@Field("stuNum") stuNum: String): Observable<RedrockApiWrapper<List<Exam>>>

    /**
     * 获取补考信息
     */
    @FormUrlEncoded
    @POST("/api/examReexam")
    fun getReExam(@Field("stuNum") stu: String): Observable<RedrockApiWrapper<List<Exam>>>

}