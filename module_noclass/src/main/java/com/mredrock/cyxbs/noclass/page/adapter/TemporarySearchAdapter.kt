package com.mredrock.cyxbs.noclass.page.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.Cls
import com.mredrock.cyxbs.noclass.bean.GroupDetail
import com.mredrock.cyxbs.noclass.bean.NoClassItem
import com.mredrock.cyxbs.noclass.bean.Student

/**
 * 临时分组里面的搜索结果rv
 * 掌邮目前不让使用密封类
 */
class TemporarySearchAdapter:  ListAdapter<NoClassItem,RecyclerView.ViewHolder>(diffUtil){

    companion object{
        private val diffUtil : DiffUtil.ItemCallback<NoClassItem> = object : DiffUtil.ItemCallback<NoClassItem>(){
            override fun areItemsTheSame(oldItem: NoClassItem, newItem: NoClassItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: NoClassItem, newItem: NoClassItem): Boolean {
                return oldItem.name == newItem.name
            }
        }
        const val TEM_STUDENT = 1
        const val TEM_CLASS = 2
        const val TEM_GROUP = 3
    }


    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is Student -> TEM_STUDENT
            is Cls ->  TEM_CLASS
            is GroupDetail ->  TEM_GROUP
            else -> throw Exception("未定义的类型")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TEM_STUDENT -> StudentHolder(LayoutInflater.from(parent.context).inflate(R.layout.noclass_item_search_student,parent,false))
            TEM_CLASS -> GroupHolder(LayoutInflater.from(parent.context).inflate(R.layout.noclass_item_search_group,parent,false))
            TEM_GROUP -> ClassHolder(LayoutInflater.from(parent.context).inflate(R.layout.noclass_item_search_class,parent,false))
            else -> throw Exception("无法绑定！未定义的类型")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.apply {
            when(val itemData = getItem(position)){
                is Student -> {
                    this as StudentHolder
                    tvName.text = itemData.name
                    tvMajor.text = itemData.major
                    tvStuNum.text = itemData.id
                }
                is Cls ->  {
                    this as ClassHolder
                    tvName.text = itemData.name
                    tvNum.text = itemData.id
                }
                is GroupDetail -> {
                    this as GroupHolder
                    tvName.text = itemData.name
                }
            }
        }
    }

    //点击学生的回调
    private var onClickStudent : ((stu : Student) -> Unit)? = null

    fun setOnClickStudent(onClickStudent : (stu : Student) -> Unit){
        this.onClickStudent = onClickStudent
    }

    inner class StudentHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvName : TextView = itemView.findViewById(R.id.noclass_tv_student_name)
        val tvMajor : TextView = itemView.findViewById(R.id.noclass_tv_student_major)
        val tvStuNum : TextView = itemView.findViewById(R.id.noclass_tv_student_id)
        private val btnAdd : Button = itemView.findViewById(R.id.noclass_iv_student_add)
        init {
            btnAdd.setOnSingleClickListener {
                onClickStudent?.invoke(getItem(bindingAdapterPosition) as Student)
            }
        }
    }

    //点击group的回调
    private var onClickGroup : ((groupDetail: GroupDetail) -> Unit)? = null

    fun setOnClickGroup(onClickGroup : (groupDetail: GroupDetail) -> Unit){
        this.onClickGroup = onClickGroup
    }

    inner class GroupHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvName : TextView = itemView.findViewById(R.id.noclass_tv_group_name)
        private val btnAdd : Button = itemView.findViewById(R.id.noclass_item_iv_group_add)
        init {
            btnAdd.setOnSingleClickListener {
                onClickGroup?.invoke(getItem(bindingAdapterPosition) as GroupDetail)
            }
        }
    }

    //点击class的回调
    private var onClickClass : ((cls: Cls) -> Unit)? = null

    fun setOnClickClass(onClickClass : (cls: Cls) -> Unit){
        this.onClickClass = onClickClass
    }

    inner class ClassHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val tvNum : TextView = itemView.findViewById(R.id.noclass_item_tv_class_num)
        val tvName : TextView = itemView.findViewById(R.id.noclass_tv_student_name)
        private val btnAdd : Button = itemView.findViewById(R.id.noclass_item_iv_class_add)
        init {
            btnAdd.setOnSingleClickListener {
                onClickClass?.invoke(getItem(bindingAdapterPosition) as Cls)
            }
        }
    }

    fun submitGroup(groupList : List<GroupDetail>){
        for (group in groupList){
            val list = currentList.toMutableList()
            list.add(group)
            this@TemporarySearchAdapter.submitList(list)  //先提交一次组，然后提交组下面的成员
            this@TemporarySearchAdapter.submitList(group.members)
        }
    }

    fun deleteStudent(student: Student){
        val list = currentList.toMutableList()
        list.remove(student)
        deleteStudent(student)
    }
}