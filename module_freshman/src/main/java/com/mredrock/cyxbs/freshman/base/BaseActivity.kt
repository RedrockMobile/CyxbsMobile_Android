package com.mredrock.cyxbs.freshman.base

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity

/**
 * Create by yuanbing
 * on 2019/8/1
 */
abstract class BaseActivity<V: IBaseView, P: IBasePresenter<V, M>, M: IBaseModel>: BaseActivity() {
    protected var presenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = createPresenter()
        presenter?.attachView(getViewToAttach())
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    /**
     * 返回需要绑定的IBaseView对象
     */
    abstract fun getViewToAttach(): V

    /**
     * 创建IBasePresenter的对象
     */
    abstract fun createPresenter(): P
}