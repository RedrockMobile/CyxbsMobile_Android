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
class CustomSecondTabLayoutAdapter : RecyclerView.Adapter<CustomSecondTabLayoutViewHolder>() {
    private var mTabs: List<String> = listOf()
    private var mCurrentSelectedTab: Int = 0
    private var mPreviousSelectedTab: Int = -1
    private var mListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomSecondTabLayoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.freshman_custom_second_tab_layout_tab_scrollable, parent, false)
        return CustomSecondTabLayoutViewHolder(view)
    }

    override fun getItemCount() = mTabs.size

    override fun onBindViewHolder(holder: CustomSecondTabLayoutViewHolder, position: Int) {
        holder.mText.text = mTabs[position]
        if (mCurrentSelectedTab == position) {
            holder.mText.typeface = Typeface.DEFAULT_BOLD
            holder.mText.textColorResource =
                    R.color.freshman_tab_layout_second_tab_layout_tab_selected_text_color
            mPreviousSelectedTab = position
        } else {
            holder.mText.typeface = Typeface.DEFAULT
            holder.mText.textColorResource = R.color.freshman_tab_layout_express_tab_text_color
        }
        holder.itemView.setOnClickListener {
            mCurrentSelectedTab = position
            notifyItemChanged(position)
            notifyItemChanged(mPreviousSelectedTab)
            mListener?.let { it(mCurrentSelectedTab) }
        }
    }

    fun addTabs(tabs: List<String>) {
        mTabs = tabs
        notifyDataSetChanged()
    }

    fun select(position: Int) {
        mCurrentSelectedTab = position
        notifyItemChanged(mCurrentSelectedTab)
        notifyItemChanged(mPreviousSelectedTab)
    }

    fun addOnTabSelectedListener(listener: (Int) -> Unit) {
        mListener = listener
    }
}

class CustomSecondTabLayoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mText: TextView = view.findViewById(R.id.tv_custom_second_tab_layout)
}