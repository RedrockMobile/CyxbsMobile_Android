package com.mredrock.cyxbs.freshman.interfaces.view

import com.mredrock.cyxbs.freshman.base.IBaseView
import java.io.File

/**
 * Create by yuanbing
 * on 2019/8/5
 */
interface IActivitySaveQRView : IBaseView {
    fun saveSuccess(file: File)
}