package com.mredrock.cyxbs.course.network

/**
 * Created by anriku on 2018/8/18.
 */
object CourseUrls {

    // 课程
    const val API_GET_COURSE = "/renewapi/kebiao"

    //老师课表
    const val API_GET_TEA_COURSE = "/magipoke-teaKb/api/teaKb"

    //事务
    const val API_GET_AFFAIR = "/magipoke-reminder/Person/getTransaction"
    const val API_ADD_AFFAIR = "/magipoke-reminder/Person/addTransaction"
    const val API_MODIFY_AFFAIR = "/magipoke-reminder/Person/editTransaction"
    const val API_DELETE_AFFAIR = "/magipoke-reminder/Person/deleteTransaction"

    //事务标题候选
    const val API_GET_TITLE_CANDIDATE = "/magipoke-reminder/Person/getHotWord"

    // 选课名单
    const val STUDENT_LIST_BASE_URL = "http://wx.yyeke.com"
    const val API_GET_STUDENT_LIST = "/api/search/coursetable/xkmdsearch"

}