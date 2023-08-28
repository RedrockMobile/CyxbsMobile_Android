package com.mredrock.cyxbs.ufield.lxh.adapter

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.gyd.ui.DetailActivity
import com.mredrock.cyxbs.ufield.lxh.bean.DetailMsg
import com.mredrock.cyxbs.ufield.lxh.util.formatNumberToTime

class MsgAdapter : ListAdapter<DetailMsg, MsgAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<DetailMsg>() {
        override fun areItemsTheSame(
            oldItem: DetailMsg,
            newItem: DetailMsg
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DetailMsg,
            newItem: DetailMsg
        ): Boolean {
            return oldItem == newItem
        }
    }
) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mtClick: CardView
        val headImageView: ImageView
        val title: TextView
        val detail: TextView
        val time: TextView

        init {
            view.run {
                mtClick = findViewById(R.id.ufield_fragment_mc_click)
                headImageView = findViewById(R.id.ufield_fragment_iv_head)
                title = findViewById(R.id.ufield_fragment_tv_title)
                detail = findViewById(R.id.ufield_fragment_tv_detail)
                time = findViewById(R.id.ufield_fragment_tv_time)
            }
            mtClick.setOnSingleClickListener {
                getItemId(absoluteAdapterPosition).run {
                    getItemId(absoluteAdapterPosition).run {
                        val intent = Intent(view.context, DetailActivity::class.java)
                        intent.putExtra("actID", currentList[absoluteAdapterPosition].activityId)
                        ContextCompat.startActivity(view.context, intent, null)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ufield_activity_campaign_item, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.run {
            headImageView.setImageFromUrl(currentList[position].activityCoverUrl)
            title.text = currentList[position].activityTitle
            detail.text = currentList[position].activityDetail
            time.text = formatNumberToTime(currentList[position].activityEndAt)
        }
    }
}