package com.mredrock.cyxbs.freshman.interfaces.model

import com.mredrock.cyxbs.freshman.base.IBaseModel
import com.mredrock.cyxbs.freshman.bean.SexRatioText

interface IFragmentSexRatioModel : IBaseModel {
    fun requestSexRatio(college: String, callback: Callback)

    interface Callback {
        fun requestSexRatioFaild()
        fun requestSexRatioSuccess(text: SexRatioText)
    }
}