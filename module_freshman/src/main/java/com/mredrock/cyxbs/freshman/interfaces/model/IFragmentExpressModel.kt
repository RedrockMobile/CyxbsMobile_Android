package com.mredrock.cyxbs.freshman.interfaces.model

import com.mredrock.cyxbs.freshman.base.IBaseModel
import com.mredrock.cyxbs.freshman.bean.ExpressText

interface IFragmentExpressModel : IBaseModel {
    fun requestExpress(callback: (List<ExpressText>) -> Unit)
}