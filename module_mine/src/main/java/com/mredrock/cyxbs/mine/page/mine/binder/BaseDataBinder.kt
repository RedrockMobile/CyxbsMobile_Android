package com.mredrock.cyxbs.mine.page.mine.binder

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.mredrock.cyxbs.mine.BR

abstract class BaseDataBinder<T : ViewDataBinding> : ClickBinder(){

    private val variableId = BR.data

    open val itemId:String? = null

    var binding: T? = null

    fun bindDataBinding(dataBinding: ViewDataBinding){
        if (this.binding == dataBinding) return
        onUnbindViewHolder()
        this.binding = dataBinding as T
        dataBinding.setVariable(variableId,this)
        if (dataBinding.root.context is LifecycleOwner){
            dataBinding.lifecycleOwner = dataBinding.root.context as LifecycleOwner
        }else {
            dataBinding.lifecycleOwner = AlwaysActiveLifecycleOwner()
        }
        onBindViewHolder(dataBinding)
        // 及时更新绑定数据的View
        dataBinding.executePendingBindings()
    }

    /**
     * 解绑
     */
    fun unBindDataBinding(){
        if (binding!=null){
            onUnbindViewHolder()
            binding = null
        }
    }

    protected open fun onBindViewHolder(binding: T){}

    protected open fun onUnbindViewHolder(){}

    abstract fun layoutId(): Int

    /**
     * 为 Binder 绑定生命周期，在 {@link Lifecycle.Event#ON_RESUME} 时响应
     */
    internal class AlwaysActiveLifecycleOwner : LifecycleOwner {

        override fun getLifecycle(): Lifecycle = object : LifecycleRegistry(this) {
            init {
                handleLifecycleEvent(Event.ON_RESUME)
            }
        }
    }
}