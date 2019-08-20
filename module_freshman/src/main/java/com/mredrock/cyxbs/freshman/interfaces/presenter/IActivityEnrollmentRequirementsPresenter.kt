package com.mredrock.cyxbs.freshman.interfaces.presenter

import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityEnrollmentRequirementsModel
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityEnrollmentRequirementsView

/**
 * Create by yuanbing
 * on 2019/8/8
 */
interface IActivityEnrollmentRequirementsPresenter :
        IBasePresenter<IActivityEnrollmentRequirementsView, IActivityEnrollmentRequirementsModel> {
    fun getEnrollmentRequirements()
}