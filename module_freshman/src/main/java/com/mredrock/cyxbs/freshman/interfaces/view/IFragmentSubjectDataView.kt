package com.mredrock.cyxbs.freshman.interfaces.view

import com.mredrock.cyxbs.freshman.base.IBaseView
import com.mredrock.cyxbs.freshman.bean.SubjectDataMessage

interface IFragmentSubjectDataView : IBaseView {
    fun showSubjectData(subjectData: List<SubjectDataMessage>)
    fun getSubJectDataFailed()
}