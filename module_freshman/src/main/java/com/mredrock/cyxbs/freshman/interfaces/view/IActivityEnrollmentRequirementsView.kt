package com.mredrock.cyxbs.freshman.interfaces.view

import com.mredrock.cyxbs.freshman.base.IBaseView
import com.mredrock.cyxbs.freshman.interfaces.ParseBean

/**
 * Create by yuanbing
 * on 2019/8/8
 */
interface IActivityEnrollmentRequirementsView : IBaseView {
    fun showEnrollmentRequirements(enrollmentRequirements: List<ParseBean>)
}