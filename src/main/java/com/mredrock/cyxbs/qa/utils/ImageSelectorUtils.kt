package com.mredrock.cyxbs.qa.utils

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.mredrock.cyxbs.common.component.multi_image_selector.MultiImageSelectorActivity
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import org.jetbrains.anko.longToast

/**
 * Created By jay68 on 2018/10/24.
 */


fun AppCompatActivity.selectImageFromAlbum(maxCount: Int, selected: ArrayList<String>?) {
    doPermissionAction(Manifest.permission.READ_EXTERNAL_STORAGE) {
        doAfterGranted {
            val intent = Intent(this@selectImageFromAlbum, MultiImageSelectorActivity::class.java)
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true)
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxCount)
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI)

            if (!selected.isNullOrEmpty())
                intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, selected)

            startActivityForResult(intent, MultiImageSelectorActivity.CHOOSE_REQUEST)
        }

        doAfterRefused {
            longToast("访问相册失败，原因：未授权")
        }
    }
}