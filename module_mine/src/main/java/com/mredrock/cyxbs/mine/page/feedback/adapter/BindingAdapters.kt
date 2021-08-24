package com.mredrock.cyxbs.mine.page.feedback.adapter

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.BindingAdapter

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("showView")
    fun showView(view: View?, show: Boolean?){
        if (show == false){
            view?.visibility = View.GONE
        }else{
            view?.visibility = View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("viewState", "backgroundFalse", "backgroundTrue", requireAll = true)
    fun stateView(
        view: View?,
        state: Boolean?,
        backgroundFalse: Drawable?,
        backgroundTrue: Drawable?,
    ) {
        if (state == null) return
        if (state) {
            view?.background = backgroundTrue
        } else {
            view?.background = backgroundFalse
        }
    }
}