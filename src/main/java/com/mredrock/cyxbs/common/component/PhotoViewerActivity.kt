package com.mredrock.cyxbs.common.component

import android.content.Context
import android.graphics.Color
import android.os.Bundle
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
import kotlinx.android.synthetic.main.common_activity_photo_viewer.*
import org.jetbrains.anko.startActivity

/**
 * Create By Hosigus at 2019/8/7
 */
fun showPhotos(context: Context, photoList: List<String>, pos: Int = 0) {
    context.startActivity<PhotoViewerActivity>("photos" to photoList.toTypedArray(), "position" to pos)
}

open class PhotoViewerActivity : BaseActivity() {
    override val isFragmentActivity = false

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
                mImgManager.load(mData).thumbnail(thumbnail).into(this)
                setOnClickListener { onPhotoClick(mData, mPos) }
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
