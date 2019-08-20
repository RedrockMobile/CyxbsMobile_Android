package com.mredrock.cyxbs.freshman.interfaces.presenter

import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityEnrollmentProcessModel
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityEnrollmentProcessView

/**
 * Create by yuanbing
 * on 2019/8/3
 */
interface IActivityEnrollmentProcessPresenter :
        IBasePresenter<IActivityEnrollmentProcessView, IActivityEnrollmentProcessModel> {
    fun getEnrollmentProcess()
}