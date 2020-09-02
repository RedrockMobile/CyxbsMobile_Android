package com.mredrock.cyxbs.qa.ui.adapter

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.github.chrisbanes.photoview.PhotoView
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.qa.R

/**
 * Created by yyfbe, Date on 2020/3/20.
 */
class HackyViewPagerAdapter(private val picUrls: Array<String>?) : PagerAdapter() {
    var savePicClick: ((Bitmap, String) -> Unit)? = null
    var photoTapClick: (() -> Unit)? = null
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return picUrls?.size ?: 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val root = LayoutInflater.from(container.context).inflate(R.layout.qa_view_pager_item_view_image, container, false)
        val photoView = root.findViewById<PhotoView>(R.id.pv_view_image)
        val url = picUrls?.get(position)
        photoView.setImageFromUrl(url)
        photoView.setOnLongClickListener {
            val drawable = photoView.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = (photoView.drawable as BitmapDrawable).bitmap
                if (!url.isNullOrEmpty())
                    savePicClick?.invoke(bitmap, url)
            }
            true
        }
        photoView.setOnPhotoTapListener { view, x, y ->
            photoTapClick?.invoke()
        }
        container.addView(root)
        return root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}