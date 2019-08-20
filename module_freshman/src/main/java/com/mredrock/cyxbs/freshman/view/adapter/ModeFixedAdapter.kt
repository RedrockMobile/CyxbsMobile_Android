package com.mredrock.cyxbs.freshman.view.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.freshman.R
import org.jetbrains.anko.textColorResource

/**
 * Create by yuanbing
 * on 2019/8/13
 */
class ModeFixedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mTabs: List<String> = listOf()
    private val header = 0
    private val item = 1
    private val footer = 2
    private var mCurrentSelectedItem = 0
    private var mPreviousSelectedItem = -1
    private var mListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            header -> {
                val view = LayoutInflater.from(parent.context).inflate(
                        R.layout.freshman_custom_second_tab_layout_tab_fixed_header, parent, false)
                ModeFixedHeaderViewHolder(view)
            }
            item -> {
                val view = LayoutInflater.from(parent.context).inflate(
                        R.layout.freshman_custom_second_tab_layout_tab_fixed_item, parent, false)
                ModeFixedItemViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(
                        R.layout.freshman_custom_second_tab_layout_tab_fixed_footer, parent, false)
                ModeFixedFooterViewHolder(view)
            }
        }
    }

    override fun getItemCount() = mTabs.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            header -> {
                holder as ModeFixedHeaderViewHolder
                holder.tab.text = mTabs[position]
                if (mCurrentSelectedItem == position) {
                    holder.tab.typeface = Typeface.DEFAULT_BOLD
                    holder.tab.textColorResource =
                            R.color.freshman_tab_layout_second_tab_layout_tab_selected_text_color
                    mPreviousSelectedItem = position
                } else {
                    holder.tab.typeface = Typeface.DEFAULT
                    holder.tab.textColorResource = R.color.freshman_tab_layout_express_tab_text_color
                }
            }
            item -> {
                holder as ModeFixedItemViewHolder
                holder.tab.text = mTabs[position]
                if (mCurrentSelectedItem == position) {
                    holder.tab.typeface = Typeface.DEFAULT_BOLD
                    holder.tab.textColorResource =
                            R.color.freshman_tab_layout_second_tab_layout_tab_selected_text_color
                    mPreviousSelectedItem = position
                } else {
                    holder.tab.typeface = Typeface.DEFAULT
                    holder.tab.textColorResource = R.color.freshman_tab_layout_express_tab_text_color
                }
            }
            else -> {
                holder as ModeFixedFooterViewHolder
                holder.tab.text = mTabs[position]
                if (mCurrentSelectedItem == position) {
                    holder.tab.typeface = Typeface.DEFAULT_BOLD
                    holder.tab.textColorResource =
                            R.color.freshman_tab_layout_second_tab_layout_tab_selected_text_color
                    mPreviousSelectedItem = position
                } else {
                    holder.tab.typeface = Typeface.DEFAULT
                    holder.tab.textColorResource = R.color.freshman_tab_layout_express_tab_text_color
                }
            }
        }
        holder.itemView.setOnClickListener {
            mCurrentSelectedItem = position
            notifyItemChanged(position)
            notifyItemChanged(mPreviousSelectedItem)
            mListener?.let { it(mCurrentSelectedItem) }
        }
    }

    override fun getItemViewType(position: Int) = when(position) {
        0 -> header
        itemCount - 1 -> footer
        else -> item
    }

    fun addTabs(tabs: List<String>) {
        mTabs = tabs
        notifyDataSetChanged()
    }

    fun selecte(position: Int) {
        mCurrentSelectedItem = position
        notifyItemChanged(mCurrentSelectedItem)
        notifyItemChanged(mPreviousSelectedItem)
    }

    fun addOnTabSelectedListener(listener: (Int) -> Unit) {
        mListener = listener
    }
}

class ModeFixedHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tab: TextView = view.findViewById(R.id.tv_custom_second_tab_fixed_header)
}

class ModeFixedItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tab: TextView = view.findViewById(R.id.tv_custom_second_tab_fixed_item)
}

class ModeFixedFooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tab: TextView = view.findViewById(R.id.tv_custom_second_tab_fixed_footer)
}