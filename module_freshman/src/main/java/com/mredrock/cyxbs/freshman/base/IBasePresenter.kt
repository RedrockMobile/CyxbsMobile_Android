package com.mredrock.cyxbs.freshman.base

/**
 * Create by yuanbing
 * on 2019/8/1
 */
interface IBasePresenter<V: IBaseView, M: IBaseModel> {
    /**
     * 绑定View
     */
    fun attachView(view: V)

    /**
     * 解绑View
     */
    fun detachView()

    /**
     * 绑定Model
     */
    fun attachModel(): M

    /**
     * 初始化Presenter的时候调用
     */
    fun onStart()

    /**
     * 和View解除绑定的时候默认调用
     */
    fun onStop()
}