package com.mredrock.cyxbs.freshman.interfaces.presenter

import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityAddMemorandumBookModel
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityAddMemorandumBookView

/**
 * Create by yuanbing
 * on 2019/8/10
 */
interface IActivityAddMemorandumBookPresenter :
        IBasePresenter<IActivityAddMemorandumBookView, IActivityAddMemorandumBookModel> {
    fun saveMemorandumBook(name: String)
}