//package com.mredrock.cyxbs.ufield.lyt.helper
//
//
//import android.content.Context
//import android.graphics.*
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.Transformation
//import com.bumptech.glide.load.engine.Resource
//import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
//import com.bumptech.glide.load.resource.bitmap.BitmapResource
//import com.bumptech.glide.load.resource.bitmap.TransformationUtils
//
//
//
///**
// *  description :
// *  author : lytMoon
// *  date : 2023/8/29 19:33
// *  email : yytds@foxmail.com
// *  version ： 1.0
// */
//
// class TopRoundedCornersTransformation(
//    private val radius: Float
//) : Transformation<Bitmap> {
//
//    override fun transform(
//        context: Context,
//        pool: BitmapPool,
//        toTransform: Bitmap,
//        outWidth: Int,
//        outHeight: Int
//    ): Resource<Bitmap> {
//        val transformedBitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight)
//        val result = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888)
//
//        val canvas = Canvas(result)
//        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
//        paint.shader = BitmapShader(transformedBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
//
//        // 上方两个圆角
//        val topRoundedRect = RectF(0f, 0f, outWidth.toFloat(), outHeight.toFloat() - radius)
//        canvas.drawRoundRect(topRoundedRect, radius, radius, paint)
//
//        // 下方直角
//        val bottomRect = RectF(0f, outHeight.toFloat() - radius, outWidth.toFloat(), outHeight.toFloat())
//        canvas.drawRect(bottomRect, paint)
//
//        return BitmapResource.obtain(result, pool)!!
//    }
//
//    override fun getId(): String {
//        return "TopRoundedCornersTransformation(radius=$radius)"
//    }
//}