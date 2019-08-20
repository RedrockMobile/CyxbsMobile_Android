package com.mredrock.cyxbs.freshman.interfaces.model

import android.graphics.Bitmap
import com.mredrock.cyxbs.freshman.base.IBaseModel
import java.io.File

/**
 * Create by yuanbing
 * on 2019/8/5
 */
interface IActivitySaveQRModel : IBaseModel {
    fun saveBitmapToDisk(bitmap: Bitmap, token: String, callback: (File) -> Unit)
}