package com.mredrock.cyxbs.noclass.page.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.widget.SlideMenuLayout

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.adapter
 * @ClassName:      NoClassGroupAdapter
 * @Author:         Yan
 * @CreateDate:     2022年08月28日 06:57:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    没课约临时分组RV的Adapter
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
                return oldItem.id == newItem.id
            }
        }
    }

    private var mOnItemDelete : ((Student) -> Unit)? = null

    inner class VH(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvName : TextView = itemView.findViewById(R.id.noclass_tv_member_name)
        val tvId : TextView = itemView.findViewById(R.id.noclass_tv_member_id)
        val slideMenu : SlideMenuLayout = itemView.findViewById(R.id.noclass_item_group_member_slide_layout)
        init {
            itemView.findViewById<TextView?>(R.id.noclass_item_tv_delete).apply {
                setOnClickListener {
                    slideMenu.closeRightSlide()
                    val stu = getItem(bindingAdapterPosition)
                    mOnItemDelete?.invoke(stu)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.noclass_item_group_member,parent,false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.tvName.text = currentList[position].name
        holder.tvId.text = currentList[position].id
    }

    fun deleteMember(stu : Student){
        val list = currentList.toMutableList()
        list.remove(stu)
        submitList(list)
    }

    /**
     * 删除已经完成，主要是删除之后的事情
     */
    fun setOnItemDelete(listener : (Student) -> Unit){
        mOnItemDelete = listener
    }

}