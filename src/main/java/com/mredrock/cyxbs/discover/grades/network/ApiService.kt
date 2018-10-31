package com.mredrock.cyxbs.grades.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.grades.bean.Exam
import com.mredrock.cyxbs.grades.bean.Grade
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
    @POST("/api/examGrade")
    fun getGrades(@Field("stuNum") stuNum: String,
                  @Field("idNum") idNum: String): Observable<RedrockApiWrapper<List<Grade>>>

    @FormUrlEncoded
    @POST("/api/examSchedule")
    fun getExam(@Field("stuNum") stuNum: String): Observable<RedrockApiWrapper<List<Exam>>>

    @FormUrlEncoded
    @POST("/api/examReexam")
    fun getReExam(@Field("stuNum") stu: String): Observable<RedrockApiWrapper<List<Exam>>>

}