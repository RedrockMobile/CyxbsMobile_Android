package com.mredrock.cyxbs.common.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.config.BASE_NORMAL_IMG_URL


/**
 * 图片加载工具类
 * Created By jay68 on 2018/8/10.
 */
@Deprecated("使用 lib_utils 中的 ImageView#setImageFromUrl() 代替")
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

@Deprecated("使用 lib_utils 中的 ImageView#setAvatarImageFromUrl() 代替", replaceWith = ReplaceWith(""))
fun Context.loadAvatar(url: String?,
                       imageView: ImageView,
                       @DrawableRes placeholder: Int = R.drawable.common_default_avatar,
                       @DrawableRes error: Int = R.drawable.common_default_avatar) {
    loadRedrockImage(url, imageView, placeholder, error)
}

@Deprecated("使用 lib_utils 中的 FragmentActivity/Fragment#loadBitmap() 代替", replaceWith = ReplaceWith(""))
fun Context.loadBitmap(rowUrl:String?,doBitmap:(Bitmap)->Unit){
    val url = when {
        rowUrl.isNullOrEmpty() -> {
            return
        }
        rowUrl.startsWith("http://") || rowUrl.startsWith("https://") -> rowUrl
        else -> BASE_NORMAL_IMG_URL + rowUrl
    }
    Glide.with(this)
        .asBitmap()
        .load(url)
        .into(object:CustomTarget<Bitmap>(){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                doBitmap(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }

        })
}

@Deprecated("使用 lib_utils 中的 ImageView#setImageFromUrl() 代替", replaceWith = ReplaceWith(""))
fun ImageView.setImageFromUrl(url: String?) = context.loadRedrockImage(url, this)

@Deprecated("使用 lib_utils 中的 ImageView#setAvatarImageFromUrl() 代替", replaceWith = ReplaceWith(""))
fun ImageView.setAvatarImageFromUrl(url: String?) = context.loadAvatar(url, this)