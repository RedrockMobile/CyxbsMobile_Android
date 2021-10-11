package com.mredrock.cyxbs.skin.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.skin.SkinManager
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.skin.R
import com.mredrock.cyxbs.skin.bean.SkinInfo
import com.mredrock.cyxbs.skin.component.DownLoadManager
import com.mredrock.cyxbs.skin.widget.SwitchPlus
import kotlinx.android.synthetic.main.skin_recycle_item.view.*

class SkinRvAdapter(val context: Context, private val mList: MutableList<SkinInfo.Data>) : RecyclerView.Adapter<SkinRvAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null
    private var switches: MutableList<SwitchPlus> = mutableListOf()

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
        if (skinBean.skinName.equals("敬请期待")) {
            holder.imageView.setImageDrawable(SkinManager.getDrawable("skin_ic_more", R.drawable.skin_ic_more))
            holder.switch.visibility = View.GONE
            holder.mNameTextView.text = skinBean.skinName
            holder.mPriceTextView.text = skinBean.skinPrice
        } else {
            holder.imageView.setImageFromUrl(skinBean.skinCover)
            holder.mNameTextView.text = skinBean.skinName
            holder.mPriceTextView.text = skinBean.skinPrice
            switches.add(holder.switch)
            holder.switch.thumbDrawable = SkinManager.getDrawable("skin_selector_thumb", R.drawable.skin_selector_thumb)
            holder.switch.trackDrawable = SkinManager.getDrawable("skin_selector_switch_track", R.drawable.skin_selector_switch_track)
            holder.switch.isChecked = context.sharedPreferences("skin").getInt("now_skin", -1) == position
            SkinManager.addSkinUpdateListener(object : SkinManager.SkinUpdateListener {
                override fun onSkinUpdate() {
                    holder.switch.thumbDrawable = SkinManager.getDrawable("skin_selector_thumb", R.drawable.skin_selector_thumb)
                    holder.switch.trackDrawable = SkinManager.getDrawable("skin_selector_switch_track", R.drawable.skin_selector_switch_track)
                    holder.switch.invalidate()
                }
            })
            holder.switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    context.sharedPreferences("skin").editor {
                        putInt("now_skin", position)
                    }
                    var fileName = ""
                    when (skinBean.skinName) {
                        "国庆专题" -> {
                            fileName = DownLoadManager.allSkinName[DownLoadManager.NATIONAL_DAY]
                                    ?: ""
                        }
                    }
                    onItemClickListener?.onItemClick(position, skinBean.skinDownload
                            ?: "", fileName)
                    notifySwitches(position)
                } else {
                    if (context.sharedPreferences("skin").getInt("now_skin", -1) == position)
                    {
                        context.sharedPreferences("skin").editor {
                            putInt("now_skin", -1)
                        }
                        onItemClickListener?.onSwitchUnChecked()
                    }

                }
            }

        }
    }

    private fun notifySwitches(position: Int) {
        switches.forEachIndexed { index, switchPlus ->
            if (index != position) {
                switchPlus.isChecked = false
            }
        }
    }

    fun notifyList(list: List<SkinInfo.Data>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, url: String, fileName: String)
        fun onSwitchUnChecked()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}