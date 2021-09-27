package com.mredrock.cyxbs.qa.pages.search.ui.callback

import android.view.View

interface OnViewClickListener{
    fun onClick(view: View){
        onClick(view,null)
    }
    fun onLongClick(view: View){
        onLongClick(view,null)
    }
    fun onClick(view: View,any: Any?){}
    fun onLongClick(view: View,any: Any?){}
}