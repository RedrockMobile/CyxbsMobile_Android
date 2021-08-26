package com.mredrock.cyxbs.mine.page.feedback.adapter

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("showView")
    fun showView(view: View?, show: Boolean?) {
        if (show == false) {
            view?.visibility = View.GONE
        } else {
            view?.visibility = View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("viewState", "backgroundFalse", "backgroundTrue", requireAll = true)
    fun stateView(
        view: View?,
        state: Boolean?,
        backgroundFalse: Drawable?,
        backgroundTrue: Drawable?
    ) {
        if (state == null) return
        if (state) {
            view?.background = backgroundTrue
        } else {
            view?.background = backgroundFalse
        }
    }

    @JvmStatic
    @BindingAdapter("netImage", "placeholder", "error", requireAll = true)
    fun netImage(
        imageView: ImageView, url: String?,
        placeholder: Drawable?,
        error: Drawable?
    ) {
        url ?: return
        if (!url.matches(Regex("http.+"))) return
        Glide.with(imageView).load(url).placeholder(placeholder).error(error).into(imageView)
    }

    @JvmStatic
    @BindingAdapter("localImage")
    fun localImage(
        imageView: ImageView?,
        imageUri: Uri?
    ) {
        imageView ?: return
        imageUri ?: return
        Glide.with(imageView).load(imageUri).into(imageView)
    }

}