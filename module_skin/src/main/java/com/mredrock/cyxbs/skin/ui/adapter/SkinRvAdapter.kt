package com.mredrock.cyxbs.skin.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.COURSE_SHOW_STATE
import com.mredrock.cyxbs.common.skin.SkinManager
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.skin.R
import com.mredrock.cyxbs.skin.bean.SkinInfo
import com.mredrock.cyxbs.skin.component.RoundRectImageView
import com.mredrock.cyxbs.skin.widget.SwitchPlus
import kotlinx.android.synthetic.main.skin_recycle_item.*
import kotlinx.android.synthetic.main.skin_recycle_item.view.*

class SkinRvAdapter(val context: Context, private val mList: MutableList<SkinInfo.Data>) : RecyclerView.Adapter<SkinRvAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.skin_iv_recycle_item
        val mNameTextView: TextView = view.skin_tv_name_item
        val mPriceTextView: TextView = view.skin_tv_price_item
        val switch: SwitchPlus = view.skin_switch_use_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.skin_recycle_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val skinBean = mList[position]
        holder.imageView.setImageFromUrl(skinBean.skinCover)
        holder.mNameTextView.text = skinBean.skinName
        holder.mPriceTextView.text = skinBean.skinPrice
        holder.switch.thumbDrawable = SkinManager.getDrawable("skin_selector_thumb", R.drawable.skin_selector_thumb)
        holder.switch.trackDrawable = SkinManager.getDrawable("skin_selector_switch_track", R.drawable.skin_selector_switch_track)
        holder.switch.setOnSingleClickListener {
            onItemClickListener?.onItemClick(position)
        }
        SkinManager.addSkinUpdateListener(object : SkinManager.SkinUpdateListener {
            override fun onSkinUpdate() {
                holder.switch.thumbDrawable = SkinManager.getDrawable("skin_selector_thumb", R.drawable.skin_selector_thumb)
                holder.switch.trackDrawable = SkinManager.getDrawable("skin_selector_switch_track", R.drawable.skin_selector_switch_track)
                holder.switch.isChecked = context.sharedPreferences("skin").getInt("now_skin",0) == position
            }
        })
        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            context.sharedPreferences("skin").editor {
                if (isChecked) {
                    putInt("now_skin", position)
                }
            }
        }
        holder.switch.isChecked = context.sharedPreferences("skin").getInt("now_skin",0) == position
    }


    fun notifyList(list: List<SkinInfo.Data>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}