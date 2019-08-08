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
import com.github.chrisbanes.photoview.PhotoView
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.ui.BaseActivity
import kotlinx.android.synthetic.main.common_activity_photo_viewer.*
import org.jetbrains.anko.startActivity

/**
 * Create By Hosigus at 2019/8/7
 */
fun start(context: Context, photoList: List<String>, pos: Int = 0) {
    context.startActivity<PhotoViewerActivity>("photos" to photoList.toTypedArray(), "position" to pos)
}

open class PhotoViewerActivity : BaseActivity() {
    override val isFragmentActivity = false

    protected lateinit var g: RequestManager
    protected lateinit var adapter: PagerAdapter

    protected open val backgroundColor: Int = Color.BLACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_activity_photo_viewer)

        val list = intent.extras?.getStringArray("photos")?.toList()
        val curPos = intent.extras?.getInt("position")
        if (list == null || curPos == null) {
            finish()
            return
        }
        g = Glide.with(this)

        adapter = object : BasePagerAdapter<PhotoView, String>(list) {
            override fun createView(context: Context) = PhotoView(context)
            override fun PhotoView.initView(mData: String, mPos: Int) {
                g.load(mData).thumbnail(0.1f).into(this)
                setOnClickListener { onPhotoClick(mData, mPos) }
            }
        }

        vp_photo.apply {
            adapter = this.adapter
            currentItem = curPos
            setBackgroundColor(backgroundColor)
        }

        setFullScreen()
    }

    private fun setFullScreen() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    protected open fun onPhotoClick(mData: String, mPos: Int) {}

    protected fun addFloatView(@LayoutRes floatLayoutId: Int): View =
            LayoutInflater.from(this).inflate(floatLayoutId, fl_photo_content, true)
}
