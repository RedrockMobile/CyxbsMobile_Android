package com.mredrock.cyxbs.mine.page.feedback.base.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.mredrock.cyxbs.mine.page.feedback.base.presenter.BasePresenter
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel

/**
 *@author ZhiQiang Tu
 *@time 2021/8/7  11:38
 *@signature 我们不明前路，却已在路上
 */
abstract class BaseMVPVMActivity<VM : BaseViewModel, T : ViewDataBinding, P : BasePresenter<*>> :
    BaseViewModelActivity<VM>() {

    protected var presenter: P? = null

    abstract fun createPresenter(): P

    protected var binding: T? = null

    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding?.lifecycleOwner = this

        //创建一个弱引用的Presenter
        //考虑到可能在创建过程中可能会在构造函数上加东西，所以就交由Activity完成
        presenter = createPresenter()
        //建立联系
        //LiveData+Lifecycle的作用下已经不需要view了
//        presenter?.onAttachView(this)
        presenter?.onAttachVM(viewModel)
        //双向关联成功

        //添加生命周期的监听
        presenter?.let { lifecycle.addObserver(it) }

        //初始化view
        initView()
        //初始化监听器
        initListener()
        //初始化数据监听
        observeData()
        //丢锅
        fetch()
    }

    open fun fetch() {
        presenter?.fetch()
    }

    override fun onDestroy() {
        //移除生命周期的监听
        presenter?.let { lifecycle.removeObserver(it) }
        //接触与VM的引用联系
        presenter?.onDetachVM()


        //置空
        presenter?.clear()
        presenter = null

        //activity凉了
        super.onDestroy()

        //接触presenter中的引用
        //presenter?.detachView()
    }

    open fun initView() {
    }

    open fun initListener() {}

    open fun observeData() {}

}