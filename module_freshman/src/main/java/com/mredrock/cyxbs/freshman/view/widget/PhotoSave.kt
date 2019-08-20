package com.mredrock.cyxbs.freshman.view.widget

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.encrypt.md5Encoding
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Create by roger
 * on 2019/8/11
 */
fun saveImage(resource: Bitmap) {
    val file = File(BaseApp.context.externalMediaDirs[0]?.absolutePath
            + File.separator
            + "Map"
            + File.separator
            + System.currentTimeMillis()
            + ".jpg")
    val parentDir = file.parentFile
    if (parentDir.exists()) parentDir.delete()
    parentDir.mkdir()
    file.createNewFile()
    val fos = FileOutputStream(file)
    resource.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    fos.close()
    galleryAddPic(file.path);
    Toast.makeText(BaseApp.context, "图片已保存在" + file.absolutePath, Toast.LENGTH_LONG).show()
}

//更新相册
fun galleryAddPic(imagePath: String) {
    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    val f = File(imagePath)
    val contentUri = Uri.fromFile(f)
    mediaScanIntent.setData(contentUri)
    BaseApp.context.sendBroadcast(mediaScanIntent)
}