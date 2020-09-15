package com.mredrock.cyxbs.qa.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mredrock.cyxbs.common.config.DIR_PHOTO
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.ui.adapter.HackyViewPagerAdapter
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.qa_activity_view_image.*

/**
 * Created by yyfbe, Date on 2020/3/20.
 * 将图片点击放大独立出来，实现左右滑动功能，下载图片
 */
class ViewImageActivity : AppCompatActivity() {
    companion object {
        private const val IMG_RES_PATHS = "imgResPaths"
        private const val POSITION = "position"

        fun activityStart(context: Context, imgResUrls: Array<String>, position: Int) {
            context.startActivity<ViewImageActivity>(IMG_RES_PATHS to imgResUrls, POSITION to position)
        }
    }

    private var imgUrls: Array<String>? = null
    private var position: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_view_image)
        setTheme(R.style.Theme_MaterialComponents)
        setFullScreen()
        imgUrls = intent?.extras?.getStringArray(IMG_RES_PATHS)
        position = intent.getIntExtra(POSITION, 0)
        tv_position.text = "${position + 1}/${imgUrls?.size}"
        if (!imgUrls.isNullOrEmpty()) {
            hc_vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    tv_position.text = "${position + 1}/${imgUrls?.size}"
                }

            })
            hc_vp.adapter = HackyViewPagerAdapter(imgUrls).apply {
                photoTapClick = {
                    finish()
                }
                savePicClick = { bitmap, url ->
                    doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        doAfterGranted {
                            MaterialAlertDialogBuilder(this@ViewImageActivity)
                                    .setTitle(getString(R.string.qa_pic_save_alert_dialog_title))
                                    .setMessage(R.string.qa_pic_save_alert_dialog_message)
                                    .setPositiveButton("确定") { dialog, _ ->
                                        val name = System.currentTimeMillis().toString() + url.split('/').lastIndex.toString()
                                        Schedulers.io().scheduleDirect {
                                            this@ViewImageActivity.saveImage(bitmap, name)
                                            MediaScannerConnection.scanFile(this@ViewImageActivity,
                                                    arrayOf(Environment.getExternalStorageDirectory().toString() + DIR_PHOTO),
                                                    arrayOf("image/jpeg"),
                                                    null)

                                            runOnUiThread {
                                                toast("图片保存于系统\"$DIR_PHOTO\"文件夹下哦")
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
            hc_vp.currentItem = position
        }

    }

}