package com.mredrock.cyxbs.freshman.view.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.chrisbanes.photoview.PhotoView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.BasePagerAdapter
import com.mredrock.cyxbs.common.component.PhotoViewerActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.encrypt.md5Encoding
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.view.widget.saveImage
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Create by roger
 * on 2019/8/6
 */
fun showPhotosToScenery(context: Context, photoList: List<String>, pos: Int = 0) {
    context.startActivity<PhotoSceneryActivity>("photos" to photoList.toTypedArray(), "position" to pos)
}

class PhotoSceneryActivity : PhotoViewerActivity() {
    private lateinit var list2: List<String>
    private var curPos2: Int = 0
    private lateinit var viewPager: ViewPager
    private var btnDownload: ImageView? = null
    private lateinit var tvPos: TextView
    private var tvVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addFloatView(R.layout.freshman_activity_photo_scenery)
        viewPager = find(R.id.vp_photo)
        list2 = intent.extras?.getStringArray("photos")?.toList()!!
        curPos2 = intent.extras?.getInt("position")!!
        tvPos = find(R.id.tv_photo_scenery)
        if (list2.size > 1) {
            tvPos.text = StringBuilder().append(curPos2 + 1).append("/").append(list2.size)
        } else {
            tvPos.visibility = View.GONE
            tvVisible = false
        }
        btnDownload = find(R.id.iv_download_scenery) as ImageView
        btnDownload?.setOnClickListener {
            (this as AppCompatActivity).doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                doAfterGranted {
                    Glide.with(BaseApp.context).asBitmap().load(list2[curPos2]).into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            saveImage(resource)
                        }
                    }
                    )
                }
            }
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageSelected(position: Int) {
                curPos2 = position
                if (tvVisible) {
                    tvPos.text = StringBuilder().append(curPos2 + 1).append("/").append(list2.size)
                }

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
        })
    }


    override fun onLongClick(mData: String, mPos: Int): Boolean {
        val intent = Intent(this@PhotoSceneryActivity, ChooseSaveQrActivity::class.java)
        startActivityForResult(intent, 0)
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            0 -> if (resultCode == Activity.RESULT_OK) {
                doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    Glide.with(BaseApp.context).asBitmap().load(list2[curPos2]).into(object : SimpleTarget<Bitmap>() {
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