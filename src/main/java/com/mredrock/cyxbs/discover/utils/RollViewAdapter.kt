package com.mredrock.cyxbs.discover.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.discover.network.RollerViewInfo
import com.mredrock.cyxbs.discover.pages.RollerViewActivity
import java.util.*

/**
 * Created by zxzhu
 *   2018/9/7.
 *   enjoy it !!
 */
class RollViewAdapter : RollerView.RollerViewAdapter {
    private var mImageViews: MutableList<MyImageView>? = null

    override val itemCount: Int
        get() = mImageViews!!.size

    constructor(context: Context, images: IntArray) {
//        mImageViews = ArrayList()
        for (id in images) {
            val imageView = MyImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imageView.setRoundHeight(13)
            imageView.setRoundWidth(13)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(context).load(id).into(imageView)
            mImageViews!!.add(imageView)
        }
    }

    constructor(context: Context, urlList: List<RollerViewInfo>?) {
        mImageViews = ArrayList()
        if (urlList == null) return
        for (url in urlList) {
            val imageView = MyImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imageView.setRoundHeight(13)
            imageView.setRoundWidth(13)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            url.picture_goto_url?.let {
                if (it.startsWith("http")) {
                    imageView.setOnClickListener { RollerViewActivity.startRollerViewActivity(url, context) }
                 }
            }

            imageView.setImageFromUrl(url.picture_url)
            mImageViews!!.add(imageView)
        }
    }

    override fun getView(container: ViewGroup, position: Int): View {
        return mImageViews!![position]
    }

}
