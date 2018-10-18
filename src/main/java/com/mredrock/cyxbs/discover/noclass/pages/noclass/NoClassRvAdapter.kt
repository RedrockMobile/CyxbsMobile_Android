package com.mredrock.cyxbs.discover.noclass.pages.noclass

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.discover.noclass.R
import com.mredrock.cyxbs.discover.noclass.network.Student
import kotlinx.android.synthetic.main.discover_noclass_item_stu.view.*
import org.jetbrains.anko.toast

/**
 * Created by zxzhu
 *   2018/9/10.
 *   enjoy it !!
 */
class NoClassRvAdapter(private val mStuList: MutableList<Student>, private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_ADD = 0
    private val TYPE_STU = 1
    private var helper: PopupWindowHelper? = null

    init {
        helper = PopupWindowHelper(mContext)
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemCount == 1 || position == mStuList.size) {
            TYPE_ADD
        } else {
            TYPE_STU
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            TYPE_STU -> {
                val viewStu = LayoutInflater.from(parent.context).inflate(R.layout.discover_noclass_item_stu, parent, false)
                ItemStuHolder(viewStu)
            }
            else -> {
                val viewAdd = LayoutInflater.from(parent.context).inflate(R.layout.discover_noclass_item_add, parent, false)
                ItemAddHolder(viewAdd)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_STU -> {
                holder.itemView.no_course_name.text = mStuList[position].name
                holder.itemView.no_course_delete.setOnClickListener { removeStu(holder) }
            }
            TYPE_ADD -> {
                holder.itemView.setOnClickListener { showSearchDialog() }
            }
        }
    }

    override fun getItemCount() = mStuList.size + 1

    private fun removeStu(holder: RecyclerView.ViewHolder) {
        holder.itemView.no_course_delete.setOnClickListener(null)
        mStuList.removeAt(holder.adapterPosition)
        notifyItemRemoved(holder.adapterPosition)
    }

    fun addStu(stu: Student) {
        for (s in mStuList) {
            if (s.stunum == stu.stunum) {
                mContext.toast("请勿重复添加")
                return
            }
        }
        mStuList.add(stu)
        notifyItemInserted(mStuList.size - 1)
    }

    private fun showSearchDialog() {
//        val stu = Student()
//        addStu(stu)
        helper!!.showPopup()
    }


    fun getStuList(): List<Student> = mStuList

    class ItemStuHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

    class ItemAddHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)


}