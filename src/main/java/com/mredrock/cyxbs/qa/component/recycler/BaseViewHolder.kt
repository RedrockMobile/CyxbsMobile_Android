package com.mredrock.cyxbs.qa.component.recycler

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created By jay68 on 2018/8/26.
 */
abstract class BaseViewHolder<T>(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup, @LayoutRes layout: Int) : this(
            LayoutInflater.from(parent.context).inflate(layout, parent, false))

    protected val context: Context = itemView.context

    abstract fun refresh(data: T?)
}