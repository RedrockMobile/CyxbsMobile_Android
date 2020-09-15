package com.mredrock.cyxbs.discover.emptyroom.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.getScreenWidth
import com.mredrock.cyxbs.discover.emptyroom.ui.widget.MultiSelector

/**
 * Created by Cynthia on 2018/9/19
 */
class StringAdapter(selector: MultiSelector,private var mLayoutWrapper: LayoutWrapper, private var isFillUp : Boolean, private var itemNumber: Int) : MultiSelector.Adapter<String, StringAdapter.TextViewViewHolder>(selector) {
    override fun bindView(holder: TextViewViewHolder, displayValue: String, selected: Boolean, position: Int) {
        mLayoutWrapper.onBindView(holder.mTextView, displayValue, selected, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(mLayoutWrapper.layoutId, parent, false)
        if(isFillUp){
            itemView.layoutParams.width = (parent.context.getScreenWidth()-parent.context.dp2px(28f)) / itemNumber
        }
        return TextViewViewHolder(itemView, mLayoutWrapper.textViewId)
    }



    inner class TextViewViewHolder constructor(itemView: View, textViewId: Int) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val mTextView: TextView = itemView.findViewById<View>(textViewId) as TextView
    }

    abstract class LayoutWrapper {
        @get:LayoutRes
        abstract val layoutId: Int

        @get:IdRes
        abstract val textViewId: Int

        open fun onBindView(textView: TextView, displayValue: String, selected: Boolean, position: Int) {
            textView.text = displayValue
        }
    }
}