package com.mredrock.cyxbs.lib.utils.extensions

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.lib.utils.R
import com.mredrock.cyxbs.lib.utils.network.getBaseUrl

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/7/25 14:34
 */

fun ImageView.setImageFromUrl(
  url: String,
  @DrawableRes placeholder: Int = R.drawable.utils_ic_place_holder,
  @DrawableRes error: Int = R.drawable.utils_ic_place_holder,
  func: (RequestBuilder<Drawable>.() -> Unit)? = null
) {
  val realUrl = if (url.startsWith("http")) url else getBaseUrl() + url
  Glide.with(this)
    .load(realUrl)
    .apply(RequestOptions().placeholder(placeholder).error(error))
    .apply { func?.invoke(this) }
    .into(this)
}

fun ImageView.setImageFromId(
  @DrawableRes id: Int,
  @DrawableRes placeholder: Int = R.drawable.utils_ic_place_holder,
  @DrawableRes error: Int = R.drawable.utils_ic_place_holder,
  func: (RequestBuilder<Drawable>.() -> Unit)? = null
) {
  Glide.with(this)
    .load(id)
    .apply(RequestOptions().placeholder(placeholder).error(error))
    .apply { func?.invoke(this) }
    .into(this)
}