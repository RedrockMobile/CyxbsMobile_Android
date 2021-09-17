package com.mredrock.cyxbs.mine.page.mine.binder

import android.view.View
import com.mredrock.cyxbs.mine.BuildConfig
import com.mredrock.cyxbs.mine.page.mine.callback.OnViewClickListener

open class ClickBinder : OnViewClickListener {
    protected open var mOnClickListener: ((View, Any?)->Unit)? = null
    protected open var mOnLongClickListener: ((View,Any?)->Unit)? = null

    open fun setOnClickListener(listener: (View,Any?)->Unit){
        this.mOnClickListener = listener
    }

    open fun setOnLongClickListener(listener: (View,Any?)->Unit){
        this.mOnLongClickListener = listener
    }

    override fun onClick(view: View) {
        this.onClick(view,this)
    }

    override fun onClick(view: View, any: Any?) {
        if (mOnClickListener != null){
            mOnClickListener?.invoke(view,any)
        }
        else {
            if (BuildConfig.DEBUG) throw NullPointerException("OnClick事件未绑定!")
        }
    }

    override fun onLongClick(view: View) {
        this.onLongClick(view,this)
    }

    override fun onLongClick(view: View, any: Any?) {
        if (mOnLongClickListener != null){
            mOnLongClickListener?.invoke(view,any)
        }
        else {
            if (BuildConfig.DEBUG) throw NullPointerException("OnLongClick事件未绑定!")
        }
    }
}