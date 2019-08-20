package com.mredrock.cyxbs.freshman.presenter

import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityCampusGuidelinesModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IActivityCampusGuidelinesPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityCampusGuidelinesView
import com.mredrock.cyxbs.freshman.model.ActivityCampusGuidelinesModel

class ActivityCampusGuidelinesPresenter : BasePresenter<IActivityCampusGuidelinesView,
        IActivityCampusGuidelinesModel>(), IActivityCampusGuidelinesPresenter {
    override fun attachModel() = ActivityCampusGuidelinesModel()

    override fun getDormitoryAndCanteen() {
        model?.requestDormitoryAndCanteen { view?.showDormitoryAndCanteen(it) }
    }
}