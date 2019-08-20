package com.mredrock.cyxbs.freshman.interfaces.model

import com.mredrock.cyxbs.freshman.base.IBaseModel
import com.mredrock.cyxbs.freshman.interfaces.ParseBean

/**
 * Create by yuanbing
 * on 2019/8/8
 */
interface IActivityEnrollmentRequirementsModel : IBaseModel {
    fun requestEnrollmentRequirements(callback: (List<ParseBean>) -> Unit)
}