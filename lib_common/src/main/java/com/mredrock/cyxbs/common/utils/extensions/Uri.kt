package com.mredrock.cyxbs.common.utils.extensions

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.lang.Exception
import java.lang.IllegalStateException

/**
 *@author zhangzhe
 *@date 2020/9/3
 *@description 将该Uri转为绝对路径
 */

fun Uri.getAbsolutePath(context: Context): String{
    val selectedImage = this
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? =
                    selectedImage.let {
                        context.contentResolver?.query(
                                it,
                                filePathColumn,
                                null,
                                null,
                                null)
                    }
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
            val imgPath = columnIndex?.let { cursor.getString(it) }
            cursor?.close()
    return imgPath?: ""
}

/**
 * 是否满足BitMap大小标准
 */
@RequiresApi(Build.VERSION_CODES.O)
@Throws(IllegalStateException::class)
fun Uri.bmSizeStandardizing(defaultSize: Int = 10, context: Context) : Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    var bitmap : Bitmap? = null
    // 使用Options，预先计算如果以bitmap加载会占用多少内存
    try {
        BitmapFactory.decodeFile(this.path, options)
        val outWidth = options.outWidth
        val outHeight = options.outHeight
        if (outWidth <= 0 || outHeight <= 0) {
            throw IllegalStateException("图片格式错误")
        }
        var size = outWidth * outHeight * when(options.outConfig) {
            Bitmap.Config.ALPHA_8 -> 1
            Bitmap.Config.RGB_565 -> 2
            Bitmap.Config.ARGB_8888 -> 4
            Bitmap.Config.ARGB_4444 -> 2
            else -> 1
        } * 1.0  / (1024 * 1024)
        var inSampleSize = 1
        while (size > defaultSize) {
            size /= 4
            inSampleSize *= 2
        }
        options.inJustDecodeBounds = false
        options.inSampleSize = inSampleSize
        bitmap = BitmapFactory.decodeFile(this.path, options)
    } catch (e : Exception) {
        e.printStackTrace()
    }
    return bitmap
}