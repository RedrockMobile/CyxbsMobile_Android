package com.mredrock.cyxbs.lib.utils.extensions

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mredrock.cyxbs.config.R
import com.mredrock.cyxbs.lib.utils.network.getBaseUrl
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/7/25 14:34
 */

fun ImageView.setImageFromUrl(
  url: String,
  @DrawableRes placeholder: Int = R.drawable.config_ic_place_holder,
  @DrawableRes error: Int = R.drawable.config_ic_place_holder,
  func: (RequestBuilder<Drawable>.() -> Unit)? = null,
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
  @DrawableRes placeholder: Int = R.drawable.config_ic_place_holder,
  @DrawableRes error: Int = R.drawable.config_ic_place_holder,
  func: (RequestBuilder<Drawable>.() -> Unit)? = null,
) {
  Glide.with(this)
    .load(id)
    .apply(RequestOptions().placeholder(placeholder).error(error))
    .apply { func?.invoke(this) }
    .into(this)
}

/**
 * 加载头像
 */
fun ImageView.setAvatarImageFromUrl(
  url: String? = null,
  @DrawableRes placeholder: Int = R.drawable.config_ic_default_avatar,
  @DrawableRes error: Int = R.drawable.config_ic_default_avatar,
  func: (RequestBuilder<Drawable>.() -> Unit)? = null,
) {
  if (url != null) {
    setImageFromUrl(url, placeholder, error, func)
  } else {
    setImageFromId(R.drawable.config_ic_default_avatar, placeholder, error, func)
  }
}

/**
 * 加载 Bitmap
 *
 * 注意：如果你给 ImageView 设置了图片。你可以使用一下方式拿到 Bitmap（但仅限网络图片）
 * ```
 * val drawable = view.drawable
 * if (drawable is BitmapDrawable) {
 *     val bitmap = drawable.bitmap
 *     // ......
 * }
 * ```
 */
fun Fragment.loadBitmap(
  url: String,
  action: (Bitmap) -> Unit
) {
  val realUrl = if (url.startsWith("http")) url else getBaseUrl() + url
  Glide.with(this)
    .asBitmap()
    .load(realUrl)
    .into(
      object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
          action(resource)
        }
        
        override fun onLoadCleared(placeholder: Drawable?) {
        }
      }
    )
}

/**
 * 加载 Bitmap
 */
fun FragmentActivity.loadBitmap(
  url: String,
  action: (Bitmap) -> Unit
) {
  val realUrl = if (url.startsWith("http")) url else getBaseUrl() + url
  Glide.with(this)
    .asBitmap()
    .load(realUrl)
    .into(
      object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
          action(resource)
        }
        
        override fun onLoadCleared(placeholder: Drawable?) {
        }
      }
    )
}