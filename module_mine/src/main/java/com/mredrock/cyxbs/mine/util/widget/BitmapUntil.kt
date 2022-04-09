package com.mredrock.cyxbs.mine.util.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

/**
 * @ClassName BitmapUntil
 * @Description TODO
 * @Author 29942
 * @QQ 2994250239
 * @Date 2021/10/19 1:42
 * @Version 1.0
 */

/**
 * 加载网络请求的Bitmap图片出来
 */
fun loadBitmap(context: Context, url: String, success: (Bitmap) -> Unit){
    Glide.with(context) // context，可添加到参数中
        .asBitmap()
        .load(url)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                // 成功返回 Bitmap
                success.invoke(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
}