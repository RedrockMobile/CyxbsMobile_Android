package com.mredrock.cyxbs.noclass.page.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.adapter
 * @ClassName:      NoClassGroupAdapter
 * @Author:         Yan
 * @CreateDate:     2022年08月28日 06:57:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    没课约主页RV的Adapter
 */
class NoClassGroupAdapter : ListAdapter<NoclassGroup.Member,NoClassGroupAdapter.VH>(
    noClassGroupDiffUtil){

    companion object{
        private val noClassGroupDiffUtil : DiffUtil.ItemCallback<NoclassGroup.Member> = object : DiffUtil.ItemCallback<NoclassGroup.Member>(){
            override fun areItemsTheSame(
                oldItem: NoclassGroup.Member,
                newItem: NoclassGroup.Member
            ): Boolean {
                return oldItem.stuNum == newItem.stuNum
            }

            override fun areContentsTheSame(
                oldItem: NoclassGroup.Member,
                newItem: NoclassGroup.Member
            ): Boolean {
                return oldItem.stuNum == newItem.stuNum && oldItem.stuName == oldItem.stuName
            }

        }
    }

    private var mOnItemDelete : ((NoclassGroup.Member) -> Unit)? = null

    inner class VH(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvName : TextView = itemView.findViewById(R.id.noclass_tv_member_name)
        val tvId : TextView = itemView.findViewById(R.id.noclass_tv_member_id)
        val ivDelete : ImageView = itemView.findViewById<ImageView?>(R.id.noclass_iv_member_delete).apply {
            setOnClickListener {
                mOnItemDelete?.invoke(currentList[absoluteAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.noclass_item_group_member,parent,false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.tvName.text = currentList[position].stuName
        holder.tvId.text = currentList[position].stuNum
    }

    fun setOnItemDelete(listener : (NoclassGroup.Member) -> Unit){
        mOnItemDelete = listener
    }

}