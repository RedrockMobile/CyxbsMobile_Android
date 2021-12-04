package com.mredrock.cyxbs.qa.pages.search.ui.binder

import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.mredrock.cyxbs.qa.BR

abstract class BaseDataBinder<T : ViewDataBinding> : ClickBinder(){

    private val variableId = BR.data

    open val itemId:String = ""

    var binding: T? = null

    fun bindDataBinding(dataBinding: ViewDataBinding,position: Int? = null){
        Log.d("TAG","(BaseDataBinder.kt:19)->position = $position ; nickname = ${getName()}")
        this.binding = dataBinding as T
        dataBinding.setVariable(variableId,this)
        onBindViewHolder(dataBinding,position)
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

    protected open fun onBindViewHolder(binding: T,position: Int?){}

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

    open fun getName():String? = null
}