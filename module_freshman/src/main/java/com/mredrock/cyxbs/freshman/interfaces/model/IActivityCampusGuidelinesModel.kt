package com.mredrock.cyxbs.freshman.interfaces.model

import com.mredrock.cyxbs.freshman.base.IBaseModel
import com.mredrock.cyxbs.freshman.bean.DormitoryAndCanteenText

interface IActivityCampusGuidelinesModel : IBaseModel {
    fun requestDormitoryAndCanteen(callback: (List<DormitoryAndCanteenText>) -> Unit)
}