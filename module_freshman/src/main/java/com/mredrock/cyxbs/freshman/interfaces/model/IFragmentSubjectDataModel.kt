package com.mredrock.cyxbs.freshman.interfaces.model

import com.mredrock.cyxbs.freshman.base.IBaseModel
import com.mredrock.cyxbs.freshman.bean.SubjectDataMessage

interface IFragmentSubjectDataModel : IBaseModel {
    fun requestSubjectData(college: String, callback: Callback)

    interface Callback {
        fun requestSubjectDataSuccess(message: List<SubjectDataMessage>)
        fun requestSubjectDataFailed()
    }
}