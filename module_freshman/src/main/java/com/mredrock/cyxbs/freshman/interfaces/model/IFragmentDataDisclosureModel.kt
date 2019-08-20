package com.mredrock.cyxbs.freshman.interfaces.model

import com.mredrock.cyxbs.freshman.base.IBaseModel

/**
 * Create by yuanbing
 * on 2019/08/10
 */
interface IFragmentDataDisclosureModel : IBaseModel {
    fun requestCollegeData(callback: (List<String>) -> Unit)
}