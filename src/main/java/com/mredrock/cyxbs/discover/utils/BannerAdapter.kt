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
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.network.RollerViewInfo
import kotlinx.android.synthetic.main.discover_viewpager_item.view.*


class BannerAdapter(private val context:Context, private val urlList: List<RollerViewInfo>,val viewPager: ViewPager2) : Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.discover_viewpager_item,parent,false))
    }

    override fun getItemCount(): Int {
        LogUtils.d("MyTag getItemCount","${viewPager.currentItem}")
        return viewPager.currentItem + 2
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        Glide.with(context)
                .load(urlList[position%urlList.size].picture_url)
                .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), RoundedCorners(20))))
                .into(holder.itemView.img_viewpager_item)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}