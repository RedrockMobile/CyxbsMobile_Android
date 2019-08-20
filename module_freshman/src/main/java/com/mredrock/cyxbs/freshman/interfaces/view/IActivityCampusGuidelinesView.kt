package com.mredrock.cyxbs.freshman.interfaces.view

import com.mredrock.cyxbs.freshman.base.IBaseView
import com.mredrock.cyxbs.freshman.bean.DormitoryAndCanteenText

interface IActivityCampusGuidelinesView : IBaseView {
    fun showDormitoryAndCanteen(dormitoryAndCanteenText: List<DormitoryAndCanteenText>)
}