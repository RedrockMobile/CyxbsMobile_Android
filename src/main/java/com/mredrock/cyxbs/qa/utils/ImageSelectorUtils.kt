package com.mredrock.cyxbs.qa.utils

import android.Manifest
import android.support.v7.app.AppCompatActivity
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import org.jetbrains.anko.longToast

/**
 * Created By jay68 on 2018/10/24.
 */


fun AppCompatActivity.selectImageFromAlbum(maxCount: Int, selected: List<LocalMedia>?) {
    doPermissionAction(Manifest.permission.READ_EXTERNAL_STORAGE) {
        doAfterGranted {
            PictureSelector.create(this@selectImageFromAlbum)
                    .openGallery(PictureMimeType.ofImage())
                    .isCamera(false)
                    .maxSelectNum(maxCount)
                    .previewImage(true)
                    .enableCrop(false)
                    .compress(true)
                    .cropCompressQuality(85)
                    .minimumCompressSize(200)
                    .selectionMedia(selected ?: emptyList())
                    .forResult(PictureConfig.CHOOSE_REQUEST)
        }

        doAfterRefused {
            longToast("访问相册失败，原因：未授权")
        }
    }
}

fun AppCompatActivity.selectImageFromCamera() {
    doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA) {
        doAfterGranted {
            PictureSelector.create(this@selectImageFromCamera)
                    .openCamera(PictureMimeType.ofImage())
                    .compress(true)
                    .cropCompressQuality(85)
                    .forResult(PictureConfig.REQUEST_CAMERA)
        }

        doAfterRefused {
            longToast("访问相册失败，原因：未授权")
        }
    }
}