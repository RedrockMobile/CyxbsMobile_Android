package com.mredrock.cyxbs.main.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.main.ui.SplashActivity
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun downloadSplash(mUrl: String) {
    val client = OkHttpClient()
    val request = Request
            .Builder()
            .url(mUrl)
            .build()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            LogUtils.d("splash_download", "Download failed")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body()?.let {
                val stream = it.byteStream()
                val bitmap = BitmapFactory.decodeStream(stream)

                val appDir = getDir()//下载目录
                if(!appDir.exists())
                    appDir.mkdirs()

                val fileName = SplashActivity.SPLASH_PHOTO_NAME
                val file = File("$appDir/$fileName")
                try {
                    if (!file.exists())
                        file.createNewFile()
                    val fos = FileOutputStream(file)

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.flush()
                    fos.close()

                } catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }

    })

}

//删除下载的Splash图
fun deleteSplash() {
    val appDir = getDir()//下载目录
    val fileName = SplashActivity.SPLASH_PHOTO_NAME
    val destFile = File(appDir, fileName)
    if (destFile.exists())
        destFile.delete()//删除图片
}

//判断是否下载了Splash图
fun isDownloadSplash(): Boolean {
    val appDir = getDir()//下载目录
    val fileName = SplashActivity.SPLASH_PHOTO_NAME
    val destFile = File(appDir, fileName)
    return destFile.exists()
}

fun getSplashFile(): File {
    val appDir = getDir()//下载目录
    val fileName = SplashActivity.SPLASH_PHOTO_NAME
    return File(appDir, fileName)
}

fun getDir(): File {
    val pictureFolder = Environment.getExternalStorageDirectory()
    val appDir = File(pictureFolder, SplashActivity.SPLASH_PHOTO_LOCATION)
    if (!appDir.exists()) {
        appDir.mkdirs()
    }
    return appDir
}