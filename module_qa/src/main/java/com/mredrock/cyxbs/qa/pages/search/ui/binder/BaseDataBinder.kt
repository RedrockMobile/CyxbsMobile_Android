package com.mredrock.cyxbs.qa.pages.search.ui.binder

import androidx.databinding.ViewDataBinding
import com.mredrock.cyxbs.qa.BR

abstract class BaseDataBinder<T : ViewDataBinding> {

    private val variableId = BR.data

    open val itemId:String = ""

    var binding: T? = null

    fun bindDataBinding(dataBinding: ViewDataBinding){
        this.binding = dataBinding as T
        dataBinding.setVariable(variableId,this)
        onBindViewHolder(dataBinding)
    }

    protected open fun onBindViewHolder(binding: T){}

    abstract fun layoutId(): Int

    open fun areContentsTheSame(binder: BaseDataBinder<*>): Boolean = false

    open fun getName():String? = null
}