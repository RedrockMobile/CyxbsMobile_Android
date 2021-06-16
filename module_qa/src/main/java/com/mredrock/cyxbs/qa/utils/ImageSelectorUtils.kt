package com.mredrock.cyxbs.qa.utils

import android.Manifest
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.common.utils.extensions.longToast
import com.mredrock.cyxbs.qa.R
import top.limuyang2.photolibrary.LPhotoHelper
import top.limuyang2.photolibrary.util.LPPImageType

/**
 * Created By jay68 on 2018/10/24.
 */

const val CHOOSE_PHOTO_REQUEST = 0x1024

fun AppCompatActivity.selectImageFromAlbum(maxCount: Int) {
    doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
        doAfterGranted {
            LPhotoHelper.Builder()
                    .maxChooseCount(maxCount) //最多选几个
                    .columnsNumber(3) //每行显示几列图片
                    .imageType(LPPImageType.ofAll()) // 文件类型
                    .pauseOnScroll(false) // 是否滑动暂停加载图片显示
                    .isSingleChoose(false) // 是否是单选
                    .isOpenLastAlbum(false) // 是否直接打开最后一次选择的相册
                    .selectedPhotos(ArrayList())
                    .theme(R.style.common_LPhotoTheme)
                    .build()
                    .start(this@selectImageFromAlbum, CHOOSE_PHOTO_REQUEST)
        }
        doAfterRefused {
            longToast("访问相册失败，原因：未授权")
        }
    }
}