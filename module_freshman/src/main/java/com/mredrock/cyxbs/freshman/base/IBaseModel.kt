package com.mredrock.cyxbs.freshman.base

/**
 * Create by yuanbing
 * on 2019/8/1
 */
interface IBaseModel {
    /**
     * 在IBasePresenter#onStop()被调用的时候默认调用
     */
    fun onStop()
}