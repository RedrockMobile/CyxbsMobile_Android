package com.mredrock.cyxbs.course.event

import com.mredrock.cyxbs.course.network.Course

/**
 * Created by anriku on 2018/10/11.
 */

data class AffairFromInternetEvent(val affairs: List<Course>)