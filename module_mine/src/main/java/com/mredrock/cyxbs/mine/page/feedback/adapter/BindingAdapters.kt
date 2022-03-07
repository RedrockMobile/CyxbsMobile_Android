package com.mredrock.cyxbs.mine.page.feedback.adapter

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

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

        Log.d("sss", "localImage:${url.toString()} ")
        Glide.with(imageView.context)
            .load(url)
            .placeholder(placeholder)
            .skipMemoryCache(true)
            .into(imageView)
//            .error(error)
    }

    @JvmStatic
    @BindingAdapter("localImage")
    fun localImage(
        imageView: ImageView?,
        imageUri: Uri?
    ) {
        imageView ?: return
        imageUri ?: return
        Log.d("sss", "localImage:${imageUri.toString()} ")
        Glide.with(imageView.context)
            .load(imageUri)
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("stateColor", "colorFalse", "colorTrue", requireAll = true)
    fun stateColor(
        textView: TextView?,
        stateColor: Boolean?,
        @ColorInt colorFalse: Int?,
        @ColorInt colorTrue: Int?
    ) {
        textView ?: return
        stateColor ?: return
        if (stateColor == false) {
            colorFalse?.let { textView.setTextColor(it) }
        } else {
            colorTrue?.let { textView.setTextColor(it) }
        }

    }

}