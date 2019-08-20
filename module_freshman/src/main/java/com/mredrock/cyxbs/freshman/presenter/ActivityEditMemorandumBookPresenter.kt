package com.mredrock.cyxbs.freshman.presenter

import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityEditMemorandumBookModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IActivityEditMemorandumBookPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityEditMemorandumBookView
import com.mredrock.cyxbs.freshman.model.ActivityEditMemorandumBookModel

/**
 * Create by yuanbing
 * on 2019/8/10
 */
class ActivityEditMemorandumBookPresenter :
        BasePresenter<IActivityEditMemorandumBookView, IActivityEditMemorandumBookModel>(),
        IActivityEditMemorandumBookPresenter {
    override fun attachModel() = ActivityEditMemorandumBookModel()

    override fun deleteMemorandumBook(memorandumBook: List<String>) {
        model?.deleteMemorandumBook(memorandumBook) { view?.deleteSuccess() }
    }

    override fun getMemorandumBook() {
        model?.getMemorandumBook { view?.showMemorandumBook(it) }
    }
}