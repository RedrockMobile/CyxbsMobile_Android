package com.mredrock.cyxbs.freshman.util

import android.content.Context
import android.widget.ImageView
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.youth.banner.loader.ImageLoader

object GlideImageLoader : ImageLoader() {
    override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {
        if (imageView == null) return
        imageView.setImageFromUrl(path.toString())
    }
}