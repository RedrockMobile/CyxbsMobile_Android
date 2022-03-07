package com.mredrock.cyxbs.mine.page.feedback.utils

import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import java.io.File

/**
 * @Date : 2021/9/3   14:24
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
object FileUtils {
    /**
     * uri转File
     */
    fun uri2File(activity: Activity, uri: Uri): File {
        var ima: String = ""
        val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
        val actualImageCursor = activity.managedQuery(uri, proj, null, null, null)
        ima = if (actualImageCursor == null) {
            uri.path
        } else {
            val index = actualImageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            actualImageCursor.moveToFirst()
            actualImageCursor.getString(index)
        }
        return File(ima)
    }
}