package com.mredrock.cyxbs.common.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.mredrock.cyxbs.common.config.DIR_PHOTO
import com.mredrock.cyxbs.common.utils.LogUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by zia on 2018/8/15.
 */

/*
val savePath = saveImage(bitmap, md5Encoding("掌邮二维码"))

saveImage(bitmap, md5Encoding("掌邮二维码"),"/cyxbs/photo",100,Bitmap.CompressFormat.JPEG)

saveImage(bitmap, md5Encoding("掌邮二维码")+".png","/cyxbs/photo",100,Bitmap.CompressFormat.PNG)
*/

fun Context.saveImage(bitmap: Bitmap?,
                      name: String,
                      dir: String = DIR_PHOTO,
                      quality: Int = 100,
                      compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG): String? {
    var outputStream: FileOutputStream? = null
    try {
        val parent = File("${Environment.getExternalStorageDirectory()}$dir")
        if (!parent.exists()) {
            parent.mkdirs()
        }
        val filePath = StringBuilder()
                .append(Environment.getExternalStorageDirectory())
                .append(DIR_PHOTO)
                .append("/")
                .append(name)

        if (!name.contains(".")) {
            filePath.append(".jpg")
        }

        val file = File(filePath.toString())
        if (!file.exists()) {
            file.createNewFile()
        }
        outputStream = FileOutputStream(file)
        bitmap?.compress(compressFormat, quality, outputStream)
        outputStream.flush()
        outputStream.close()
        return filePath.toString()
    } catch (e: Exception) {
        LogUtils.e("saveImage", e.message ?: "", e)
    } finally {
        try {
            outputStream?.close()
        } catch (e: IOException) {

        }
    }
    return null
}