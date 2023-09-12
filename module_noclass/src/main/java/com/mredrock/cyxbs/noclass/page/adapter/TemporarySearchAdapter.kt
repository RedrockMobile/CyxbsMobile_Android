package com.mredrock.cyxbs.noclass.page.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.Cls
import com.mredrock.cyxbs.noclass.bean.NoClassGroup
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

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: NoClassItem, newItem: NoClassItem): Boolean {
                return oldItem == newItem
            }
        }
        const val TEM_STUDENT = 1
        const val TEM_CLASS = 2
        const val TEM_GROUP = 3
    }

    /**
     * 状态位，是否要显示头像
     */
    private var isDisplay = false

    fun setDisPlay(isDisplay : Boolean){
        this.isDisplay = isDisplay
    }


    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is Student -> TEM_STUDENT
            is Cls ->  TEM_CLASS
            is NoClassGroup ->  TEM_GROUP
            else -> throw Exception("未定义的类型")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TEM_STUDENT -> StudentHolder(LayoutInflater.from(parent.context).inflate(R.layout.noclass_item_search_student,parent,false))
            TEM_CLASS -> ClassHolder(LayoutInflater.from(parent.context).inflate(R.layout.noclass_item_search_class,parent,false))
            TEM_GROUP -> GroupHolder(LayoutInflater.from(parent.context).inflate(R.layout.noclass_item_search_group,parent,false))
            else -> throw Exception("无法绑定！未定义的类型")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.apply {
            when(val itemData = getItem(position)){
                is Student -> {
                    this as StudentHolder
                    tvName.text = itemData.name
                    tvStuNum.text = itemData.id
                    if (isDisplay){
                        imgUser.visible()
                    }
                }
                is Cls ->  {
                    this as ClassHolder
                    tvNum.text = itemData.id
                }
                is NoClassGroup -> {
                    this as GroupHolder
                    tvName.text = itemData.name
                    if (isDisplay){
                        imgUser.visible()
                    }
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
        val imgUser : ImageView = itemView.findViewById(R.id.noclass_item_img_user)
        val tvName : TextView = itemView.findViewById(R.id.noclass_tv_student_name)
        val tvStuNum : TextView = itemView.findViewById(R.id.noclass_tv_student_id)
        private val btnAdd : ImageView = itemView.findViewById(R.id.noclass_iv_student_add)
        init {
            btnAdd.setOnSingleClickListener {
                onClickStudent?.invoke(getItem(bindingAdapterPosition) as Student)
            }
        }
    }

    //点击group的回调
    private var onClickGroup : ((groupDetail: NoClassGroup) -> Unit)? = null

    fun setOnClickGroup(onClickGroup : (groupDetail: NoClassGroup) -> Unit){
        this.onClickGroup = onClickGroup
    }

    inner class GroupHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imgUser : ImageView = itemView.findViewById(R.id.noclass_item_img_group)
        val tvName : TextView = itemView.findViewById(R.id.noclass_item_tv_group_name)
        private val btnAdd : ImageView = itemView.findViewById(R.id.noclass_item_iv_group_add)
        init {
            btnAdd.setOnSingleClickListener {
                onClickGroup?.invoke(getItem(bindingAdapterPosition) as NoClassGroup)
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
        private val btnAdd : ImageView = itemView.findViewById(R.id.noclass_item_iv_class_add)
        init {
            btnAdd.setOnSingleClickListener {
                onClickClass?.invoke(getItem(bindingAdapterPosition) as Cls)
            }
        }
    }


    fun deleteStudent(student: Student){
        val list = currentList.toMutableList()
        list.remove(student)
        submitList(list)
    }
}