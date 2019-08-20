package com.mredrock.cyxbs.freshman.view.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.chrisbanes.photoview.PhotoView
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.component.BasePagerAdapter
import com.mredrock.cyxbs.common.component.PhotoViewerActivity
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.freshman.R
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.startActivity

/**
 * Create by roger
 * on 2019/8/7
 */
fun showPhotosToMap(context: Context, photoList: List<String>, pos: Int = 0) {
    context.startActivity<PhotoMapActivity>("photos" to photoList.toTypedArray(), "position" to pos)
}

class PhotoMapActivity : PhotoViewerActivity() {


    private var btnDownload: ImageView? = null
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addFloatView(R.layout.freshman_activity_photo_map)
        url = intent.extras?.getStringArray("photos")?.toList()?.get(0)
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            btnDownload = find(R.id.iv_download) as ImageView
            btnDownload?.setOnClickListener {
                (this as AppCompatActivity).doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    doAfterGranted {
                        Glide.with(context).asBitmap().load(url).into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                com.mredrock.cyxbs.freshman.view.widget.saveImage(resource)
                            }
                        }
                        )
                    }
                }
            }
        }

    }

    override fun onLongClick(mData: String, mPos: Int): Boolean {
        val intent = Intent(this@PhotoMapActivity, ChooseSaveQrActivity::class.java)
        startActivityForResult(intent, 0)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            0 -> if (resultCode == Activity.RESULT_OK) {
                doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    Glide.with(context).asBitmap().load(url).into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            com.mredrock.cyxbs.freshman.view.widget.saveImage(resource)
                        }
                    }
                    )
                }
            }
        }
    }
}
