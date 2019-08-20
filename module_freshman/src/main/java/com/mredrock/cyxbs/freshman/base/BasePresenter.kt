package com.mredrock.cyxbs.freshman.base

/**
 * Create by yuanbing
 * on 2019/8/1
 */
abstract class BasePresenter<V: IBaseView, M: IBaseModel> : IBasePresenter<V, M> {
    protected var view: V? = null
    protected var model: M? = null

    override fun detachView() {
        view = null
        onStop()
    }

    override fun attachView(view: V) {
        this.view = view
        model = attachModel()
    }

    override fun onStop() {
        model?.onStop()
        model = null
    }

    override fun onStart() {}

    init {
        onStart()
    }
}