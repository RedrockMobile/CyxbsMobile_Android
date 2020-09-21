package com.mredrock.cyxbs.main.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.main.ui.MainActivity
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun downloadSplash(mUrl: String, context: Context) {
    val client = OkHttpClient()
    val request = Request.Builder().url(mUrl).build()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            LogUtils.d("splash_download", "Download failed")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.let {
                val stream = it.byteStream()
                val bitmap = BitmapFactory.decodeStream(stream)

                val appDir = getDir(context)//下载目录
                if (!appDir.exists())
                    appDir.mkdirs()

                val fileName = MainActivity.SPLASH_PHOTO_NAME
                val file = File("$appDir/$fileName")
                try {
                    if (!file.exists())
                        file.createNewFile()
                    val fos = FileOutputStream(file)

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.flush()
                    fos.close()
                    LogUtils.d("splash_download", "Download success")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    })

}

//判断是否下载了Splash图
fun isDownloadSplash(context: Context): Boolean {
    return getSplashFile(context).exists()
}

fun getSplashFile(context: Context): File {
    val appDir = getDir(context)//下载目录
    val fileName = MainActivity.SPLASH_PHOTO_NAME
    return File("$appDir/$fileName")
}

//获取路径
fun getDir(context: Context): File {
    val pictureFolder = context.externalCacheDir
    val appDir = File("$pictureFolder/${MainActivity.SPLASH_PHOTO_LOCATION}")
    if (!appDir.exists()) {
        appDir.mkdirs()
    }
    return appDir
}

//删除下载的Splash图
fun deleteDir(path: File?) {
    if (null != path) {
        if (!path.exists())
            return
        if (path.isFile) {
            var result = path.delete()
            var tryCount = 0
            while (!result && tryCount++ < 10) {
                System.gc() // 回收资源
                result = path.delete()
            }
        }
        val files = path.listFiles()
        if (null != files) {
            for (i in files.indices) {
                deleteDir(files[i])
            }
        }
        path.delete()
    }
}