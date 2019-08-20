package com.mredrock.cyxbs.freshman.interfaces.presenter

import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentCollegeGroupModel
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentCollegeGroupView

/**
 * Create by yuanbing
 * on 2019/8/4
 */
interface IFragmentCollegeGroupPresenter : IBasePresenter<IFragmentCollegeGroupView, IFragmentCollegeGroupModel> {
    fun getCollegeGroup()
    fun search(college: String)
}