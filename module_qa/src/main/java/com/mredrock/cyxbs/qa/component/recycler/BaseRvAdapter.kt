package com.mredrock.cyxbs.qa.component.recycler

import android.view.View
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener


/**
 * Created By jay68 on 2018/8/26.
 */
abstract class BaseRvAdapter<T> : androidx.recyclerview.widget.RecyclerView.Adapter<BaseViewHolder<T>>() {
    private val list = ArrayList<T>()
    protected val dataList: List<T> = list // 禁止修改这个数组, 也不要动上面那个 list 数组, 如果要删除用 removeAtAndNotify() 方法

    /**
     * 用于删除某个位置的 item
     */
    fun removeAtAndNotify(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.refresh(list[position])
        //可能在refresh方法对dataList进行了更改，导致判空失效，再次判空
        holder.itemView.setOnSingleClickListener { onItemClickListener(holder, position, list[position]) }
        holder.itemView.setOnLongClickListener {
            onItemLongClickListener(holder, position, list[position], it)
            true
        }
    }

    /**
     * 注意不用在重写这个方法的时候使用
     * holder.itemView.setOnClickListener {}
     * 因为在onBindViewHolder已经设置了，如果再次设置会导致需要点俩次才能起到效果
     */
    protected open fun onItemClickListener(holder: BaseViewHolder<T>, position: Int, data: T) = Unit

    protected open fun onItemLongClickListener(holder: BaseViewHolder<T>, position: Int, data: T, itemView: View) = Unit

    open fun refreshData(dataCollection: Collection<T>) {
        /*
        * 先删后加, 这代码动不得, 动了就会有部分评论刷新出错, 也不能用 notifyDataSetChange() 改写, 相信我
        * @Date 2021/9/5 13:28
        * */
        notifyItemRangeRemoved(0, list.size)
        list.clear()
        list.addAll(dataCollection)
        notifyItemRangeInserted(0, list.size)
    }


    open fun addData(dataCollection: Collection<T>) {
        list.addAll(dataCollection)
        notifyItemRangeInserted(itemCount - dataCollection.size, dataCollection.size)
    }
}