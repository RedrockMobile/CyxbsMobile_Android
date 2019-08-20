package com.mredrock.cyxbs.freshman.interfaces.presenter

import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityCampusGuidelinesModel
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityCampusGuidelinesView

interface IActivityCampusGuidelinesPresenter :
        IBasePresenter<IActivityCampusGuidelinesView, IActivityCampusGuidelinesModel> {
    fun getDormitoryAndCanteen()
}