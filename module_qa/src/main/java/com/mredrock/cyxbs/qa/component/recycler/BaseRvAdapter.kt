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
        * 为什么先调用的 notifyDataSetChanged(), 再修改的数据?
        *
        * 如果放在修改数据后再调用, 邮问主界面就会报错, 而先刷新再修改数就不会报错了, 我猜测原因在于有人在 Adapter 正在
        * 构建过程中就调用了这个方法, 因为 notifyDataSetChanged() 是延时的, 所以数据立马就被修改了, 导致 onBindViewHolder()
        * 回调找不到 list 之前位置的数据(notifyDataSetChanged() 是延时的, 不会立即刷新)
        *
        * 为什么数据修改放在后面仍能成功刷新?
        *
        * 原因在于 notifyDataSetChanged() 最终调用的是 requestLayout(), 这个方法有一定的延时效果, 所以紧接着再修改
        * 数据是可以的
        * @author 985892345 (不要以为这是我写的, 我只是在针对学长之前写的在优化而已, 但目前只能这样写)
        * (不行的话可以看 2021/8/30 17:31 的历史记录进行比对, 之前学长写的更..., 先删除在加(我无语了...))
        * */
        notifyDataSetChanged()
        list.clear()
        list.addAll(dataCollection)
    }


    open fun addData(dataCollection: Collection<T>) {
        list.addAll(dataCollection)
        notifyItemRangeInserted(itemCount - dataCollection.size, dataCollection.size)
    }
}