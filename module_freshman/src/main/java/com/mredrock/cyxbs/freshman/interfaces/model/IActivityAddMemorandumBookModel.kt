package com.mredrock.cyxbs.freshman.interfaces.model

import com.mredrock.cyxbs.freshman.base.IBaseModel

/**
 * Create by yuanbing
 * on 2019/8/10
 */
interface IActivityAddMemorandumBookModel : IBaseModel {
    fun saveMemorandumBook(name: String, callback: () -> Unit)
}