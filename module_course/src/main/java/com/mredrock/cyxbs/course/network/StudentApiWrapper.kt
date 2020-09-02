package com.mredrock.cyxbs.course.network

import java.io.Serializable

/**
 * Created by anriku on 2018/10/13.
 */

class StudentApiWrapper : Serializable {

    var code: String? = null
    var msg: String? = null
    var status: String? = null
    var data: List<Student>? = null

}
