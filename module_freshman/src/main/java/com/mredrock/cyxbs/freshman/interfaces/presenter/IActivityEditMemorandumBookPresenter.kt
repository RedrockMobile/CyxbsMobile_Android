package com.mredrock.cyxbs.freshman.interfaces.presenter

import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityEditMemorandumBookModel
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityEditMemorandumBookView

/**
 * Create by yuanbing
 * on 2019/8/10
 */
interface IActivityEditMemorandumBookPresenter :
        IBasePresenter<IActivityEditMemorandumBookView, IActivityEditMemorandumBookModel> {
    fun deleteMemorandumBook(memorandumBook: List<String>)
    fun getMemorandumBook()
}