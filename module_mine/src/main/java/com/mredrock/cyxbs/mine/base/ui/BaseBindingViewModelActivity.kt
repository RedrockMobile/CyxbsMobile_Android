package com.mredrock.cyxbs.common.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel

/**
 *@author ZhiQiang Tu
 *@time 2021/8/5  10:49
 *@signature 我们不明前路，却已在路上
 */
abstract class BaseBindingViewModelActivity<VM : BaseViewModel,T : ViewDataBinding> :
    BaseViewModelActivity<VM>() {

    protected var binding:T? = null

    abstract fun getLayoutId():Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,getLayoutId())
        binding?.lifecycleOwner = this

        //初始化view
        initView()

        //初始化监听器
        initListener()

        //初始化数据监听
        observeData()
    }

    open fun observeData() {}

    open fun initListener() {}

    open fun initView() {}

}