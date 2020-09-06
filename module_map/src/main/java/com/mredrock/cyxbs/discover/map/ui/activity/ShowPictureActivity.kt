package com.mredrock.cyxbs.discover.map.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.doPermissionAction
import com.mredrock.cyxbs.common.utils.extensions.setFullScreen
import com.mredrock.cyxbs.common.utils.extensions.startActivity
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.component.MapToast
import com.mredrock.cyxbs.discover.map.ui.adapter.MyImageAdapter
import com.mredrock.cyxbs.discover.map.widget.MapDialog
import com.mredrock.cyxbs.discover.map.widget.OnSelectListener
import kotlinx.android.synthetic.main.map_activity_show_picture.*
import java.io.File
import java.io.FileOutputStream


class ShowPictureActivity : AppCompatActivity() {

    companion object {
        private const val IMG_RES_PATHS = "imgResPaths"
        private const val POSITION = "position"

        fun activityStart(context: Context?, images: ArrayList<String>, position: Int) {
            context?.startActivity<ShowPictureActivity>(Pair("images", images), Pair("picturePosition", position))
        }
    }

    private var picturePosition = 0
    private val imageData = mutableListOf<String>()
    private lateinit var adapter: MyImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity_show_picture)
        setFullScreen()

        /**
         * 获取点击第几张图片
         */
        picturePosition = intent.getIntExtra("picturePosition", 0)

        adapter = MyImageAdapter(imageData, this)

        /**
         * 单击图片退出
         */
        adapter.setMyOnPhotoClickListener(object : MyImageAdapter.OnPhotoClickListener {
            override fun onPhotoClick() {
                finish()
            }

        })

        /**
         * 长按保存图片
         */
        adapter.setMyOnPhotoLongClickListener(object : MyImageAdapter.OnPhotoLongClickListener {
            override fun onPhotoLongClick(url: String) {
                MapDialog.show(this@ShowPictureActivity
                        , getString(R.string.map_show_picture_save)
                        , getString(R.string.map_show_picture_content)
                        , object : OnSelectListener {
                    override fun onDeny() {
                    }

                    override fun onPositive() {

                        (this@ShowPictureActivity as AppCompatActivity).doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                            doAfterGranted {
                                Glide.with(BaseApp.context).asBitmap().load(url).into(object : SimpleTarget<Bitmap>() {
                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        saveImage(resource)
                                    }
                                }
                                )
                            }
                        }
                    }
                })
            }

        })

        /**
         * 获取图片urlList数据
         */
        val images = intent.getStringArrayListExtra("images")
        if (images != null) {
            imageData.clear()
            imageData.addAll(images)
            adapter.setList(images)
            adapter.notifyDataSetChanged()
        }
        map_vp_show_picture.adapter = adapter

        /**
         * 设置当前展示的是点击的图片
         */
        map_vp_show_picture.setCurrentItem(picturePosition, false)

        /** 图片上方页数 */
        val s = "${picturePosition + 1}/${imageData.size}"
        map_tv_show_picture.text = s
        map_vp_show_picture.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                picturePosition = position
                val content = "${picturePosition + 1}/${imageData.size}"
                map_tv_show_picture.text = content
            }
        })
    }

    private fun saveImage(resource: Bitmap) {
        val file = File(BaseApp.context.externalMediaDirs[0]?.absolutePath
                + File.separator
                + "Map"
                + File.separator
                + System.currentTimeMillis()
                + ".jpg")
        val parentDir = file.parentFile
        if (parentDir.exists()) parentDir.delete()
        parentDir.mkdir()
        file.createNewFile()
        val fos = FileOutputStream(file)
        resource.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
        galleryAddPic(file.path)
        MapToast.makeText(this, "图片已保存在" + file.absolutePath, Toast.LENGTH_LONG).show()
    }

    //更新相册
    private fun galleryAddPic(imagePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        BaseApp.context.sendBroadcast(mediaScanIntent)
    }

}