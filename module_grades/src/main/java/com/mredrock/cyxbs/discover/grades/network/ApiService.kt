package com.mredrock.cyxbs.discover.grades.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.grades.bean.Exam
import com.mredrock.cyxbs.discover.grades.bean.Status
import com.mredrock.cyxbs.discover.grades.bean.analyze.GPAStatus
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
interface ApiService {


    /**
     * 获取考试信息（不含补考）
     */
    @FormUrlEncoded
    @POST("/magipoke-jwzx/examSchedule")
    fun getExam(@Field("stuNum") stuNum: String): Observable<RedrockApiWrapper<List<Exam>>>

    /**
     * 获取补考信息
     */
    @FormUrlEncoded
    @POST("/magipoke-jwzx/examReexam")
    fun getReExam(@Field("stuNum") stu: String): Observable<RedrockApiWrapper<List<Exam>>>

    @POST("/magipoke/ids/unbind")
    fun unbindIds(): Observable<RedrockApiStatus>

    @GET("/magipoke/gpa")
    fun getAnalyzeData(): Observable<GPAStatus>

    /**
     * 获取考试成绩，2024接口新增
     * 参数 学号 认证密码
     */
    @FormUrlEncoded
    @POST("/magipoke-jwzx/examGrade")
    fun getExamGrades(@Field("stuNum") stuNum: String, @Field("inNum") inNum: String)

    @GET("/magipoke-jwzx/nowStatus")
    fun getNowStatus(): Observable<RedrockApiWrapper<Status>>

}