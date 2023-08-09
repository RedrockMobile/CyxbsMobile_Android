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
import com.mredrock.cyxbs.noclass.bean.Student

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
class NoClassTemporaryAdapter : ListAdapter<Student,NoClassTemporaryAdapter.VH>(
    noClassGroupDiffUtil
){

    companion object{
        private val noClassGroupDiffUtil : DiffUtil.ItemCallback<Student> = object : DiffUtil.ItemCallback<Student>(){
            override fun areItemsTheSame(
                oldItem: Student,
                newItem: Student
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Student,
                newItem: Student
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    private var mOnItemDelete : ((NoclassGroup.Member) -> Unit)? = null

    inner class VH(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvName : TextView = itemView.findViewById(R.id.noclass_tv_member_name)
        val tvId : TextView = itemView.findViewById(R.id.noclass_tv_member_id)
//        val ivDelete : ImageView = itemView.findViewById<ImageView?>(R.id.noclass_iv_member_delete).apply {
//            setOnClickListener {
//                //要删除的成员
//                val member = currentList[absoluteAdapterPosition]
////                //要更新的列表
////                val toBeList = currentList.toMutableList()
////                toBeList.remove(member)
////                submitList(toBeList)
//                mOnItemDelete?.invoke(member)
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.noclass_item_group_member,parent,false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.tvName.text = currentList[position].name
        holder.tvId.text = currentList[position].id
    }
    
    fun addMember(member : Student){
        val list = currentList.toMutableList()
        list.add(member)
        submitList(list)
    }
    
    fun deleteMember(member : Student){
        val list = currentList.toMutableList()
        list.remove(member)
        submitList(list)
    }

    fun setOnItemDelete(listener : (NoclassGroup.Member) -> Unit){
        mOnItemDelete = listener
    }

}