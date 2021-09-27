package com.mredrock.cyxbs.qa.pages.search.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.qa.pages.search.viewmodel.QuestionSearchedViewModel

/**
 * @class
 * @author YYQF
 * @data 2021/9/24
 * @description
 **/
abstract class BaseResultFragment : BaseFragment() {

    val viewModel by activityViewModels<QuestionSearchedViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(),container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    abstract fun getViewModelFactory(): ViewModelProvider.Factory

    abstract fun initData()

    abstract fun getLayoutId(): Int

}