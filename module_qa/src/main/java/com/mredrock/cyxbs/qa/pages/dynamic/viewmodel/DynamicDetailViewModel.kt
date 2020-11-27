package com.mredrock.cyxbs.qa.pages.dynamic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel

/**
 * @Author: sandyz987
 * @Date: 2020/11/27 23:52
 */

open class DynamicDetailViewModel(kind: String) : BaseViewModel() {


    class Factory(private val kind: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(DynamicListViewModel::class.java)) {
                return DynamicListViewModel(kind) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}