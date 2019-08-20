package com.mredrock.cyxbs.freshman.interfaces.view

import com.mredrock.cyxbs.freshman.base.IBaseView
import com.mredrock.cyxbs.freshman.bean.EnrollmentProcessText

/**
 * Create by yuanbing
 * on 2019/8/3
 */
interface IActivityEnrollmentProcessView : IBaseView {
    fun showEnrollmentProcess(enrollmentProcess: List<EnrollmentProcessText>)
}