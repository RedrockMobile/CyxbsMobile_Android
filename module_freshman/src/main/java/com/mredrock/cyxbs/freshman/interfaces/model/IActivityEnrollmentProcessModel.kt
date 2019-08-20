package com.mredrock.cyxbs.freshman.interfaces.model

import com.mredrock.cyxbs.freshman.base.IBaseModel
import com.mredrock.cyxbs.freshman.bean.EnrollmentProcessText

/**
 * Create by yuanbing
 * on 2019/8/3
 */
interface IActivityEnrollmentProcessModel : IBaseModel {
    fun requestErollmentProcess(callback: (List<EnrollmentProcessText>) -> Unit)
}