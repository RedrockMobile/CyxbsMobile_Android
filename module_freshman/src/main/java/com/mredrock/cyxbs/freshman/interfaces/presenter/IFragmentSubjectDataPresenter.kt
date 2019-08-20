package com.mredrock.cyxbs.freshman.interfaces.presenter

import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentSubjectDataModel
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentSubjectDataView

interface IFragmentSubjectDataPresenter : IBasePresenter<IFragmentSubjectDataView, IFragmentSubjectDataModel> {
    fun getSubjectData(college: String)
}