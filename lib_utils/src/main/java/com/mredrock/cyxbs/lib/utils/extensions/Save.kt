package com.mredrock.cyxbs.lib.utils.extensions

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.mredrock.cyxbs.common.config.DIR_PHOTO
import java.io.File
import java.io.OutputStream


/**
 *@Author:SnowOwlet
 *@Date:2022/8/30 16:24
 *
 */
fun Context.saveImage(bitmap: Bitmap?, name: String, quality: Int = 100,
                      compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG) {
  val values = ContentValues()
  values.put(MediaStore.MediaColumns.DISPLAY_NAME, name.split("/").last())
  values.put(
    MediaStore.MediaColumns.MIME_TYPE, if (name.endsWith("png")) {
      "image/png"
    } else {
      "image/jpeg"
    })

  val path = if (name.contains("/")) {
    "${Environment.DIRECTORY_PICTURES}/$DIR_PHOTO/${name.split("/").first()}"
  } else {
    "${Environment.DIRECTORY_PICTURES}/$DIR_PHOTO"
  }
  values.put(MediaStore.MediaColumns.RELATIVE_PATH, path)
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

fun Context.saveFile(byteStream:ByteArray,name:String):Uri?{
  val values = ContentValues()
  values.put(MediaStore.MediaColumns.DISPLAY_NAME, name.split("/").last())
  val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(name.subSequence(name.indexOfLast{ it == '.' },name.length-1).toString())
  values.put(MediaStore.MediaColumns.MIME_TYPE,type)
  val path = if (name.contains("/")) {
    "${Environment.DIRECTORY_DOWNLOADS}/${name.split("/").first()}"
  } else {
    Environment.DIRECTORY_DOWNLOADS
  }
  values.put(MediaStore.MediaColumns.RELATIVE_PATH, path)
  var uri: Uri? = null
  var outputStream: OutputStream? = null
  try {
    uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
    outputStream = contentResolver.openOutputStream(uri!!)!!
    outputStream.write(byteStream)

    outputStream.flush()
    outputStream.close()
    return uri
  } catch (e: Exception) {
    if (uri != null) {
      contentResolver.delete(uri, null, null);
    }
    return null
  } finally {
    outputStream?.close()
  }
}

fun Context.showFile(uri:Uri): File? {
  var cursor: Cursor? = null
  try {
    val projection = arrayOf(MediaStore.Downloads.DATA)
    cursor = this.contentResolver.query(uri, projection, null, null, null)
    cursor?.moveToFirst()
    // 拿到指定的 content uri 的真实路径
    val path: String? = cursor?.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
    return path?.let { File(it) }
  } catch (e:Exception){
    e.printStackTrace()
    return null
  } finally {
    cursor?.close()
  }
}