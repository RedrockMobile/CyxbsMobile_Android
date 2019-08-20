package com.mredrock.cyxbs.freshman.presenter

import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityAddMemorandumBookModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IActivityAddMemorandumBookPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityAddMemorandumBookView
import com.mredrock.cyxbs.freshman.model.ActivityAddMemorandumBookModel

/**
 * Create by yuanbing
 * on 2019/8/10
 */
class ActivityAddMemorandumBookPresenter :
        BasePresenter<IActivityAddMemorandumBookView, IActivityAddMemorandumBookModel>(),
        IActivityAddMemorandumBookPresenter {
    override fun attachModel() = ActivityAddMemorandumBookModel()

    override fun saveMemorandumBook(name: String) {
        model?.saveMemorandumBook(name) { view?.saveSuccess() }
    }
}