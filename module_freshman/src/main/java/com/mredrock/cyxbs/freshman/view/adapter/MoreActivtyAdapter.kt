package com.mredrock.cyxbs.freshman.view.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.FreshTextItem
import org.jetbrains.anko.find

/**
 * Create by roger
 * on 2019/8/5
 */
class MoreActivtyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private  var mOnItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TextViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TextViewHolder) {
            holder.titleView.text = data[position].title
            //设置pingfong字体
            val pingFangFonts = Typeface.createFromAsset(BaseApp.context.assets, "fonts/pf.ttf")
            holder.titleView.typeface = pingFangFonts

            holder.description.text = data[position].discript
            holder.itemView.setOnClickListener (View.OnClickListener {
                mOnItemClickListener?.onItemClick(position)
            })
        }
    }

    private fun getString(id: Int): String {
        return BaseApp.context.resources.getString(id)
    }

    private val data = listOf<FreshTextItem>(
            FreshTextItem(getString(R.string.freshman_more_zhuanti), getString(R.string.freshman_more_youni)),
            FreshTextItem(getString(R.string.freshman_more_zhangshang), getString(R.string.freshman_more_xiaoche)),
            FreshTextItem(getString(R.string.freshman_more_cyxbs), getString(R.string.freshman_more_xuezhang))
    )

    class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.find(R.id.tv_more_title)
        val description: TextView = view.find(R.id.tv_more_description)
        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.freshman_recycle_item_more, parent, false)
                return TextViewHolder(view)
            }
        }

    }

}