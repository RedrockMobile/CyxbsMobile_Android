package com.mredrock.cyxbs.freshman.presenter

import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityEnrollmentProcessModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IActivityEnrollmentProcessPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityEnrollmentProcessView
import com.mredrock.cyxbs.freshman.model.ActivityEnrollmentProcessModel

/**
 * Create by yuanbing
 * on 2019/8/3
 */
class ActivityEnrollmentProcessPresenter :
        BasePresenter<IActivityEnrollmentProcessView, IActivityEnrollmentProcessModel>(),
        IActivityEnrollmentProcessPresenter {
    override fun attachModel() = ActivityEnrollmentProcessModel()

    override fun getEnrollmentProcess() {
        model?.requestErollmentProcess { view?.showEnrollmentProcess(it) }
    }
}