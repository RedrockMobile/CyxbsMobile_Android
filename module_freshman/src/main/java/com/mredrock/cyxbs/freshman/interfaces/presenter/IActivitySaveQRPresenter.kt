package com.mredrock.cyxbs.freshman.interfaces.presenter

import android.graphics.Bitmap
import com.mredrock.cyxbs.freshman.base.IBasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IActivitySaveQRModel
import com.mredrock.cyxbs.freshman.interfaces.view.IActivitySaveQRView

/**
 * Create by yuanbing
 * on 2019/8/5
 */
interface IActivitySaveQRPresenter : IBasePresenter<IActivitySaveQRView, IActivitySaveQRModel> {
    fun saveBitmapToDisk(bitmap: Bitmap, token: String)
}