package com.mredrock.cyxbs.freshman.bean

import com.mredrock.cyxbs.freshman.interfaces.ParseBean
import java.io.Serializable

/**
 * Create by yuanbing
 * on 2019/8/8
 */
const val STATUS_FALSE_CUSTOM = -1

const val STATUS_TRUE_CUSTOM = 1

const val STATUS_FALSE_MUST = -2

const val STATUS_TRUE_MUST = 2

data class EnrollmentRequirementsTitleBean(
        val title: String
) : ParseBean, Serializable

data class EnrollmentRequirementsItemBean(
        val name: String,
        val detail: String,
        var status: Int
) : ParseBean,Serializable
