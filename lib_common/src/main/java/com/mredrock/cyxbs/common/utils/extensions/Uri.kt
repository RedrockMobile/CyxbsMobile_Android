package com.mredrock.cyxbs.common.utils.extensions

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

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