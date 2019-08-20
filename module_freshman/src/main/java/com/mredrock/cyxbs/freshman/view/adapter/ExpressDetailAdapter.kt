package com.mredrock.cyxbs.freshman.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.ExpressMessage
import com.mredrock.cyxbs.freshman.view.activity.showPhotosToScenery

class ExpressDetailAdapter(val data: List<ExpressMessage>) : RecyclerView.Adapter<ExpressDetailViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpressDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.freshman_recycle_item_campus_guidelines_single_photo, parent, false)
        return ExpressDetailViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ExpressDetailViewHolder, position: Int) {
        val message = data[position]
        holder.mPhoto.setImageFromUrl(message.photo)
        holder.mPhoto.setOnClickListener {
            showPhotosToScenery(holder.itemView.context, listOf(message.photo))
        }
        holder.mPlace.text = message.title
        holder.mDescription.text = message.detail
    }
}

class ExpressDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mPhoto: ImageView = view.findViewById(R.id.iv_recycle_item_campus_guidelines_single_photo_photo)
    val mPlace: TextView = view.findViewById(R.id.tv_recycle_item_campus_guidelines_single_photo_place)
    val mDescription: TextView = view.findViewById(R.id.tv_recycle_item_campus_guidelines_single_photo_description)
}