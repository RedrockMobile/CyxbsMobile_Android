package com.mredrock.cyxbs.discover.map.util

import android.app.ProgressDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.component.MapToast
import com.mredrock.cyxbs.discover.map.widget.ProgressInterceptor
import java.io.File

class SubsamplingScaleImageViewShowPictureTarget(val context: Context, view: SubsamplingScaleImageView, val dialog: ProgressDialog, val url: String)
    : CustomViewTarget<SubsamplingScaleImageView, File>(view) {
    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        view.setImage(ImageSource.uri(Uri.fromFile(resource)))
        dialog.dismiss()
        ProgressInterceptor.removeListener(url)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        dialog.dismiss()
        MapToast.makeText(context, context.getText(R.string.map_show_picture_fail), Toast.LENGTH_SHORT).show()
    }

    override fun onResourceCleared(placeholder: Drawable?) {
        // Ignore
    }

    override fun onResourceLoading(placeholder: Drawable?) {
        super.onResourceLoading(placeholder)
    }
}