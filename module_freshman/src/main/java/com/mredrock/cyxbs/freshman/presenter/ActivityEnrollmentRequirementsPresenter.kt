package com.mredrock.cyxbs.freshman.presenter

import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityEnrollmentRequirementsModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IActivityEnrollmentRequirementsPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityEnrollmentRequirementsView
import com.mredrock.cyxbs.freshman.model.ActivityEnrollmentRequirementsModel

/**
 * Create by yuanbing
 * on 2019/8/9
 */
class ActivityEnrollmentRequirementsPresenter :
        BasePresenter<IActivityEnrollmentRequirementsView, IActivityEnrollmentRequirementsModel>(),
        IActivityEnrollmentRequirementsPresenter {
    override fun getEnrollmentRequirements() {
        model?.requestEnrollmentRequirements { view?.showEnrollmentRequirements(it) }
    }

    override fun attachModel() = ActivityEnrollmentRequirementsModel()
}