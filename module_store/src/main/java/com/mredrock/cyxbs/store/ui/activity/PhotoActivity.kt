package com.mredrock.cyxbs.store.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.github.chrisbanes.photoview.PhotoView
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.utils.widget.slideshow.SlideShow
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/9 15:10
 */
class PhotoActivity : AppCompatActivity() {

    private lateinit var mImgUrls: ArrayList<String>

    companion object {

        private const val INTENT_IMG_URLS = "imgUrls"

        // 加载时或退出时图片显示的位置(如果使用 startActivityForResult(),
        // 则会在共享动画时因回调过慢在图片位置不对应时出现图片闪动问题)
        var SHOW_POSITION = 0
            private set

        fun activityStart(context: Context, imgUrls: ArrayList<String>, showPosition: Int, options: Bundle?) {
            SHOW_POSITION = showPosition
            val intent = Intent(context, PhotoActivity::class.java)
            intent.putStringArrayListExtra(INTENT_IMG_URLS, imgUrls)
            context.startActivity(intent, options)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 降低因使用共享动画进入 activity 后的闪眼情况
        window.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(R.layout.store_activity_photo)
        setTheme(com.google.android.material.R.style.Theme_MaterialComponents) // 因为学长用的奇怪的 dialog, 需要这个主题支持
        setFullScreen()

        initData()
        initView()
    }

    private fun initData() {
        val imgUrls = intent.getStringArrayListExtra(INTENT_IMG_URLS)
        if (imgUrls != null ) {
            mImgUrls = imgUrls
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val tvPosition: TextView = findViewById(R.id.store_tv_photo_position)

        val slideShow: SlideShow = findViewById(R.id.store_slideShow_photo)
        slideShow
            .setStartItem(SHOW_POSITION)
            .setPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    //设置图片进度(1/X)
                    tvPosition.post { // TextView 有奇怪的 bug, 改变文字不用 post 就无法改变
                        tvPosition.text = "${position + 1}/${mImgUrls.size}"
                    }
                    SHOW_POSITION = position
                }
            })
            .setViewAdapter(
                getNewView = { context -> PhotoView(context) },
                getItemCount = { mImgUrls.size },
                create = { holder ->
                    holder.view.scaleType= ImageView.ScaleType.CENTER_INSIDE
                    holder.view.setOnPhotoTapListener { _, _, _ ->
                        finishAfterTransition()
                    }
                    holder.view.setOnOutsidePhotoTapListener {
                        finishAfterTransition()
                    }
                    holder.view.setOnLongClickListener {
                        val drawable = holder.view.drawable
                        if (drawable is BitmapDrawable) {
                            val bitmap = drawable.bitmap
                            savePhoto(bitmap, mImgUrls[holder.layoutPosition])
                        }
                        true
                    }
                },
                onBindViewHolder = { view, _, position, _ ->
                    view.setImageFromUrl(mImgUrls[position])
                }
            )
    }

    //对图片保存的处理是照搬 邮问 ViewImageActivity
    private fun savePhoto(bitmap: Bitmap, url: String) {
        doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            doAfterGranted {
                com.google.android.material.dialog.MaterialAlertDialogBuilder(this@PhotoActivity)
                    .setTitle(getString(R.string.store_pic_save_alert_dialog_title))
                    .setMessage(R.string.store_pic_save_alert_dialog_message)
                    .setPositiveButton("确定") { dialog, _ ->
                        val name = "${System.currentTimeMillis()}${url.split('/').lastIndex}"
                        Schedulers.io()
                            .scheduleDirect {
                                this@PhotoActivity.saveImage(bitmap, name)
                                android.media.MediaScannerConnection.scanFile(
                                    this@PhotoActivity,
                                    arrayOf(
                                        "${android.os.Environment.getExternalStorageDirectory()}" +
                                                com.mredrock.cyxbs.common.config.DIR_PHOTO
                                    ),
                                    arrayOf("image/jpeg"),
                                    null
                                )

                            runOnUiThread {
                                toast("图片保存于系统\"${com.mredrock.cyxbs.common.config.DIR_PHOTO}\"文件夹下哦")
                                dialog.dismiss()
                                setFullScreen()
                            }

                        }
                    }
                    .setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                        setFullScreen()
                    }
                    .show()
            }
        }
    }
}