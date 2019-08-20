package com.mredrock.cyxbs.freshman.presenter

import android.graphics.Bitmap
import com.mredrock.cyxbs.freshman.base.BasePresenter
import com.mredrock.cyxbs.freshman.interfaces.model.IActivitySaveQRModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IActivitySaveQRPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IActivitySaveQRView
import com.mredrock.cyxbs.freshman.model.ActivitySaveQRModel

/**
 * Create by yuanbing
 * on 2019/8/5
 */
class ActivitySaveQRPresenter : BasePresenter<IActivitySaveQRView, IActivitySaveQRModel>(),
        IActivitySaveQRPresenter {
    override fun attachModel() = ActivitySaveQRModel()

    override fun saveBitmapToDisk(bitmap: Bitmap, token: String) {
        model?.saveBitmapToDisk(bitmap, token) { view?.saveSuccess(it) }
    }
}