package com.mredrock.cyxbs.freshman.interfaces.model

import com.mredrock.cyxbs.freshman.base.IBaseModel
import com.mredrock.cyxbs.freshman.interfaces.ParseBean

/**
 * Create by yuanbing
 * on 2019/8/10
 */
interface IActivityEditMemorandumBookModel : IBaseModel {
    fun getMemorandumBook(callback: (List<ParseBean>) -> Unit)
    fun deleteMemorandumBook(memorandumBook: List<String>, callback: () -> Unit)
}