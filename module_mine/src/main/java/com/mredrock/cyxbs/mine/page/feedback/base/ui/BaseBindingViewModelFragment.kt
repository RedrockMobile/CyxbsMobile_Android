package com.mredrock.cyxbs.mine.page.feedback.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel

/**
 *@author ZhiQiang Tu
 *@time 2021/8/5  11:52
 *@signature 我们不明前路，却已在路上
 */
abstract class BaseBindingViewModelFragment<VM : BaseViewModel, T : ViewDataBinding> :
    BaseViewModelFragment<VM>() {

    var binding: T? = null

    abstract fun getLayoutId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding?.lifecycleOwner = this

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //初始化view
        initView()

        //初始化监听器
        initListener()

        //初始化数据监听
        observeData()
    }


    open fun initView() {}

    open fun initListener() {}

    open fun observeData() {}
}