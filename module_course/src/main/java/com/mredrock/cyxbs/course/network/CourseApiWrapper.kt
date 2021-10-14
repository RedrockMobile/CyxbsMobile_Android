package com.mredrock.cyxbs.course.network

/**
 * Created by anriku on 2018/8/14.
 */
class CourseApiWrapper<T>(var cachedTimestamp: Long = 0,
                          var outOfDateTimestamp: Long = 0,
                          var nowWeek: Int = 0) : RedRockApiWrapper<T>()