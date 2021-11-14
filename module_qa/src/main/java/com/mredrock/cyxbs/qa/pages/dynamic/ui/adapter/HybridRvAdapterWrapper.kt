package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper

/**
 * Author: RayleighZ
 * Time: 2021-11-10 17:25
 */
class HybridRvAdapterWrapper(
    normalAdapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
    headerAdapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>? = null,
    footerAdapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>? = null,
    emptyAdapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>? = null
) :
    RvAdapterWrapper(
        normalAdapter = normalAdapter,
        headerAdapter = headerAdapter,
        footerAdapter = footerAdapter,
        emptyAdapter = emptyAdapter
    )
{
    override fun getItemViewType(position: Int): Int{
        return when {
            position < normalPositionStart -> TYPE_HEADER
            position < footerPositionStart -> normalAdapter.getItemViewType(position)
            normalAdapter.itemCount == 0 -> TYPE_EMPTY
            else -> TYPE_FOOTER
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder = when {
        viewType == TYPE_HEADER -> headerAdapter!!.onCreateViewHolder(parent, viewType)
        viewType == TYPE_NORMAL -> normalAdapter.onCreateViewHolder(parent, viewType)
        viewType == TYPE_EMPTY -> emptyAdapter!!.onCreateViewHolder(parent, viewType)
        //tip: 下面这种情况是为了将类型下发到内部的viewHolder
        viewType <= 0 -> normalAdapter.onCreateViewHolder(parent, viewType)
        else -> footerAdapter!!.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)  {
        val viewType = getItemViewType(position)
        when {
            viewType == TYPE_HEADER -> headerAdapter!!.onBindViewHolder(holder, position)
            viewType == TYPE_NORMAL -> normalAdapter.onBindViewHolder(holder, position - normalPositionStart)
            viewType == TYPE_EMPTY -> emptyAdapter!!.onBindViewHolder(holder, position - footerPositionStart)
            viewType <= 0 -> normalAdapter.onBindViewHolder(holder, position - normalPositionStart)
            else -> footerAdapter!!.onBindViewHolder(holder, position - footerPositionStart)
        }
    }
}