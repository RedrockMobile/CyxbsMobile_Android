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
 * 没课约固定分组界面的adapter
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
                    return oldItem == newItem
                }
            }
    }

    //分别是点击名称，置顶，和删除的回调
    private var onClickGroupName : ((noclassGroup: NoclassGroup) -> Unit)? = null
    private var onClickGroupIsTop : ((noclassGroup: NoclassGroup,tvIsGroup : TextView) -> Unit)? = null
    private var onClickGroupDelete : ((noclassGroup: NoclassGroup) -> Unit)? = null

    fun setOnClickGroupName(onClickGroupName : (noclassGroup: NoclassGroup) -> Unit){
        this.onClickGroupName = onClickGroupName
    }

    fun setOnClickGroupIsTop(onClickGroupIsTop : (noclassGroup: NoclassGroup,tvIsGroup : TextView) -> Unit){
        this.onClickGroupIsTop = onClickGroupIsTop
    }

    fun setOnClickGroupDelete(onClickGroupDelete : (noclassGroup: NoclassGroup) -> Unit){
        this.onClickGroupDelete = onClickGroupDelete
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGroupName: TextView = itemView.findViewById(R.id.tv_noclass_group_name)
        val tvGroupIsTop: TextView = itemView.findViewById(R.id.tv_noclass_group_top_name)
        val tvGroupDelete: TextView = itemView.findViewById(R.id.tv_noclass_group_delete_item)
        init {
            tvGroupName.setOnClickListener {
                val item = getItem(bindingAdapterPosition)
                onClickGroupName?.invoke(item)
            }
            tvGroupIsTop.setOnClickListener {
                val item = getItem(bindingAdapterPosition)
                onClickGroupIsTop?.invoke(item,tvGroupIsTop)
            }
            tvGroupDelete.setOnClickListener {
                val item = getItem(bindingAdapterPosition)
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
            tvGroupName.text = item.name
            tvGroupIsTop.text = if(item.isTop) "取消置顶" else "置顶"
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

    /**
     * 远端更新成功之后就调用，将该条数据按照是否有序插入并且提交，相比于submitListToOrder减少了比较
     */
    fun addItemToOrder(item : NoclassGroup,tvIsGroup : TextView){
        val list = currentList.toMutableList()
        if (item.isTop){
            tvIsGroup.text = "取消置顶"
            list.remove(item)
            //取消置顶就放到最后
            list.add(NoclassGroup(item.id,false,item.members,item.name))  //置顶效果
        }else{
            tvIsGroup.text = "置顶"
            list.remove(item)
            //置顶就放到第一个
            list.add(0, NoclassGroup(item.id,true,item.members,item.name))  //置顶效果
        }
        submitList(list)
    }
}