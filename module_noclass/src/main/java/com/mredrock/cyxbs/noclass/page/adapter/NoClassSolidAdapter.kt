package com.mredrock.cyxbs.noclass.page.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup


/**
 * 没课约固定分组界面的adapter,全部是分组！
 */
class NoClassSolidAdapter : ListAdapter<NoclassGroup, NoClassSolidAdapter.MyHolder>(solidDiffUtil) {
    companion object {
        private val solidDiffUtil: DiffUtil.ItemCallback<NoclassGroup> =
            object : DiffUtil.ItemCallback<NoclassGroup>() {
                override fun areItemsTheSame(
                    oldItem: NoclassGroup,
                    newItem: NoclassGroup
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: NoclassGroup,
                    newItem: NoclassGroup
                ): Boolean {
                    return oldItem.isTop == newItem.isTop && oldItem.id == newItem.id && oldItem.name == oldItem.name
                }

            }
    }

    //分别是点击名称，置顶，和删除的回调
    private var onClickGroupName : ((noclassGroup: NoclassGroup) -> Unit)? = null
    private var onClickGroupIsTop : ((noclassGroup: NoclassGroup) -> Unit)? = null
    private var onClickGroupDelete : ((noclassGroup: NoclassGroup) -> Unit)? = null

    fun setOnClickGroupName(onClickGroupName : (noclassGroup: NoclassGroup) -> Unit){
        this.onClickGroupName = onClickGroupName
    }

    fun setOnClickGroupIsTop(onClickGroupIsTop : (noclassGroup: NoclassGroup) -> Unit){
        this.onClickGroupIsTop = onClickGroupIsTop
    }

    fun setOnClickGroupDelete(onClickGroupDelete : (noclassGroup: NoclassGroup) -> Unit){
        this.onClickGroupDelete = onClickGroupDelete
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.tv_noclass_group_name)
        val groupIsTop: TextView = itemView.findViewById(R.id.tv_noclass_group_top_name)
        val groupDelete: TextView = itemView.findViewById(R.id.tv_noclass_group_delete_item)
        init {
            val item = getItem(bindingAdapterPosition)
            groupName.setOnClickListener {
                onClickGroupName?.invoke(item)
            }
            groupIsTop.setOnClickListener {
                val list = currentList.toMutableList()
                if (item.isTop){
                    groupIsTop.text = "取消置顶"
                    list.remove(item)
                    //取消置顶就放到最后
                    list.add(NoclassGroup(item.id,false,item.members,item.name))  //置顶效果
                }else{
                    groupIsTop.text = "置顶"
                    list.remove(item)
                    //置顶就放到第一个
                    list.add(0, NoclassGroup(item.id,true,item.members,item.name))  //置顶效果
                }
                submitList(list)
                onClickGroupIsTop?.invoke(item)
            }
            groupDelete.setOnClickListener {
                onClickGroupDelete?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.noclass_item_group_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val item = getItem(position)
        holder.apply {
            groupName.text = item.name
            groupIsTop.text = if(item.isTop) "取消置顶" else "置顶"
        }
    }

    /**
     * 将传进来的列表变成有序列表，上面是置顶
     */
    fun submitListToOrder(noclassGroups: List<NoclassGroup>){
        val orderList = ArrayList<NoclassGroup>()
        noclassGroups.forEach {
            if (it.isTop){
                orderList.add(0,it)
            }else{
                orderList.add(it)
            }
        }
        submitList(orderList)
    }

    fun deleteGroup(noclassGroup: NoclassGroup){
        val list = currentList.toMutableList()
        list.remove(noclassGroup)
        submitList(list)
    }
}