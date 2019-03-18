package com.mredrock.cyxbs.course.network

/**
 * Created by anriku on 2018/8/18.
 */
object CourseUrls {

    // 课程
    const val API_GET_COURSE = "/redapi2/api/kebiao"

    //事务
    const val API_GET_AFFAIR = "/app/index.php/Home/Person/getTransaction"
    const val API_ADD_AFFAIR = "/app/index.php/Home/Person/addTransaction"
    const val API_MODIFY_AFFAIR = "/app/index.php/Home/Person/editTransaction"
    const val API_DELETE_AFFAIR = "/app/index.php/Home/Person/deleteTransaction"

    // 选课名单
    const val STUDENT_LIST_BASE_URL = "http://wx.yyeke.com"
    const val API_GET_STUDENT_LIST = "/api/search/coursetable/xkmdsearch"

}