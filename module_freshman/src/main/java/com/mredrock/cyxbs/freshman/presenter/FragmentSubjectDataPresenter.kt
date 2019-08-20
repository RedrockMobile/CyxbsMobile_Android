package com.mredrock.cyxbs.freshman.presenter

import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.bean.SubjectDataMessage
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentSubjectDataModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentSubjectDataPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentSubjectDataView
import com.mredrock.cyxbs.freshman.model.FragmentSubjectDataModel

class FragmentSubjectDataPresenter :
        BasePresenter<IFragmentSubjectDataView, IFragmentSubjectDataModel>(),
        IFragmentSubjectDataPresenter {
    override fun attachModel() = FragmentSubjectDataModel()

    override fun getSubjectData(college: String) {
        model?.requestSubjectData(college,
                object : IFragmentSubjectDataModel.Callback {
                    override fun requestSubjectDataSuccess(message: List<SubjectDataMessage>) {
                        view?.showSubjectData(message)
                    }

                    override fun requestSubjectDataFailed() {
                        view?.getSubJectDataFailed()
                    }
                }
        )
    }
}