package com.mredrock.cyxbs.discover.map.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.mredrock.cyxbs.discover.map.util.SubsamplingScaleImageViewShowPictureTarget
import com.mredrock.cyxbs.common.utils.extensions.*



class MyImageAdapter(private val imageUrls: MutableList<String>?
                     , private val context: Context
) : PagerAdapter() {

    /**
     * 单击图片回调
     */
    private var onPhotoClickListener: OnPhotoClickListener? = null

    /**
     * 长按图片回调
     */
    private var onPhotoLongClickListener: OnPhotoLongClickListener? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val url = imageUrls?.get(position)
        val subsamplingScaleImageView = SubsamplingScaleImageView(context)
        subsamplingScaleImageView.setDoubleTapZoomScale(1f)
        Glide.with(context)
                .download(GlideUrl(url))
                .into(SubsamplingScaleImageViewShowPictureTarget(context, subsamplingScaleImageView))
        subsamplingScaleImageView.setOnSingleClickListener {
            onPhotoClickListener?.onPhotoClick()
        }
        subsamplingScaleImageView.setOnLongClickListener {

            onPhotoLongClickListener?.onPhotoLongClick(url ?: "")

            true
        }
        container.addView(subsamplingScaleImageView)
        return subsamplingScaleImageView
    }

    override fun getCount(): Int {
        return imageUrls?.size ?: 0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    fun setList(list: List<String>) {
        if (imageUrls != null) {
            imageUrls.clear()
            imageUrls.addAll(list)
        }
        notifyDataSetChanged()
    }

    interface OnPhotoClickListener {
        fun onPhotoClick()
    }

    interface OnPhotoLongClickListener {
        fun onPhotoLongClick(url: String)
    }

    fun setMyOnPhotoClickListener(onPhotoClickListener: OnPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener
    }

    fun setMyOnPhotoLongClickListener(onPhotoLongClickListener: OnPhotoLongClickListener) {
        this.onPhotoLongClickListener = onPhotoLongClickListener
    }
}