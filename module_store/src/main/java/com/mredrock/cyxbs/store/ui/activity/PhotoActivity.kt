package com.mredrock.cyxbs.store.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.util.SparseArray
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.WindowCompat
import com.github.chrisbanes.photoview.PhotoView
import com.mredrock.cyxbs.config.dir.DIR_PHOTO
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.lib.utils.extensions.saveImage
import com.mredrock.cyxbs.lib.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.store.R
import com.ndhzs.slideshow.SlideShow
import com.ndhzs.slideshow.adapter.ViewAdapter
import com.ndhzs.slideshow.adapter.setViewAdapter
import com.ndhzs.slideshow.utils.OnPageChangeCallback
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/9 15:10
 */
class PhotoActivity : BaseActivity() {
  
  companion object {
    
    // 加载时或退出时图片显示的位置(如果使用 startActivityForResult(),
    // 则会在共享动画时因回调过慢在图片位置不对应时出现图片闪动问题)
    private val PositionList = SparseArray<Position>()
    
    fun activityStart(
      context: Context,
      imgUrls: ArrayList<String>,
      showPosition: Int,
      options: Bundle?
    ): Position {
      val intent = Intent(context, PhotoActivity::class.java)
      intent.putExtra(PhotoActivity::mImgUrls.name, imgUrls)
      context.startActivity(intent, options)
      val position = Position(showPosition)
      PositionList.put(imgUrls.hashCode(), position)
      return position
    }
    
    data class Position(var value: Int)
  }
  
  private val mImgUrls by intent<ArrayList<String>>()
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // 降低因使用共享动画进入 activity 后的闪眼情况
    window.setBackgroundDrawableResource(android.R.color.transparent)
    setContentView(R.layout.store_activity_photo)
    setTheme(com.google.android.material.R.style.Theme_MaterialComponents) // 因为学长用的奇怪的 dialog, 需要这个主题支持
    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsController.isAppearanceLightStatusBars = false // 设置状态栏字体颜色为白色
    initView()
  }
  
  @SuppressLint("SetTextI18n")
  private fun initView() {
    val tvPosition: TextView = findViewById(R.id.store_tv_photo_position)
    val pos = PositionList.get(mImgUrls.hashCode())
    val slideShow: SlideShow = findViewById(R.id.store_slideShow_photo)
    slideShow.setCurrentItem(pos.value)
      .addPageChangeCallback(
        object : OnPageChangeCallback {
          override fun onPageSelected(position: Int) {
            //设置图片进度(1/X)
            tvPosition.post { // TextView 有奇怪的 bug, 改变文字不用 post 就无法改变
              tvPosition.text = "${position + 1}/${mImgUrls.size}"
            }
            pos.value = position
          }
        }
      )
      .setViewAdapter(
        ViewAdapter.Builder(mImgUrls) {
          PhotoView(context)
        }.onCreate {
          view.scaleType = ImageView.ScaleType.CENTER_INSIDE
          view.setOnPhotoTapListener { _, _, _ ->
            finishAfterTransition()
          }
          view.setOnOutsidePhotoTapListener {
            finishAfterTransition()
          }
          view.setOnLongClickListener {
            val drawable = view.drawable
            if (drawable is BitmapDrawable) {
              val bitmap = drawable.bitmap
              savePhoto(bitmap, data)
            }
            true
          }
        }.onBind {
          view.setImageFromUrl(data)
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
                    "${Environment.getExternalStorageDirectory()}" +
                      DIR_PHOTO
                  ),
                  arrayOf("image/jpeg"),
                  null
                )
                
                runOnUiThread {
                  toast("图片保存于${Environment.DIRECTORY_PICTURES}${DIR_PHOTO}文件夹下哦")
                  dialog.dismiss()
                }
                
              }
          }
          .setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
          }
          .show()
      }
    }
  }
}