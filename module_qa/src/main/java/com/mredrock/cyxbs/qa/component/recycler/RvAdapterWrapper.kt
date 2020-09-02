package com.mredrock.cyxbs.qa.component.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created By jay68 on 2018/8/26.
 */
class RvAdapterWrapper(normalAdapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
                       headerAdapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>? = null,
                       footerAdapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>? = null,
                       emptyAdapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_FOOTER = 1
        const val TYPE_NORMAL = 2
        const val TYPE_EMPTY = 3
    }

    //谁有好的解决方法改改吧……
    val normalAdapter = normalAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
    val headerAdapter = headerAdapter as? RecyclerView.Adapter<RecyclerView.ViewHolder>
    val footerAdapter = footerAdapter as? RecyclerView.Adapter<RecyclerView.ViewHolder>
    val emptyAdapter = emptyAdapter as? RecyclerView.Adapter<RecyclerView.ViewHolder>

    init {
        normalAdapter.registerAdapterDataObserver(AdapterDataObserver(TYPE_NORMAL))
        headerAdapter?.registerAdapterDataObserver(AdapterDataObserver(TYPE_HEADER))
        footerAdapter?.registerAdapterDataObserver(AdapterDataObserver(TYPE_FOOTER))
        emptyAdapter?.registerAdapterDataObserver(AdapterDataObserver(TYPE_EMPTY))
    }

    private val normalPositionStart get() = headerAdapter.getItemCountOrDefault()
    private val footerPositionStart get() = normalPositionStart + normalAdapter.itemCount

    private fun RecyclerView.Adapter<out RecyclerView.ViewHolder>?.getItemCountOrDefault(default: Int = 0) = this?.itemCount
            ?: default

    override fun getItemViewType(position: Int) = when {
        position < normalPositionStart -> TYPE_HEADER
        position < footerPositionStart -> TYPE_NORMAL
        normalAdapter.itemCount == 0 -> TYPE_EMPTY
        else -> TYPE_FOOTER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_HEADER -> headerAdapter!!.onCreateViewHolder(parent, viewType)
        TYPE_NORMAL -> normalAdapter.onCreateViewHolder(parent, viewType)
        TYPE_EMPTY -> emptyAdapter!!.onCreateViewHolder(parent, viewType)
        else -> footerAdapter!!.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (getItemViewType(position)) {
        TYPE_HEADER -> headerAdapter!!.onBindViewHolder(holder, position)
        TYPE_NORMAL -> normalAdapter.onBindViewHolder(holder, position - normalPositionStart)
        TYPE_EMPTY -> emptyAdapter!!.onBindViewHolder(holder, position - footerPositionStart)
        else -> footerAdapter!!.onBindViewHolder(holder, position - footerPositionStart)
    }

    override fun getItemCount(): Int {
        var count = footerPositionStart
        count += if (normalAdapter.itemCount != 0) {
            footerAdapter.getItemCountOrDefault()
        } else {
            emptyAdapter.getItemCountOrDefault()
        }
        return count
    }

    private inner class AdapterDataObserver(val type: Int) : RecyclerView.AdapterDataObserver() {
        override fun onChanged() = when (type) {
            TYPE_HEADER -> notifyItemRangeChanged(0, normalPositionStart)
            TYPE_NORMAL -> {
                if (normalAdapter.itemCount > 0) {
                    emptyAdapter?.notifyItemRangeRemoved(0, emptyAdapter.itemCount)
                } else {
                    emptyAdapter?.notifyItemRangeInserted(0, emptyAdapter.itemCount)
                }
                notifyItemRangeChanged(normalPositionStart, normalAdapter.itemCount)
            }
            TYPE_FOOTER -> notifyItemRangeChanged(footerPositionStart, footerAdapter.getItemCountOrDefault())
            else -> notifyItemRangeChanged(footerPositionStart, emptyAdapter.getItemCountOrDefault())
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = when (type) {
            TYPE_HEADER -> notifyItemRangeRemoved(positionStart, itemCount)
            TYPE_NORMAL -> {
                if (normalAdapter.itemCount <= 0) {
                    emptyAdapter?.notifyItemRangeInserted(0, emptyAdapter.getItemCountOrDefault())
                }
                notifyItemRangeRemoved(normalPositionStart + positionStart, itemCount)
            }
            TYPE_FOOTER -> notifyItemRangeRemoved(footerPositionStart + positionStart, itemCount)
            else -> {
                notifyItemRangeRemoved(footerPositionStart + positionStart, itemCount)
                footerAdapter?.notifyItemRangeInserted(0, footerAdapter.getItemCountOrDefault())
                        ?: Unit
            }
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) = when (type) {
            TYPE_HEADER -> notifyItemMoved(fromPosition, toPosition)
            TYPE_NORMAL -> notifyItemMoved(fromPosition + normalPositionStart, toPosition + normalPositionStart)
            TYPE_FOOTER -> notifyItemMoved(fromPosition + footerPositionStart, toPosition + footerPositionStart)
            else -> notifyItemMoved(fromPosition + footerPositionStart, toPosition + footerPositionStart)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = when (type) {
            TYPE_HEADER -> notifyItemRangeInserted(positionStart, itemCount)
            TYPE_NORMAL -> {
                if (normalAdapter.itemCount > 0) {
                    emptyAdapter?.notifyItemRangeRemoved(0, emptyAdapter.getItemCountOrDefault())
                }
                notifyItemRangeInserted(normalPositionStart + positionStart, itemCount)
            }

            TYPE_FOOTER -> notifyItemRangeInserted(footerPositionStart + positionStart, itemCount)

            else -> {
                footerAdapter?.notifyItemRangeRemoved(0, footerAdapter.getItemCountOrDefault())
                notifyItemRangeInserted(footerPositionStart + positionStart, itemCount)
            }
        }


        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) = when (type) {
            TYPE_HEADER -> notifyItemRangeChanged(positionStart, itemCount)
            TYPE_NORMAL -> notifyItemRangeChanged(normalPositionStart + positionStart, itemCount)
            TYPE_FOOTER -> notifyItemRangeChanged(footerPositionStart + positionStart, itemCount)
            else -> notifyItemRangeChanged(footerPositionStart + positionStart, itemCount)
        }
    }
}