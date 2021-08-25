package com.mredrock.cyxbs.mine.page.feedback.base.presenter

import com.mredrock.cyxbs.common.viewmodel.BaseViewModel

/**
 *@author ZhiQiang Tu
 *@time 2021/8/8  14:34
 *@signature 好在键盘没坏。ha
 */
interface IPresenter<VM : BaseViewModel> {
    fun onAttachVM(vm: BaseViewModel)
    fun onDetachVM()

    //获得总数据
    fun fetch()
}