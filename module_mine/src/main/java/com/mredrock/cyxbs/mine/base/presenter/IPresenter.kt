package com.mredrock.cyxbs.mine.base.presenter

import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.IVM

/**
 *@author ZhiQiang Tu
 *@time 2021/8/8  14:34
 *@signature 好在键盘没坏。ha
 */
interface IPresenter<VM: IVM> {
    fun onAttachVM(vm:IVM)
    fun onDetachVM()
    //获得总数据
    fun fetch()
}