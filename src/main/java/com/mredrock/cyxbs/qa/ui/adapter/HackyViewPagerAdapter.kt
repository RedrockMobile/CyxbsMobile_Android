package com.mredrock.cyxbs.qa.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.mredrock.cyxbs.qa.R

/**
 * Created by yyfbe, Date on 2020/3/20.
 */
class HackyViewPagerAdapter(private val picUrls: Array<String>?) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return picUrls?.size ?: 0
    }

    @SuppressLint("SetTextI18n")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val root = LayoutInflater.from(container.context).inflate(R.layout.qa_view_pager_item_view_image, container, false)
        val photoView = root.findViewById<PhotoView>(R.id.pv_view_image)
        root.findViewById<TextView>(R.id.tv_position).apply {
            text = "${position + 1}/${picUrls?.size}"
        }
        Glide.with(container.context)
                .load(picUrls?.get(position))
                .apply(RequestOptions().placeholder(com.mredrock.cyxbs.common.R.drawable.common_place_holder)
                        .error(com.mredrock.cyxbs.common.R.drawable.common_place_holder))
                .into(photoView)
        container.addView(root)
        return root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}