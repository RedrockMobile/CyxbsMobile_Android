package com.mredrock.cyxbs.freshman.model

import android.annotation.SuppressLint
import android.graphics.Bitmap
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.encrypt.md5Encoding
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.freshman.base.BaseModel
import com.mredrock.cyxbs.freshman.interfaces.model.IActivitySaveQRModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream

/**
 * Create by yuanbing
 * on 2019/8/5
 */
class ActivitySaveQRModel : BaseModel(), IActivitySaveQRModel {
    @SuppressLint("CheckResult")
    override fun saveBitmapToDisk(bitmap: Bitmap, token: String, callback: (File) -> Unit) {
        Observable.just(bitmap)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map {
                    val file = File(BaseApp.context.externalMediaDirs[0]?.absolutePath
                            + File.separator
                            + "QR"
                            + File.separator
                            + md5Encoding(token)
                            + ".png")
                    val parentDir = file.parentFile
                    if (parentDir.exists()) parentDir.delete()
                    parentDir.mkdir()
                    file.createNewFile()
                    val fos = FileOutputStream(file)
                    it.compress(Bitmap.CompressFormat.PNG, 0, fos)
                    fos.flush()
                    fos.close()
                    file
                }
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribeBy { callback(it) }
    }
}