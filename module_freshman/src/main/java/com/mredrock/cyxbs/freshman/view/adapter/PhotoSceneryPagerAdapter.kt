package com.mredrock.cyxbs.freshman.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * Create by roger
 * on 2019/8/6
 */
class PhotoSceneryPagerAdapter(private val urls: List<String>, private val  mContext: Context) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return urls.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val options = RequestOptions().centerCrop()
        val url = urls[position]
        val itemView = ImageView(mContext)
        Glide.with(mContext).load(url).apply(options).into(itemView)
        (container as ViewPager).addView(itemView)
        return itemView
    }
}