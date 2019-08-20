package com.mredrock.cyxbs.freshman.interfaces.view

import com.mredrock.cyxbs.freshman.base.IBaseView
import com.mredrock.cyxbs.freshman.bean.CollegeGroupText

/**
 * Create by yuanbing
 * on 2019/8/4
 */
interface IFragmentCollegeGroupView : IBaseView {
    fun showCollegeGroup(collegeGroup: List<CollegeGroupText>)
    fun showSearchResult(collegeGroup: List<CollegeGroupText>)
}