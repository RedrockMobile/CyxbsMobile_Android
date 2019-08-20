package com.mredrock.cyxbs.freshman.interfaces.model

import com.mredrock.cyxbs.freshman.base.IBaseModel
import com.mredrock.cyxbs.freshman.bean.FellowTownsmanGroupText

/**
 * Create by yuanbing
 * on 2019/8/4
 */
interface IFragmentFellowTownsmanGroupModel : IBaseModel {
    fun requestFellowTownsmanGroup(callback: (List<FellowTownsmanGroupText>) -> Unit)
    fun searchFellowTownsmanGroup(province: String, callback: (List<FellowTownsmanGroupText>) -> Unit)
}