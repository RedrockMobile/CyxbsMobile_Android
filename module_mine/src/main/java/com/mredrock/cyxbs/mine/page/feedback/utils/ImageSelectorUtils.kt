package com.mredrock.cyxbs.mine.page.feedback.utils

import android.Manifest
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.common.utils.extensions.longToast
import com.mredrock.cyxbs.mine.R
import top.limuyang2.photolibrary.LPhotoHelper
import top.limuyang2.photolibrary.util.LPPImageType

/**
 *@author ZhiQiang Tu
 *@time 2021/8/25  13:31
 *@signature 我们不明前路，却已在路上
 */
const val CHOOSE_FEED_BACK_PIC = 0x101010
private var selectedPics: ArrayList<Uri>? = null
fun AppCompatActivity.selectImageFromAlbum(maxCount: Int) {
    doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
        doAfterGranted {
            LPhotoHelper.Builder()
                .maxChooseCount(maxCount) //最多选几个
                .selectedPhotos(selectedPics)
                .columnsNumber(3) //每行显示几列图片
                .imageType(LPPImageType.ofAll()) // 文件类型
                .pauseOnScroll(false) // 是否滑动暂停加载图片显示
                .isSingleChoose(false) // 是否是单选
                .isOpenLastAlbum(false) // 是否直接打开最后一次选择的相册
                .theme(R.style.common_LPhotoTheme)
                .build()
                .start(this@selectImageFromAlbum, CHOOSE_FEED_BACK_PIC)
        }
        doAfterRefused {
            longToast("访问相册失败，原因：未授权")
        }
    }
}

fun setSelectedPhotos(list: java.util.ArrayList<Uri>?) {
    selectedPics = list
}
