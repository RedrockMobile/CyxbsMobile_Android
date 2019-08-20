package com.mredrock.cyxbs.freshman.interfaces.view

import com.mredrock.cyxbs.freshman.base.IBaseView
import com.mredrock.cyxbs.freshman.bean.SexRatioText

interface IFragmentSexRatioView : IBaseView {
    fun showSexRatio(text: SexRatioText)
    fun getSexRatioFaild()
}