package com.mredrock.cyxbs.freshman.interfaces.view

import com.mredrock.cyxbs.freshman.base.IBaseView
import com.mredrock.cyxbs.freshman.interfaces.ParseBean

/**
 * Create by yuanbing
 * on 2019/8/10
 */
interface IActivityEditMemorandumBookView : IBaseView {
    fun deleteSuccess()
    fun showMemorandumBook(memorandumBook: List<ParseBean>)
}