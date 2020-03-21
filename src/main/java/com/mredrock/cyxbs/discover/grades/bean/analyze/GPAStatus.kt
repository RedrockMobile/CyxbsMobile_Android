package com.mredrock.cyxbs.discover.grades.bean.analyze

import java.io.Serializable

/**
 * Created by roger on 2020/3/21
 */
data class GPAStatus(
        val data: GPAData,
        val status: String
) : Serializable