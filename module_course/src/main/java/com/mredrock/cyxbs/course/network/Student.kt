package com.mredrock.cyxbs.course.network

import java.io.Serializable

/**
 * Created by anriku on 2018/10/13.
 */

class Student : Serializable {
    /**
     * classId : 04031501
     * major : 网络工程
     * school : 计算机科学与技术学院
     * stuId : 2015211754
     * stuName : 杨彪
     * stuSex : 男
     * year : 2015
     */

    var classId: String? = null
    var major: String? = null
    var school: String? = null
    var stuId: String? = null
    var stuName: String? = null
    var stuSex: String? = null
    var year: String? = null
}