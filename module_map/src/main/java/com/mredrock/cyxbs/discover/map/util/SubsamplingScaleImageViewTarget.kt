package com.mredrock.cyxbs.discover.map.util

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.component.MapToast
import com.mredrock.cyxbs.discover.map.model.DataSet
import com.mredrock.cyxbs.discover.map.widget.GlideProgressDialog
import com.mredrock.cyxbs.discover.map.widget.MapDialogTips
import com.mredrock.cyxbs.discover.map.widget.OnSelectListenerTips
import com.mredrock.cyxbs.discover.map.widget.ProgressInterceptor
import java.io.File

class SubsamplingScaleImageViewTarget(val context: Context, view: SubsamplingScaleImageView, val url: String)
    : CustomViewTarget<SubsamplingScaleImageView, File>(view) {
    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        view.setImage(ImageSource.uri(Uri.fromFile(resource)))
        GlideProgressDialog.hide()
        ProgressInterceptor.removeListener(url)
        DataSet.savePath(resource.absolutePath)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        GlideProgressDialog.hide()
        val path = DataSet.getPath()
        if (path != null && fileIsExists(path)) {
            view.setImage(ImageSource.uri(Uri.fromFile(File(path))))
            MapToast.makeText(context, BaseApp.context.getString(R.string.map_map_load_failed), Toast.LENGTH_SHORT).show()
        } else {
            MapDialogTips.show(context, context.getString(R.string.map_map_load_failed_title_tip)
                    , context.getString(R.string.map_map_load_failed_message_tip)
                    , false, object : OnSelectListenerTips {
                override fun onPositive() {
                    val activity = context as Activity
                    activity.finish()
                }
            })
        }
    }

    override fun onResourceCleared(placeholder: Drawable?) {
        // Ignore
    }

    override fun onResourceLoading(placeholder: Drawable?) {
        super.onResourceLoading(placeholder)
    }

    /**
     * 判断文件是否存在
     */
    private fun fileIsExists(strFile: String?): Boolean {
        try {
            if (!File(strFile).exists()) {
                return false
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }
}