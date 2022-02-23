package com.mredrock.cyxbs.course.network

import java.io.Serializable

/**
 * Created by anriku on 2018/8/14.
 */

open class RedRockApiWrapper<T>(var status: Int = 0,
                                var isSuccess: Boolean = false,
                                var version: String? = null,
                                var term: String? = null,
                                var stuNum: String = "",
                                var data: T? = null) : Serializable