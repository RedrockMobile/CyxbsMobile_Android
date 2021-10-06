package com.mredrock.cyxbs.common.utils.extensions

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.config.BASE_NORMAL_IMG_URL
import com.mredrock.cyxbs.common.skin.SkinManager


/**
 * 图片加载工具类
 * Created By jay68 on 2018/8/10.
 */
fun Context.loadRedrockImage(rowUrl: String?,
                             imageView: ImageView,
                             @DrawableRes placeholder: Int = R.drawable.common_ic_place_holder,
                             @DrawableRes error: Int = R.drawable.common_ic_place_holder) {
    val url = when {
        rowUrl.isNullOrEmpty() -> {
            imageView.setImageResource(error)
            return
        }
        rowUrl.startsWith("http://") || rowUrl.startsWith("https://") -> rowUrl
        else -> BASE_NORMAL_IMG_URL + rowUrl
    }
    Glide.with(this)
            .load(url)
            // Glide 加载动画bug
//            .transition(DrawableTransitionOptions().crossFade())
            .apply(RequestOptions().placeholder(placeholder).error(error))
            .into(imageView)
}

fun Context.loadAvatar(url: String?,
                       imageView: ImageView,
                       @DrawableRes placeholder: Int = R.drawable.common_default_avatar,
                       @DrawableRes error: Int = R.drawable.common_default_avatar) {
    loadRedrockImage(url, imageView, placeholder, error)
}

fun ImageView.setImageFromUrl(url: String?) = context.loadRedrockImage(url, this)

fun ImageView.setAvatarImageFromUrl(url: String?) {
    if (url.isNullOrEmpty()) {
        this.setImageDrawable(SkinManager.getDrawable("common_default_avatar", R.drawable.common_default_avatar))
    } else {
        context.loadAvatar(url, this)
    }
}