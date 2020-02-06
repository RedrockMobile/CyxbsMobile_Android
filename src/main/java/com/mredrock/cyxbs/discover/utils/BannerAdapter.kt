package com.mredrock.cyxbs.discover.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.network.RollerViewInfo
import com.mredrock.cyxbs.discover.pages.RollerViewActivity
import kotlinx.android.synthetic.main.discover_viewpager_item.view.*
import org.jetbrains.anko.dip

/**
 * @author zixuan
 * 2019/11/20
 *
 */
class BannerAdapter(private val context: Context, private val urlList: List<RollerViewInfo>, private val viewPager: ViewPager2) : Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.discover_viewpager_item, parent, false))
    }

    override fun getItemCount(): Int {
        return viewPager.currentItem + 2
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val picUrl = urlList[position % urlList.size].picture_url
        Glide.with(context)
                .load(picUrl)
                .apply(RequestOptions()
                        .placeholder(R.drawable.discover_ic_cyxbsv6)
                        .transform(MultiTransformation(CenterCrop(), RoundedCorners(context.dip(8))))
                )
                .into(holder.itemView.iv_viewpager_item)
        val targetUrl = urlList[position % urlList.size].picture_goto_url
        targetUrl ?: return
        if (targetUrl.startsWith("http")) {
            holder.itemView.setOnClickListener {
                RollerViewActivity.startRollerViewActivity(urlList[position % urlList.size], holder.itemView.context)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}