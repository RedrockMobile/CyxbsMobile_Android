package com.mredrock.cyxbs.noclass.page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.invisible
import com.mredrock.cyxbs.noclass.R

class NoClassAddToGroupAdapter(val groupListByPage : HashMap<Int,ArrayList<String>>,val context: Context) : RecyclerView.Adapter<NoClassAddToGroupAdapter.MyHolder>(){

    inner class MyHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val containerView = itemView.findViewById<GridLayout>(R.id.noclass_gl_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(LayoutInflater.from(parent.context).inflate(R.layout.noclass_layout_gathering,parent,false))
    }

    override fun getItemCount(): Int {
        return groupListByPage.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        groupListByPage[position]!!.forEach {
            // 每一个都是一个分组名称，一回合遍历八次
            val itemView = groupNameView(context)
            // todo 点击之后需要更改背景颜色
            val linearLayout = itemView.findViewById<LinearLayout>(R.id.noclass_ll_gathering_item_container)
            val textView = itemView.findViewById<TextView>(R.id.noclass_tv_gathering_name).apply {
                text = it
            }
            holder.containerView.addView(itemView)
        }
        // 假如说没有填满，就填入空白的，可见性为invisible,不可点击，仅起占位作用
        val needFillNum = 8 - groupListByPage[position]!!.size
        (0 until needFillNum).forEach {
            val emptyView = groupNameView(context).apply {
                invisible()
                isClickable = false
            }
            holder.containerView.addView(emptyView)
        }
    }

    /**
     * 一页中的一个view
     */
    private fun groupNameView(context : Context) : View{
        return LayoutInflater.from(context).inflate(R.layout.noclass_item_gathering,null).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                setMargins(12,14,12,14)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED,1f)
            }
        }
    }

}