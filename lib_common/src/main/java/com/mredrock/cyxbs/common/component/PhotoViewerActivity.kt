package com.mredrock.cyxbs.common.component

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.setFullScreen
import com.mredrock.cyxbs.common.utils.extensions.startActivity
import kotlinx.android.synthetic.main.common_activity_photo_viewer.*
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener

/**
 * Create By Hosigus at 2019/8/7
 */
fun showPhotos(context: Context, photoList: List<String>, pos: Int = 0) {
    context.startActivity<PhotoViewerActivity>("photos" to photoList.toTypedArray(), "position" to pos)
}

open class PhotoViewerActivity : BaseActivity() {


    protected lateinit var mImgManager: RequestManager
    protected lateinit var mAdapter: PagerAdapter

    protected open val backgroundColor: Int = Color.BLACK
    protected open val glideOption: RequestOptions? = null
    protected open val thumbnail: Float = 0.1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_activity_photo_viewer)

        val list = intent.extras?.getStringArray("photos")?.toList()
        val curPos = intent.extras?.getInt("position")
        if (list == null || curPos == null) {
            finish()
            return
        }
        mImgManager = Glide.with(this)
        glideOption?.let {
            mImgManager.setDefaultRequestOptions(it)
        }

        mAdapter = object : BasePagerAdapter<PhotoView, String>(list) {
            override fun createView(context: Context) = PhotoView(context)
            override fun PhotoView.initView(mData: String, mPos: Int) {
                //应对教务在线使用base64的图片，暂时想到这样解决，有其他方式改改
                if (mData.startsWith("data:image/png;base64")) {
                    val source = mData.removePrefix("data:image/png;base64")
                    try {
                        val bitmapArray = Base64.decode(source, Base64.DEFAULT)
                        mImgManager.load(BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.size)).thumbnail(thumbnail).into(this)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    mImgManager.load(mData).thumbnail(thumbnail).into(this)
                }
                setOnSingleClickListener { onPhotoClick(mData, mPos) }
                setOnLongClickListener { onLongClick(mData, mPos) }
            }
        }

        vp_photo.apply {
            adapter = mAdapter
            currentItem = curPos
            setBackgroundColor(backgroundColor)
        }

        setFullScreen()
    }

    protected open fun onPhotoClick(mData: String, mPos: Int) {}
    protected open fun onLongClick(mData: String, mPos: Int) = false

    protected fun addFloatView(@LayoutRes floatLayoutId: Int): View =
            LayoutInflater.from(this).inflate(floatLayoutId, fl_photo_content, true)
}
