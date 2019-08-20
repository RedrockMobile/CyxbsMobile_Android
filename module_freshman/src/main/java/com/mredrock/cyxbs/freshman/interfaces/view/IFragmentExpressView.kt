package com.mredrock.cyxbs.freshman.interfaces.view

import com.mredrock.cyxbs.freshman.base.IBaseView
import com.mredrock.cyxbs.freshman.bean.ExpressText

interface IFragmentExpressView : IBaseView {
    fun showExpress(express: List<ExpressText>)
}