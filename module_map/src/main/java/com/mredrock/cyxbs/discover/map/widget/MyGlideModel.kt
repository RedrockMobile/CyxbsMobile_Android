package com.mredrock.cyxbs.discover.map.widget

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream


@GlideModule
class MyGlideModel : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean {
        return super.isManifestParsingEnabled()
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
//        super.registerComponents(context,glide,registry);
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(ProgressInterceptor())
                .build()
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(okHttpClient))
    }
}