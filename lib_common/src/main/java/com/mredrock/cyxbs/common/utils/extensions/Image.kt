package com.mredrock.cyxbs.common.utils.extensions

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.mredrock.cyxbs.common.config.DIR_PHOTO
import com.mredrock.cyxbs.common.utils.LogUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * Created by zia on 2018/8/15.
 */

/*
val savePath = saveImage(bitmap, md5Encoding("掌邮二维码"))

saveImage(bitmap, md5Encoding("掌邮二维码"),"/cyxbs/photo",100,Bitmap.CompressFormat.JPEG)

saveImage(bitmap, md5Encoding("掌邮二维码")+".png","/cyxbs/photo",100,Bitmap.CompressFormat.PNG)
*/
@Deprecated(message = "common 模块的 BaseApp 已被废弃，请使用最新的 lib_utils 模块")
fun Context.saveImage(bitmap:Bitmap?,name: String,quality: Int = 100,
                 compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG) {
  val values = ContentValues()
  values.put(MediaStore.MediaColumns.DISPLAY_NAME, name.split("/").last())
  values.put(MediaStore.MediaColumns.MIME_TYPE, if (name.endsWith("png")) {
    "image/png"
  } else {
    "image/jpeg"
  })

  val path = if (name.contains("/")) {
    "${Environment.DIRECTORY_PICTURES}/$DIR_PHOTO/${name.split("/").first()}"
  } else {
    "${Environment.DIRECTORY_PICTURES}/$DIR_PHOTO"
  }
  values.put(MediaStore.MediaColumns.RELATIVE_PATH, path);
  var uri: Uri? = null
  var outputStream: OutputStream? = null
  try {
    uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    outputStream = contentResolver.openOutputStream(uri!!)!!
    bitmap?.compress(compressFormat, quality, outputStream)
    outputStream.flush()
    outputStream.close()
  } catch (e: Exception) {
    if (uri != null) {
      contentResolver.delete(uri, null, null);
    }
  } finally {
    outputStream?.close()
  }
}